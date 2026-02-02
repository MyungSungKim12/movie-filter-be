package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.web.dto.detail.YoutubeVideoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class YoutubeApiService {

    private final RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String apiKey;

    private final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    public YoutubeVideoResponseDTO searchVideos(String movieTitle) {
        String query = movieTitle + " 리뷰";

        URI uri = UriComponentsBuilder
                .fromUriString(YOUTUBE_SEARCH_URL)
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("type", "video")
                .queryParam("maxResults", 3)
                .queryParam("key", apiKey)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri, YoutubeVideoResponseDTO.class);
    }
}