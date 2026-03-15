package com.project.moviefilterbe.web.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbSearchResponseDto {
    @JsonProperty("results")
    private List<Results> results;

    @Data
    public static class Results {
        @JsonProperty("id")
        private Long tmdbId;
        @JsonProperty("title")
        private String title;
        @JsonProperty("release_date")
        private String releaseDate;
    }
}
