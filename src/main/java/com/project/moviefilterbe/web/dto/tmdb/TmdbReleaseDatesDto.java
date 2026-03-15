package com.project.moviefilterbe.web.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbReleaseDatesDto {
    @JsonProperty("results")
    private List<Results> results;

    @Data
    public static class Results {
        @JsonProperty("iso_3166_1")
        private String isoId;
        @JsonProperty("release_dates")
        private List<ReleaseDates> detail;
    }

    @Data
    public static class ReleaseDates {
        @JsonProperty("certification")
        private String rating;
        @JsonProperty("note")
        private String note;
        @JsonProperty("release_date")
        private String date;
        @JsonProperty("type")
        private Integer type;
    }
}
