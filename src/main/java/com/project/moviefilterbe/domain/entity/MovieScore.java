package com.project.moviefilterbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mf_movie_score")
public class MovieScore {

    @Id
    @Column(name = "ms_id", length = 100, nullable = false)
    private String msId;

    @Column(name = "mi_id", length = 100, nullable = false)
    private String miId;

    @Column(name = "ms_title", length = 50, nullable = false)
    private String msTitle;

    @Column(name = "ms_year", length = 50, nullable = false)
    private String msYear;

    @Column(name = "ms_tmdb_score", nullable = false)
    private BigDecimal msTmdbScore;

    @Column(name = "ms_imdb_score", nullable = false)
    private BigDecimal msImdbScore;

    @Column(name = "ms_meta_score", nullable = false)
    private int msMetaScore;

    @Column(name = "ms_tomato_score", nullable = false)
    private int msTomatoScore;

    @Column(name = "ms_created_date", nullable = false)
    private OffsetDateTime msCreatedDate;

    @Builder
    public MovieScore(String msId, String miId, String msTitle, String msYear, BigDecimal msTmdbScore,
                    BigDecimal msImdbScore, int msMetaScore, int msTomatoScore, OffsetDateTime msCreatedDate) {
        this.msId = msId;
        this.miId = miId;
        this.msTitle = msTitle;
        this.msYear = msYear;
        this.msTmdbScore = msTmdbScore;
        this.msImdbScore = msImdbScore;
        this.msMetaScore = msMetaScore;
        this.msTomatoScore = msTomatoScore;
        this.msCreatedDate = msCreatedDate;
    }
}