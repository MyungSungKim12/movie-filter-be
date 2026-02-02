package com.project.moviefilterbe.movie.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class MovieResponseDTO {
    private int page;
    private List<MovieDetailDTO> results; // 영화 목록
    private int total_pages;
    private int total_results;

    @Getter @Setter
    public static class MovieDetailDTO {
        private Long id;             // TMDB 영화 고유 ID
        private String title;        // 제목
        private String overview;     // 줄거리
        private String poster_path;  // 포스터 이미지 경로
        private String release_date; // 개봉일
        private double vote_average; // 평점
    }
}