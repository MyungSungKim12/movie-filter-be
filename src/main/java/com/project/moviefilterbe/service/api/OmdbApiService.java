package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.web.dto.omdb.OmdbSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class OmdbApiService {
    private final RestTemplate restTemplate;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    public OmdbSearchResponseDto getMovieDetails(String imdbId) {
        String uri = UriComponentsBuilder
                .fromUriString("http://www.omdbapi.com/")
                .queryParam("apikey", omdbApiKey)
                .queryParam("i", imdbId)
                .queryParam("plot", "full")
                .build()
                .toUriString();
        return  restTemplate.getForObject(uri, OmdbSearchResponseDto.class);
    }
}
