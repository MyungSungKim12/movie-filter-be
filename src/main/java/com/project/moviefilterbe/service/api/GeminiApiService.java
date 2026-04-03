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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiService {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // 플랫폼 코드 → 한글 매핑
    private static final Map<String, String> PLATFORM_LABEL = Map.of(
            "NETFLIX", "넷플릭스(Netflix)",
            "TVING",   "티빙(TVING)",
            "WATCHA",  "왓챠(Watcha)",
            "WAVVE",   "웨이브(Wavve)",
            "AMAZON",  "아마존 프라임(Amazon Prime Video)",
            "DISNEY",  "디즈니+(Disney+)"
    );

    public List<GeminiJsonDto> geminiSearchMovies(MovieRecommendRequestDto requestDto) {
        Map<String, String> grouped = requestDto.getOption().stream()
                .collect(Collectors.groupingBy(
                        MovieRecommendRequestDto.Option::getType,
                        Collectors.mapping(MovieRecommendRequestDto.Option::getTitle, Collectors.joining(","))
                ));

        String people  = grouped.getOrDefault("P", "");
        String motions = grouped.getOrDefault("M", "");
        String genres  = grouped.getOrDefault("G", "");
        String platform = requestDto.getPlatform();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        // 플랫폼 조건 문구 생성
        String platformCondition;
        if (platform == null || platform.isBlank() || platform.equals("ALL")) {
            platformCondition = "OTT 플랫폼 제한 없이 추천해주세요.";
        } else {
            String label = PLATFORM_LABEL.getOrDefault(platform, platform);
            platformCondition = String.format(
                    "반드시 %s에서 시청 가능한 영화만 추천해주세요. 해당 플랫폼에서 제공되지 않는 영화는 절대 포함하지 마세요.", label
            );
        }

        String promptText = String.format(
                "Act as a movie recommendation API. Recommend 20 movies based on the user's input.\n\n" +
                        "[Data] Emotion: %s / People: %s / Genre: %s\n" +
                        "[Platform] %s\n" +
                        "[Request] Mix latest hits and all-time classics appropriately.\n" +
                        "[Response Format] Output in JSON format with Korean values: {\"movies\": [{\"t\":\"Movie Title\", \"y\":\"Year\"}]}",
                motions, people, genres, platformCondition
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

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<Map<String, String>>> resultMap = objectMapper.readValue(resultText, new TypeReference<>() {});
                List<Map<String, String>> tempMovieList = resultMap.get("movies");

                List<GeminiJsonDto> movieList = new ArrayList<>();
                for (Map<String, String> raw : tempMovieList) {
                    movieList.add(new GeminiJsonDto(raw.get("t"), raw.get("y")));
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