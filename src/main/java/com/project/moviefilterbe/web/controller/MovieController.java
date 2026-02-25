package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.web.dto.movie.MovieListResponseDto;
import com.project.moviefilterbe.web.dto.detail.NaverReviewResponseDTO;
import com.project.moviefilterbe.web.dto.detail.YoutubeVideoResponseDTO;
import com.project.moviefilterbe.service.api.TmdbApiService;
import com.project.moviefilterbe.service.api.NaverApiService;
import com.project.moviefilterbe.service.api.YoutubeApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final TmdbApiService movieService;
    private final NaverApiService naverService;
    private final YoutubeApiService youtubeService;

    // 인기 영화 목록 (TMDB)
    @GetMapping("/popular")
    public MovieListResponseDto getPopular() {
        System.out.println("11111111111");

        return movieService.getPopularMovies();
    }
    // 영화 리뷰 검색 (Naver)
    @GetMapping("/reviews")
    public NaverReviewResponseDTO getReviews(@RequestParam("title") String title) {
        System.out.println("2222222222");
        return naverService.searchReviews(title);
    }

    // 유튜브 영상 검색 테스트
    @GetMapping("/videos")
    public YoutubeVideoResponseDTO getVideos(@RequestParam("title") String title) {
        System.out.println("333333333333");
        return youtubeService.searchVideos(title);
    }
}