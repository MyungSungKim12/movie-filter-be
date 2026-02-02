package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.service.api.NaverApiService;
import com.project.moviefilterbe.service.api.YoutubeApiService;
import com.project.moviefilterbe.web.dto.movie.MovieDetailResponseDto;
import com.project.moviefilterbe.web.dto.movie.MovieListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/* [작업자: ms / 날짜: 2026-01-26] TMDB API 데이터 가공 및 인증 로직 보완 */
@Service
@RequiredArgsConstructor
public class TmdbApiService {

    private final RestTemplate restTemplate;
    private final NaverApiService naverApiService;
    private final YoutubeApiService youtubeService;
    private final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private final String TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?language=ko-KR&page=1";

    @Value("${tmdb.api.token}")
    private String tmdbToken;

    public MovieListResponseDto getPopularMovies() {
        // 1. 헤더에 토큰(Bearer) 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. TMDB API 호출
        MovieListResponseDto response = restTemplate.exchange(
                TMDB_POPULAR_URL,
                HttpMethod.GET,
                entity,
                MovieListResponseDto.class
        ).getBody();

        // 3. 포스터 URL
        if (response != null && response.getResults() != null) {
            response.getResults().forEach(movie -> {
                if (movie.getPoster_path() != null && !movie.getPoster_path().startsWith("http")) {
                    movie.setPoster_path(IMAGE_BASE_URL + movie.getPoster_path());
                }
            });
        }

        return response;
    }
    public MovieDetailResponseDto getMovieDetailComposite(String title, Long movieId) {
        // 1. 기본 정보 가져오기
        return MovieDetailResponseDto.builder()
                .movieInfo(null) // 여기에 상세 정보 로직 연결
                .blogReviews(naverApiService.searchReviews(title).getItems())
                .videoReviews(youtubeService.searchVideos(title).getItems())
                .build();
    }
}