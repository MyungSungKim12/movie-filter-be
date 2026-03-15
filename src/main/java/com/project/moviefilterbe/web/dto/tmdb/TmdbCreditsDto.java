package com.project.moviefilterbe.web.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbCreditsDto {
    @JsonProperty("cast")
    private List<Cast> cast;

    @JsonProperty("crew")
    private List<Crew> crew;

    @Data
    public static class Cast {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("known_for_department")
        private String department;
        @JsonProperty("order")
        private Integer order;
    }

    @Data
    public static class Crew {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("known_for_department")
        private String department;
        @JsonProperty("job")
        private String job;
    }
}
