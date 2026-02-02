package com.project.moviefilterbe.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class YoutubeResponseDTO {
    private List<YoutubeItem> items;

    @Data
    public static class YoutubeItem {
        private Id id;
        private Snippet snippet;
    }

    @Data
    public static class Id {
        private String videoId;
    }

    @Data
    public static class Snippet {
        private String title;
        private String description;
        private Thumbnails thumbnails;
    }

    @Data
    public static class Thumbnails {
        @JsonProperty("high") // 고화질 썸네일 사용
        private ThumbnailItem high;
    }

    @Data
    public static class ThumbnailItem {
        private String url;
    }
}