package com.project.moviefilterbe.web.dto.movie;

import lombok.Data;

import java.util.List;

@Data
public class MovieRecommendRequestDto {
    private String userId;
    private List<Option> option;

    @Data
    public static class Option {
        private String id;
        private String type;
        private String title;
    }
}
