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
    @JsonProperty("Search")
    private List<OmdbSearchListDto> search;
    @JsonProperty("totalResults")
    private String totalResults;
    @JsonProperty("Response")
    private String response;
    @JsonProperty("Error")
    private String error;
}
