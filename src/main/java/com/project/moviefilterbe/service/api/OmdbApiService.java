package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.web.dto.omdb.OmdbSearchResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OmdbApiService {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public OmdbSearchResponseDto getMovieDetails(String imdbId) {
        String uri = UriComponentsBuilder
                .fromUriString("http://www.omdbapi.com/")
                .queryParam("apikey", omdbApiKey)
                .queryParam("i", imdbId)
                .queryParam("plot", "full")
                .build()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        return  restTemplate.getForObject(uri, OmdbSearchResponseDto.class);
    }
}
