package com.project.moviefilterbe.movie.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MovieDetailDTO {
    // TMDB 데이터 (필요한 것만 추출하거나 객체 통째로)
    private Object movieInfo;

    // 네이버 리뷰 리스트
    private List<NaverSearchResponseDTO.Item> blogReviews;

    // 유튜브 영상 리스트
    private List<YoutubeResponseDTO.YoutubeItem> videoReviews;
}