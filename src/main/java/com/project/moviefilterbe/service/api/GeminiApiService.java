package com.project.moviefilterbe.service.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.moviefilterbe.web.dto.gemini.GeminiJsonDto;
import com.project.moviefilterbe.web.dto.gemini.GeminiResponseDto;
import com.project.moviefilterbe.web.dto.movie.MovieRecommendRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiService {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public List<GeminiJsonDto> geminiSearchMovies(MovieRecommendRequestDto requestDto) {
        Map<String, String> grouped = requestDto.getOption().stream()
                .collect(Collectors.groupingBy(
                        MovieRecommendRequestDto.Option::getType,
                        Collectors.mapping(MovieRecommendRequestDto.Option::getTitle, Collectors.joining(","))
                ));

        String people = grouped.getOrDefault("P", "");
        String motions = grouped.getOrDefault("M", "");
        String genres = grouped.getOrDefault("G", "");

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        String promptText = String.format(
            "너는 영화 추천 전문 API 시스템이야. 사용자의 감정, 인원, 장르 데이터를 기반으로 최적의 영화 20개를 추천한다.\n\n" +
            "[규칙]\n" +
            "1. 모든 응답은 반드시 순수한 JSON 형식으로만 출력한다. 다른 설명은 생략한다.\n" +
            "2. 영화 제목은 반드시 '영화제목(개봉연도)' 형식으로, 무조건 한국어로 작성한다.\n" +
            "3. 최근 3년 이내 신작 5개 이상 포함, 대중적 작품과 명작 비율 7:3 엄수.\n\n" +
            "[JSON 구조]\n" +
            "{\n" +
            "  \"recommended_movies\": [\"제목(연도)\", \"제목(연도)\", ...]\n" +
            "}\n\n" +
            "[요청 내용]\n" +
            "감정: %s / 인원: %s / 장르: %s",
            motions, people, genres
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", promptText))
                )),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json"
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiResponseDto response = restTemplate.postForObject(uri, entity, GeminiResponseDto.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                String resultText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
//                log.info("Gemini 추천 결과 : {}" , resultText);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> resultMap = objectMapper.readValue(resultText, new TypeReference<>() {});
                List<String> tempMovieList = resultMap.get("recommended_movies");

                List<GeminiJsonDto> movieList = new ArrayList<>();
                Pattern pattern = Pattern.compile("(.+)\\s*\\((\\d{4})\\)");

                for (String raw : tempMovieList) {
                    Matcher matcher = pattern.matcher(raw);
                    if (matcher.find()) {
                        String title = matcher.group(1).trim();
                        String year = matcher.group(2);
                        movieList.add(new GeminiJsonDto(title, year));
                    }
                }
                return movieList;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Gemini 호출 에러: {}", e.getMessage());
            return null;
        }
    }
}
