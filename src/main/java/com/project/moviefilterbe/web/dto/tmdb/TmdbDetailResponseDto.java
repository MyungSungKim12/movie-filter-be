package com.project.moviefilterbe.web.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbDetailResponseDto {
    @JsonProperty("id")
    private Long tmdbId;

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("overview")
    private String summary;

    @JsonProperty("runtime")
    private Integer runtime;

    @JsonProperty("poster_path")
    private String posterImagePath;

    @JsonProperty("backdrop_path")
    private String backdropImagePath;

    @JsonProperty("vote_average")
    private Double tmdbScore;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("genres")
    private List<Genres> genres;

    @JsonProperty("credits")
    private TmdbCreditsDto credits;

    @JsonProperty("watch/providers")
    private TmdbWatchProviderDto watchProviders;

    @JsonProperty("release_dates")
    private TmdbReleaseDatesDto releaseDates;

    private String genre;

    private String rating;

    private String cast;

    private String crew;

    private String ott;

    @Data
    public static class Genres {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("name")
        private String name;
    }
}
