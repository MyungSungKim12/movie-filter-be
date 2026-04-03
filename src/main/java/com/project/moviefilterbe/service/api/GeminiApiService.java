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
                "Act as a movie recommendation API. Recommend 20 movies based on the user's input.\n\n" +
                        "[Data] Emotion: %s / People: %s / Genre: %s\n" +
                        "[Request] Mix latest hits and all-time classics appropriately.\n" +
                        "[Response Format] Output in JSON format with Korean values: {\"movies\": [{\"t\":\"Movie Title\", \"y\":\"Year\"}]}",
                motions, people, genres
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", promptText))
                )),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.6,
                        "candidateCount", 1
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiResponseDto response = restTemplate.postForObject(uri, entity, GeminiResponseDto.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                String resultText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
//                log.info("Gemini 추천 결과 : {}" , resultText);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<Map<String, String>>> resultMap = objectMapper.readValue(resultText, new TypeReference<>() {});
                List<Map<String, String>> tempMovieList = resultMap.get("movies");

                List<GeminiJsonDto> movieList = new ArrayList<>();

                for (Map<String, String> raw : tempMovieList) {
                    String title = raw.get("t");
                    String year = raw.get("y");
                    movieList.add(new GeminiJsonDto(title, year));
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
