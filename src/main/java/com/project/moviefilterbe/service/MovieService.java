package com.project.moviefilterbe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.project.moviefilterbe.domain.entity.MovieInfo;
import com.project.moviefilterbe.domain.entity.MoviePicture;
import com.project.moviefilterbe.domain.entity.MovieScore;
import com.project.moviefilterbe.domain.repository.MovieInfoRepository;
import com.project.moviefilterbe.domain.repository.MoviePictureRepository;
import com.project.moviefilterbe.domain.repository.MovieScoreRepository;
import com.project.moviefilterbe.service.api.MovieExternalApiService;
import com.project.moviefilterbe.web.dto.TestRequestDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbSearchListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieExternalApiService movieApiService;
    private final MovieInfoRepository infoRepository;
    private final MoviePictureRepository pictureRepository;
    private final MovieScoreRepository scoreRepository;

    @Transactional
    public void recommendAndSave(List<TestRequestDto> options) {
        System.out.println("======= [DEBUG] 서비스 로직 시작! =======");

        // 1. 옵션 파싱 (프론트에서 보낸 type에 맞춰서 추출)
        String people = options.stream().filter(o -> "P".equals(o.getType())).map(TestRequestDto::getTitle).findFirst().orElse("상관없음");
        String motion = options.stream().filter(o -> "M".equals(o.getType())).map(TestRequestDto::getTitle).findFirst().orElse("상관없음");
        String genre = options.stream().filter(o -> "G".equals(o.getType())).map(TestRequestDto::getTitle).findFirst().orElse("모든 장르");

        System.out.println("파싱된 옵션 - 인원: " + people + ", 감정: " + motion + ", 장르: " + genre);

        // 2. Gemini 호출 시 파싱한 변수들 전달
        List<String> recommendedTitles = movieApiService.geminiSearchMovies(people, motion, genre);
        System.out.println("Gemini 추천 결과: " + recommendedTitles);

        if (recommendedTitles.isEmpty()) {
            System.out.println("Gemini가 영화를 하나도 추천하지 않았습니다.");
            return;
        }

        for (String rawTitle : recommendedTitles) {
            try {
                // 3. TMDB 검색 (ID 확보)
                TmdbSearchListDto basic = movieApiService.tmdbSearchExactMovie(rawTitle);

                if (basic != null && basic.getTmdbId() != null) {
                    // 이미 DB에 있는지 확인 (중복 저장 방지)
//                    if (infoRepository.existsById(basic.getTmdbId())) {
//                        log.info("이미 존재하는 영화 스킵: {}", basic.getTitle());
//                        continue;
//                    }

                    // 4. TMDB 상세 정보 확보 (Runtime, Cast 등)
                    Map<String, Object> details = movieApiService.getTmdbMovieDetails(basic.getTmdbId());

                    if (details != null) {
                        saveMovieData(basic, details);
                        log.info("영화 저장 완료: {}", basic.getTitle());
                    }
                }
            } catch (Exception e) {
                log.error("영화 처리 중 오류 발생 ({}): {}", rawTitle, e.getMessage());
            }
        }
    }

    @Transactional // 개별 영화 단위로 트랜잭션을 분리하여 에러 시 전체 롤백 방지
    public void saveMovieData(TmdbSearchListDto basic, Map<String, Object> details) throws JsonProcessingException {
        // 1. 공통 ID 준비
        String commonMovieId = "mi_" + String.valueOf(basic.getTmdbId());

        // 2. 기존 영화 존재 여부 확인 (중복 체크)
        Optional<MovieInfo> existingMovie = infoRepository.findById(commonMovieId);

        if (existingMovie.isPresent()) {
            // [1] 중복된 경우: 카운트만 증가
            MovieInfo info = existingMovie.get();
            long currentCount = (info.getMiCreatedCount() != null) ? info.getMiCreatedCount() : 1L;
            info.setMiCreatedCount(currentCount + 1);

            infoRepository.save(info);
            log.info("======= [SKIP] 이미 존재하는 영화 카운트 증가: {} (현재 {}회) =======", info.getMiTitle(), info.getMiCreatedCount());
        } else {
            // [신규 시] 전체 데이터 보강하여 저장 시작
            LocalDateTime now = LocalDateTime.now();
            String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 데이터 추출 및 안전한 형변환
            List<Map<String, Object>> genres = (List<Map<String, Object>>) details.get("genres");
            String genreNames = (genres != null) ? genres.stream()
                    .map(g -> String.valueOf(g.get("name")))
                    .collect(Collectors.joining(", ")) : "기타";

            Integer runtime = (details.get("runtime") != null) ? (Integer) details.get("runtime") : 0;

            Object voteAvg = details.get("vote_average");
            String voteStr = (voteAvg != null) ? String.valueOf(voteAvg) : "0.0";
            Double voteDouble = (voteAvg != null) ? Double.valueOf(voteAvg.toString()) : 0.0;

            String imdbId = String.valueOf(details.get("imdb_id"));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode releaseDates = objectMapper.convertValue(details.get("release_dates"), JsonNode.class);
            JsonNode jsonResults = releaseDates.path("results");

            String ratingStr = "";
            JsonNode ratingRaw = MissingNode.getInstance();
            if(jsonResults.isArray()) {
                for(JsonNode jsonData : jsonResults) {
                    String isoCode = jsonData.path("iso_3166_1").asText();
                    if(isoCode.equals("KR")) {
                        ratingRaw = jsonData.path("release_dates");
                        break;
                    }
                }
            }
            if(ratingRaw.isArray() && !ratingRaw.isEmpty()) {
                for(JsonNode jsonData : ratingRaw) {
                    ratingStr = jsonData.path("certification").asText();
                    break;
                }
            }

//            String ratingRaw = (releaseDates != null) ? releaseDates.get(results) : "0.0";

            // iso_3166_1
//            String ratingStr = (ratingRaw != null) ? String.valueOf(ratingRaw) : "N";

            double popularity = 0.0;
            if (details.get("popularity") instanceof Number) {
                popularity = ((Number) details.get("popularity")).doubleValue();
            }

            // 출연진 및 제작진 추출
            Map<String, Object> credits = (Map<String, Object>) details.get("credits");
            String castNames = "";
            String crewNames = "";

            if (credits != null) {
                List<Map<String, Object>> castList = (List<Map<String, Object>>) credits.get("cast");
                if (castList != null) {
                    castNames = castList.stream()
                            .limit(10)
                            .map(c -> String.valueOf(c.get("name")))
                            .collect(Collectors.joining(", "));
                }
                List<Map<String, Object>> crewList = (List<Map<String, Object>>) credits.get("crew");
                if (crewList != null) {
                    crewNames = crewList.stream()
                            .filter(c -> "Director".equals(c.get("job")))
                            .map(c -> String.valueOf(c.get("name")))
                            .collect(Collectors.joining(", "));
                }
            }

            Map<String, Object> watchProviders = (Map<String, Object>) details.get("watch/providers");
            String providerNames = ""; // 기본값은 빈 문자열

            if (watchProviders != null) {
                Map<String, Object> results = (Map<String, Object>) watchProviders.get("results");
                if (results != null && results.containsKey("KR")) {
                    Map<String, Object> krData = (Map<String, Object>) results.get("KR");

                    // OTT 리스트 가져오기 (가장 일반적인 OTT 플랫폼)
                    List<Map<String, Object>> flatrate = (List<Map<String, Object>>) krData.get("flatrate");

                    if (flatrate != null && !flatrate.isEmpty()) {
                        providerNames = flatrate.stream()
                                .map(f -> String.valueOf(f.get("provider_name"))) // 플랫폼 명칭 추출
                                .distinct() // 혹시 모를 중복 제거
                                .collect(Collectors.joining(",")); // 콤마(,)로 구분하여 결합
                    }
                }
            }

            // 만약 한국 제공 정보가 아예 없다면 "정보 없음" 또는 빈 값 처리
            if (providerNames.isEmpty()) {
                providerNames = "정보 없음";
            }

            // [1] MovieInfo 저장 (신규)
            MovieInfo newInfo = MovieInfo.builder()
                    .miId(commonMovieId)
                    .miTitle(basic.getTitle())
                    .miSummary(basic.getOverview() != null ? basic.getOverview() : "정보 없음")
                    .miReleaseDate(basic.getReleaseDate())
                    .miRuntime(runtime)
                    .miGenre(genreNames)
                    .miRating(ratingStr)
                    .miPopularity(popularity)
                    .miCast(castNames)
                    .miCrew(crewNames)
                    .miProvider(providerNames)
                    .miCreatedDate(formattedNow)
                    .miCreatedCount(1L) // 신규 저장이므로 1부터 시작
                    .miWishlistCount(0L)
                    .miImdbId(imdbId)
                    .build();
            infoRepository.save(newInfo);

            // [2] MoviePicture 저장
            MoviePicture picture = MoviePicture.builder()
                    .mpId("mp_" + UUID.randomUUID().toString())
                    .miId(commonMovieId)
                    .mpPoster(basic.getPosterPath())
                    .mpBackdrop(basic.getBackdropPath())
                    .mpType("")
                    .mpAlt(basic.getTitle() + "_image")
                    .mpCreatedDate(now)
                    .build();
            pictureRepository.save(picture);

            // [3] MovieScore 저장
            MovieScore score = MovieScore.builder()
                    .msId("ms_" + UUID.randomUUID().toString())
                    .miId(commonMovieId)
                    .msTitle(basic.getTitle())
                    .msScoreRating(voteDouble)
                    .msCreatedDate(now)
                    .build();
            scoreRepository.save(score);

            log.info("======= [NEW] 새로운 영화 저장 완료: {} =======", basic.getTitle());
        }
    }
}