package com.project.moviefilterbe.movie.controller;

import com.project.moviefilterbe.movie.dto.MovieResponseDTO;
import com.project.moviefilterbe.movie.dto.NaverSearchResponseDTO;
import com.project.moviefilterbe.movie.dto.YoutubeResponseDTO;
import com.project.moviefilterbe.movie.service.MovieService;
import com.project.moviefilterbe.movie.service.NaverService;
import com.project.moviefilterbe.movie.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final NaverService naverService;
    private final YoutubeService youtubeService;

    // 인기 영화 목록 (TMDB)
    @GetMapping("/popular")
    public MovieResponseDTO getPopular() {
        return movieService.getPopularMovies();
    }

    // 영화 리뷰 검색 (Naver)
    // 주소창에 /api/movies/reviews?title=영화제목 형식으로 입력하게 됩니다.
    @GetMapping("/reviews")
    public NaverSearchResponseDTO getReviews(@RequestParam("title") String title) {
        return naverService.searchReviews(title);
    }

    // 유튜브 영상 검색 테스트
    @GetMapping("/videos")
    public YoutubeResponseDTO getVideos(@RequestParam("title") String title) {
        return youtubeService.searchVideos(title);
    }
}