package com.project.moviefilterbe.movie.dto;

import lombok.Data;
import java.util.List;

@Data
public class NaverSearchResponseDTO {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items; // 검색 결과 리스트

    @Data
    public static class Item {
        private String title;       // 포스트 제목
        private String link;        // 상세 링크
        private String description; // 요약 내용
        private String bloggername; // 블로그 이름
        private String bloggerlink; // 블로그 주소
        private String postdate;    // 작성일
    }
}