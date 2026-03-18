package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.service.app.MovieService;
import com.project.moviefilterbe.web.dto.detail.NaverReviewResponseDTO;
import com.project.moviefilterbe.web.dto.detail.YoutubeVideoResponseDTO;
import com.project.moviefilterbe.service.api.NaverApiService;
import com.project.moviefilterbe.service.api.YoutubeApiService;
import com.project.moviefilterbe.web.dto.movie.MovieRecommendRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final NaverApiService naverService;
    private final YoutubeApiService youtubeService;

    @PostMapping("/recommend")
    public String recommendMovie(@RequestBody MovieRecommendRequestDto requestDto) {
        return movieService.recommendMovieService(requestDto);
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