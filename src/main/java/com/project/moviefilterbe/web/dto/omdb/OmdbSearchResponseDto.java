package com.project.moviefilterbe.web.dto.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmdbSearchResponseDto {
    @JsonProperty("imdbID")
    private String imdbId;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("imdbRating")
    private String imdbRating; // IMDb 평점

    @JsonProperty("Metascore")
    private String metaScore;   // 메타크리틱 점수

    @JsonProperty("Ratings")
    private List<Rating> ratings; // 상세 평점 배열

    @Data
    public static class Rating {
        @JsonProperty("Source")
        private String source;
        @JsonProperty("Value")
        private String value;
    }
}
