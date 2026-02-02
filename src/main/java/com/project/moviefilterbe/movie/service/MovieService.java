package com.project.moviefilterbe.movie.service;

import com.project.moviefilterbe.movie.dto.MovieDetailDTO;
import com.project.moviefilterbe.movie.dto.MovieResponseDTO;
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
public class MovieService {

    private final RestTemplate restTemplate;
    private final NaverService naverService;
    private final YoutubeService youtubeService;
    private final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private final String TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?language=ko-KR&page=1";

    @Value("${tmdb.api.token}")
    private String tmdbToken;

    public MovieResponseDTO getPopularMovies() {
        // 1. 헤더에 토큰(Bearer) 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. TMDB API 호출
        MovieResponseDTO response = restTemplate.exchange(
                TMDB_POPULAR_URL,
                HttpMethod.GET,
                entity,
                MovieResponseDTO.class
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
    public MovieDetailDTO getMovieDetailComposite(String title, Long movieId) {
        // 1. 기본 정보 가져오기
        return MovieDetailDTO.builder()
                .movieInfo(null) // 여기에 상세 정보 로직 연결
                .blogReviews(naverService.searchReviews(title).getItems())
                .videoReviews(youtubeService.searchVideos(title).getItems())
                .build();
    }
}