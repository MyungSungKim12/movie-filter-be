package com.project.moviefilterbe.service.app;

import com.project.moviefilterbe.domain.entity.MovieInfo;
import com.project.moviefilterbe.domain.entity.MovieLog;
import com.project.moviefilterbe.domain.entity.MoviePicture;
import com.project.moviefilterbe.domain.entity.MovieScore;
import com.project.moviefilterbe.domain.repository.MovieInfoRepository;
import com.project.moviefilterbe.domain.repository.MovieLogRepository;
import com.project.moviefilterbe.domain.repository.MoviePictureRepository;
import com.project.moviefilterbe.domain.repository.MovieScoreRepository;
import com.project.moviefilterbe.service.api.GeminiApiService;
import com.project.moviefilterbe.service.api.OmdbApiService;
import com.project.moviefilterbe.service.api.TmdbApiService;
import com.project.moviefilterbe.util.CommonUtil;
import com.project.moviefilterbe.util.TmdbApiUtil;
import com.project.moviefilterbe.web.dto.gemini.GeminiJsonDto;
import com.project.moviefilterbe.web.dto.movie.MovieRecommendRequestDto;
import com.project.moviefilterbe.web.dto.omdb.OmdbSearchResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbDetailResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final GeminiApiService geminiApiService;
    private final TmdbApiService tmdbApiService;
    private final OmdbApiService omdbApiService;

    private final MovieInfoRepository movieInfoRepository;
    private final MoviePictureRepository moviePictureRepository;
    private final MovieScoreRepository movieScoreRepository;
    private final MovieLogRepository movieLogRepository;

    @Transactional
    public String recommendMovieService(MovieRecommendRequestDto requestDto) {
        long geminiStartTime = System.currentTimeMillis();
        List<GeminiJsonDto> geminiJsonList = geminiApiService.geminiSearchMovies(requestDto);
        long geminiEndTime = System.currentTimeMillis();
        if (geminiJsonList == null || geminiJsonList.isEmpty()) {
            log.error("[Gemini] 영화 추천 응답 오류: {}", geminiJsonList);
            return "NONE";
        }
        log.info("[Gemini] 실행 시간: {}s", ((geminiEndTime - geminiStartTime)/1000.0));

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(geminiJsonList.size(), 10), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        try {
            long tmdbStartTime = System.currentTimeMillis();
            List<CompletableFuture<TmdbDetailResponseDto>> futures = geminiJsonList.stream()
                    .map(title -> CompletableFuture.supplyAsync(() -> {
                        log.info("{} 가 '{}' 처리 중..." , Thread.currentThread().getName(), title);
                        return tmdbApiService.searchTmdbMovieInfo(title);
                    }, executor))
                    .collect(Collectors.toList());

            List<TmdbDetailResponseDto> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            long tmdbEndTime = System.currentTimeMillis();
            log.info("[TMDB] 실행 시간: {}s", ((tmdbEndTime - tmdbStartTime)/1000.0));

            long supabaseStartTime = System.currentTimeMillis();
            boolean saveResult = recommendMovieSave(results);

            if(saveResult) {
                List<String> recommendTmdbIdList = results.stream()
                        .map(dto -> "mi_" + dto.getTmdbId())
                        .collect(Collectors.toList());
                Map<String, List<String>> recommendOptionList = new HashMap<>();

                for(MovieRecommendRequestDto.Option option : requestDto.getOption()) {
                    recommendOptionList.computeIfAbsent(option.getType(), k -> new ArrayList<>()).add(option.getTitle());
                }
                String id = recommendMovieLogSave(requestDto.getUserId(), recommendTmdbIdList, recommendOptionList);
                long supabaseEndTime = System.currentTimeMillis();
                log.info("[Supabase] 실행 시간: {}s", ((supabaseEndTime - supabaseStartTime)/1000.0));
                return id;
            } else {
                return "NONE";
            }
        } finally {
            executor.shutdown();
        }
    }


    @Transactional
    public boolean recommendMovieSave(List<TmdbDetailResponseDto> results) {
        if (results == null || results.isEmpty()) {
            log.error("영화 추천 리스트 응답 오류: {}", results);
            return false;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(results.size(), 10), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        try {
            List<String> tmdbIdList = results.stream()
                    .map(dto -> "mi_" + dto.getTmdbId())
                    .collect(Collectors.toList());

            List<MovieInfo> existingMovieInfoList = movieInfoRepository.findAllByMiIdIn(tmdbIdList);

            Map<String, MovieInfo> movieMap = existingMovieInfoList.stream().collect(Collectors.toMap(MovieInfo::getMiId, m -> m));

            List<MovieInfo> batchMovieInfo = new ArrayList<>();
            List<MoviePicture> batchMoviePicture = new ArrayList<>();
            List<MovieScore> batchMovieScore = new ArrayList<>();

            List<CompletableFuture<OmdbSearchResponseDto>> futures = results.stream()
                    .map(omdb -> CompletableFuture.supplyAsync(() -> {
                        log.info("{} 가 '{}' 처리 중..." , Thread.currentThread().getName(), omdb.getTitle());
                        return omdbApiService.getMovieDetails(omdb.getImdbId());
                    }, executor))
                    .collect(Collectors.toList());

            List<OmdbSearchResponseDto> omdbDetails = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            for (TmdbDetailResponseDto dto : results) {
                String miId = "mi_" + dto.getTmdbId();
                OmdbSearchResponseDto omdbSearchResponseDto = omdbDetails.stream()
                        .filter(omdb -> omdb.getImdbId().equals(dto.getImdbId()))
                        .findFirst() // 첫 번째로 발견된 것 반환
                        .orElse(null);
                Map<String, String> extractScore = TmdbApiUtil.getExtractScore(omdbSearchResponseDto);

                if (movieMap.containsKey(miId)) {
                    MovieInfo movieInfo = movieMap.get(miId);
                    movieInfo.movieInfoUpdate(dto.getOtt());
                    MovieScore movieScore = movieScoreRepository.findById("ms_" + movieInfo.getMiImdbId())
                            .orElseThrow(() -> new EntityNotFoundException("해당 정보를 찾을 수 없습니다. : ms_" + movieInfo.getMiImdbId()));
                    movieScore.movieScoreUpdate(BigDecimal.valueOf(dto.getTmdbScore()), BigDecimal.valueOf(Double.parseDouble(extractScore.get("imdb").split("/")[0])),
                            Integer.parseInt(extractScore.get("meta").split("/")[0]), Integer.parseInt(extractScore.get("tomato").split("%")[0]));
                } else {
                    MovieInfo movieInfo = MovieInfo.builder()
                            .miId(miId)
                            .miTitle(dto.getTitle().length() > 0 ? dto.getTitle() : "정보 없음")
                            .miSummary(dto.getSummary())
                            .miReleaseDate(dto.getReleaseDate())
                            .miRuntime(dto.getRuntime())
                            .miGenre(dto.getGenre())
                            .miRating(dto.getRating())
                            .miPopularity(dto.getPopularity())
                            .miCast(dto.getCast())
                            .miCrew(dto.getCrew())
                            .miProvider(dto.getOtt())
                            .miImdbId(dto.getImdbId())
                            .miCreatedDate(CommonUtil.getDateTimeNow())
                            .miUpdatedDate(CommonUtil.getDateTimeNow())
                            .miCreatedCount(1L)
                            .miWishlistCount(0L)
                            .build();

                    MoviePicture moviePicture = MoviePicture.builder()
                            .mpId(CommonUtil.getGenerateId("mp"))
                            .miId(miId)
                            .mpPoster(dto.getPosterImagePath())
                            .mpBackdrop(dto.getBackdropImagePath())
                            .mpType("")
                            .mpAlt(dto.getTitle() + "_image")
                            .mpCreatedDate(CommonUtil.getDateTimeNow())
                            .build();

                    MovieScore movieScore = MovieScore.builder()
                            .msId("ms_" + dto.getImdbId())
                            .miId(miId)
                            .msTitle(omdbSearchResponseDto.getTitle())
                            .msYear(omdbSearchResponseDto.getYear())
                            .msTmdbScore(BigDecimal.valueOf(dto.getTmdbScore()))
                            .msImdbScore(BigDecimal.valueOf(Double.parseDouble(extractScore.get("imdb").split("/")[0])))
                            .msMetaScore(Integer.parseInt(extractScore.get("meta").split("/")[0]))
                            .msTomatoScore(Integer.parseInt(extractScore.get("tomato").split("%")[0]))
                            .msCreatedDate(CommonUtil.getDateTimeNow())
                            .build();

                    batchMovieInfo.add(movieInfo);
                    batchMoviePicture.add(moviePicture);
                    batchMovieScore.add(movieScore);
                }
            }

            if(batchMovieInfo.size() > 0 && batchMoviePicture.size() > 0 && batchMovieScore.size() > 0) {
                movieInfoRepository.saveAll(batchMovieInfo);
                moviePictureRepository.saveAll(batchMoviePicture);
                movieScoreRepository.saveAll(batchMovieScore);
            }
            return true;
        } catch (Exception e) {
            log.error("[Supabase] 알수없는 에러 ({}): ", e.getMessage());
            return false;
        } finally {
            executor.shutdown();
        }
    }

    @Transactional
    public String recommendMovieLogSave(String userId, List<String> tmdbIdList, Map<String, List<String>> optionList) {
        try {
            MovieLog movieLog = MovieLog.builder()
                    .mlId(CommonUtil.getGenerateId("ml"))
                    .uiId(userId)
                    .mlMovieList(tmdbIdList)
                    .mlOptionList(optionList)
                    .mlCreatedDate(CommonUtil.getDateTimeNow())
                    .mlExpiresDate(CommonUtil.getDateTimePlusDay(7))
                    .build();
            MovieLog savedLog = movieLogRepository.save(movieLog);

            return savedLog.getMlId();
        } catch (Exception e) {
            log.error("[Supabase] 알수없는 에러 ({}): ", e.getMessage());
            return "NONE";
        }
    }
}