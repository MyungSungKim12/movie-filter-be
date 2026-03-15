package com.project.moviefilterbe.service.api;

import com.project.moviefilterbe.util.TmdbApiUtil;
import com.project.moviefilterbe.web.dto.gemini.GeminiJsonDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbDetailResponseDto;
import com.project.moviefilterbe.web.dto.tmdb.TmdbSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbApiService {
    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    public TmdbDetailResponseDto searchTmdbMovieInfo(GeminiJsonDto geminiJsonDto) {
        String movieTitle = geminiJsonDto.getTitle();
        String movieYear = geminiJsonDto.getYear();
        try {
            String tmdbApiStep01 = UriComponentsBuilder
                    .fromUriString("https://api.themoviedb.org/3/search/movie")
                    .queryParam("api_key", tmdbApiKey)
                    .queryParam("language", "ko-KR")
                    .queryParam("query", movieTitle)
                    .queryParam("primary_release_year", movieYear)
                    .build().encode().toUriString();

            TmdbSearchResponseDto tmdbSearchResponseDto = restTemplate.getForObject(tmdbApiStep01, TmdbSearchResponseDto.class);

            if (tmdbSearchResponseDto == null || tmdbSearchResponseDto.getResults() == null || tmdbSearchResponseDto.getResults().isEmpty()) {
                log.error("[TMDB] 검색 결과가 없습니다. : {}-{}", movieTitle, movieYear);
                return null;
            }

            Long tmdbId = tmdbSearchResponseDto.getResults().get(0).getTmdbId();
            // Long tmdbId = 1858L;

            if(tmdbId != null) {
                String tmdbApiStep02 = UriComponentsBuilder
                        .fromUriString("https://api.themoviedb.org/3/movie/{id}")
                        .queryParam("api_key", "fdb28192995b1d3f224fffe05d4da29a")
                        .queryParam("language", "ko-KR")
                        .queryParam("append_to_response", "credits,watch/providers,release_dates")
                        .buildAndExpand(tmdbId).toUriString();

                TmdbDetailResponseDto tmdbDetailResponseDto = restTemplate.getForObject(tmdbApiStep02, TmdbDetailResponseDto.class);
                if (tmdbDetailResponseDto != null) {
                    tmdbDetailResponseDto.setGenre(TmdbApiUtil.getExtractGenre(tmdbDetailResponseDto.getGenres()));
                    tmdbDetailResponseDto.setRating(TmdbApiUtil.getExtractRating(tmdbDetailResponseDto.getReleaseDates().getResults()));
                    tmdbDetailResponseDto.setCast(TmdbApiUtil.getExtractCast(tmdbDetailResponseDto.getCredits().getCast()));
                    tmdbDetailResponseDto.setCrew(TmdbApiUtil.getExtractCrew(tmdbDetailResponseDto.getCredits().getCrew()));
                    tmdbDetailResponseDto.setOtt(TmdbApiUtil.getExtractOtt(tmdbDetailResponseDto.getWatchProviders().getResults()));
                    return tmdbDetailResponseDto;
                } else {
                    log.error("[TMDB] 상세 정보 응답 빈값 ID: {}", tmdbId);
                    return null;
                }
            }
            return null;
        } catch (HttpClientErrorException e) {
            log.error("[TMDB] 클라이언트 에러 : {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (ResourceAccessException e) {
            log.error("[TMDB] 네트워크 연결 실패 : {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[TMDB] 알수없는 에러 ({}): ", e.getMessage());
            return null;
        }
    }
}