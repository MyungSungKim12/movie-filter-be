package com.project.moviefilterbe.web.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TmdbSearchListDto {

    @JsonProperty("id")
    private String tmdbId;

    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("overview") // mi_summary로 들어갈 줄거리
    private String overview;

    @JsonProperty("vote_average") // mi_rating으로 들어갈 평점
    private Double voteAverage;

    @JsonProperty("popularity") // mi_popularity로 들어갈 인기도
    private Double popularity;
}