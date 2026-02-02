package com.project.moviefilterbe.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mf_movie_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfo {

    @Id
    @Column(name = "mv_id")
    private Long id;

    @Column(name = "mv_title", nullable = false)
    private String title;

    @Column(name = "mv_overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "mv_poster_path")
    private String posterPath;

    @Column(name = "mv_release_date")
    private String releaseDate;

    @Column(name = "mv_vote_average")
    private Double voteAverage;

    @Column(name = "mv_popularity")
    private Double popularity;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt; // DB에 저장/수정된 시간 (캐싱 판단 기준)

    // JPA의 라이프사이클 이벤트를 이용해 저장/수정 시 자동으로 시간 갱신
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}