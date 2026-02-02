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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class TestApi {
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

    public List<OmdbSearchListDto> omdbSearchMovies(String title) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://www.omdbapi.com/")
                .queryParam("apikey", omdbApiKey)
                .queryParam("s", title) // Search 파라미터
                .build()
                .encode()
                .toUri();
        System.out.println(restTemplate.getForObject(uri, OmdbSearchResponseDto.class));

        OmdbSearchResponseDto response = restTemplate.getForObject(uri, OmdbSearchResponseDto.class);

        // OMDb는 검색 결과가 없을 때 "Response":"False"를 보내므로 체크가 필요합니다.
        if ("True".equalsIgnoreCase(response.getResponse())) {
            return response.getSearch();
        }

        return List.of();
    }

    public List<String> geminiSearchMovies(String people, String motion, String genre) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        String promptText = String.format(
                "너는 영화 추천 전문가야. 다음 조건에 맞는 영화 3개만 추천해줘. " +
                "[조건] 인원: %s, 감정: %s, 장르: %s. " +
                "답변은 반드시 영화 제목만 콤마(,)로 구분해서 보내줘. 예: 영화1,영화2,영화3",
                people, motion, genre
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
}
