package com.project.moviefilterbe.web.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbWatchProviderDto {
    @JsonProperty("results")
    private Results results;

    @Data
    public static class Results {
        @JsonProperty("KR")
        private CountryData kr;
    }

    @Data
    public static class CountryData {
        @JsonProperty("link")
        private String link;
        @JsonProperty("flatrate")
        private List<Flatrate> flatrate;
    }

    @Data
    public static class Flatrate {
        @JsonProperty("provider_id")
        private Integer providerId;
        @JsonProperty("provider_name")
        private String providerName;
        @JsonProperty("logo_path")
        private String logoImagePath;
    }
}
