package com.project.moviefilterbe.web.dto.movie;

import lombok.Data;

import java.util.List;

@Data
public class MovieRecommendRequestDto {
    private String userId;
    private List<Option> option;
    private String platform; // 추가: "NETFLIX", "TVING", "WATCHA", "WAVVE", "AMAZON", "DISNEY", "ALL"

    @Data
    public static class Option {
        private String id;
        private String type;
        private String title;
    }
}