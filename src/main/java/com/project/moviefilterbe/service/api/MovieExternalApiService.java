package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.web.dto.gemini.GeminiResponseDto;
import com.project.moviefilterbe.web.dto.omdb.OmdbSearchListDto;
import com.project.moviefilterbe.web.dto.omdb.OmdbSearchResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbSearchListDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbSearchResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class MovieExternalApiService {
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    @Value("${omdb.api.key}")
    private String omdbApiKey;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<TmdbSearchListDto> tmdbSearchMovies(String title) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.themoviedb.org/3/search/movie")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "ko-KR")
                .queryParam("query", title)
                .build()    // 1차 빌드
                .encode()   // 인코딩 수행
                .toUri();
        System.out.println(restTemplate.getForObject(uri, TmdbSearchResponseDto.class));

        TmdbSearchResponseDto response = restTemplate.getForObject(uri, TmdbSearchResponseDto.class);

        if (response.getResults() != null) {
            return response.getResults();
        }
        return List.of();
    }

//    public List<OmdbSearchListDto> omdbSearchMovies(String title) {
//        URI uri = UriComponentsBuilder
//                .fromUriString("http://www.omdbapi.com/")
//                .queryParam("apikey", omdbApiKey)
//                .queryParam("s", title) // Search 파라미터
//                .build()
//                .encode()
//                .toUri();
//        System.out.println(restTemplate.getForObject(uri, OmdbSearchResponseDto.class));
//
//        OmdbSearchResponseDto response = restTemplate.getForObject(uri, OmdbSearchResponseDto.class);
//
//        if ("True".equalsIgnoreCase(response.getResponse())) {
//            return response.getSearch();
//        }
//
//        return List.of();
//    }

    public List<String> geminiSearchMovies(String people, String motion, String genre) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);
        // ai prompt 수정 - 20260211 ms
        String promptText = String.format(
                "너는 TMDB 데이터베이스 기반의 영화 추천 전문가야. 이번 추천의 핵심은 **'장르의 완벽한 일치'**야.\n\n" +
                        "[최우선 지시사항]\n" +
                        "사용자가 선택한 장르가 **'%s'**라면, 너는 반드시 **오직 해당 카테고리에 속하는 콘텐츠만** 추천해야 해.\n" +
                        "- 특히 장르가 '애니' 또는 '애니메이션'인 경우, **실사 영화(Live-action)는 단 한 편도 포함해서는 안 돼.**\n" +
                        "- 픽사, 디즈니, 지브리, 일본 애니메이션 등 '애니메이션' 장르로 분류된 영화만 20개 골라줘.\n\n" +
                        "[추천 조건]\n" +
                        "1. 상황: %s 인원과 %s 감정에 어울리는 영화.\n" +
                        "2. 형식: 반드시 '영화제목(개봉연도)' 형식을 지킬 것.\n" +
                        "3. 구분: 각 영화는 반드시 콤마(,)로만 구분하고, 다른 부연 설명은 절대 하지 마.\n\n" +
                        "결과 예시: 센과 치히로의 행방불명(2001), 토이 스토리 4(2019), 너의 이름은.(2016)...",
                genre, people, motion
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", promptText))
                ))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Calling Gemini API: " + uri);
            GeminiResponseDto response = restTemplate.postForObject(uri, entity, GeminiResponseDto.class);

            if (response.getCandidates() != null) {
                String resultText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                return java.util.Arrays.stream(resultText.split(","))
                        .map(String::trim)
                        .toList();
            }
        } catch (Exception e) {
            System.err.println("Gemini 호출 에러: " + e.getMessage());
        }
        return List.of();
    }

    /** ai 추천 로직 추가 개선 20260211 ms **/
    public TmdbSearchListDto tmdbSearchExactMovie(String rawTitleFromGemini) {
        // 1. "기생충(2019)" -> 제목: 기생충, 연도: 2019 추출
        String title = rawTitleFromGemini.replaceAll("\\(\\d{4}\\)", "").trim();
        String year = "";

        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d{4})\\)").matcher(rawTitleFromGemini);
        if (m.find()) {
            year = m.group(1);
        }

        // 2. 검색 정확도를 높이기 위해 primary_release_year 파라미터 활용
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString("https://api.themoviedb.org/3/search/movie")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "ko-KR")
                .queryParam("query", title);

        if (!year.isEmpty()) {
            uriBuilder.queryParam("primary_release_year", year);
        }

        URI uri = uriBuilder.build().encode().toUri();

        try {
            TmdbSearchResponseDto response = restTemplate.getForObject(uri, TmdbSearchResponseDto.class);
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                // 검색 결과 중 첫 번째가 가장 연관도가 높음
                return response.getResults().get(0);
            }
        } catch (Exception e) {
            System.err.println("TMDB 정밀 검색 에러 (" + rawTitleFromGemini + "): " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getTmdbMovieDetails(String tmdbId) {
        if (tmdbId == null) return null;

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.themoviedb.org/3/movie/" + tmdbId)
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "ko-KR")
                .queryParam("append_to_response", "credits,watch/providers,release_dates")
                .build()
                .toUri();

        return restTemplate.getForObject(uri, Map.class);
    }

}
