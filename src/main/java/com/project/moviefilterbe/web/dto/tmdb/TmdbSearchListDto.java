package com.project.moviefilterbe.web.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmdbSearchListDto {
    private Long tmdbId;
    private String title;
    private String release_date;
    private String poster_path;
}
