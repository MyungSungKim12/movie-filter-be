package com.project.moviefilterbe.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mf_movie_log")
public class MovieLog {

    @Id
    @Column(name = "ml_id", length = 100, nullable = false)
    private String mlId;

    @Column(name = "ui_id", length = 100)
    private String uiId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ml_movie_list", nullable = false)
    private List<String> mlMovieList;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ml_option_list", nullable = false)
    private Map<String, List<String>> mlOptionList;

    @Column(name = "ml_created_date", nullable = false)
    private OffsetDateTime mlCreatedDate;

    @Column(name = "ml_expires_date", nullable = false)
    private OffsetDateTime mlExpiresDate;

    @Builder
    public MovieLog(String mlId, String uiId, List<String> mlMovieList, Map<String, List<String>> mlOptionList,
                    OffsetDateTime mlCreatedDate, OffsetDateTime mlExpiresDate) {
        this.mlId = mlId;
        this.uiId = uiId;
        this.mlMovieList = mlMovieList;
        this.mlOptionList = mlOptionList;
        this.mlCreatedDate = mlCreatedDate;
        this.mlExpiresDate = mlExpiresDate;
    }
}