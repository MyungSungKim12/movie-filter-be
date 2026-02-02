package com.project.moviefilterbe.web.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmdbSearchResponseDto {
    private List<TmdbSearchListDto> results;
}
