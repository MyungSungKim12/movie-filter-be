package com.project.moviefilterbe.domain.entity;

import com.project.moviefilterbe.util.CommonUtil;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "mf_movies_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfo {

    @Id
    @Column(name = "mi_id", nullable = false)
    private String miId;

    @Column(name = "mi_title", nullable = false)
    private String miTitle;

    @Column(name = "mi_summary", nullable = false)
    private String miSummary;

    @Column(name = "mi_release_date", nullable = false)
    private String miReleaseDate;

    @Column(name = "mi_runtime", nullable = false)
    private Integer miRuntime;

    @Column(name = "mi_genre", nullable = false)
    private String miGenre;

    @Column(name = "mi_rating", nullable = false)
    private String miRating;

    @Column(name = "mi_popularity", nullable = false)
    private Double miPopularity;

    @Column(name = "mi_cast", nullable = false)
    private String miCast;

    @Column(name = "mi_crew", nullable = false)
    private String miCrew;

    @Column(name = "mi_provider", nullable = false)
    private String miProvider;

    @Column(name = "mi_created_date", nullable = false)
    private OffsetDateTime miCreatedDate;

    @Column(name = "mi_updated_date", nullable = false)
    private OffsetDateTime miUpdatedDate;

    @Column(name = "mi_created_count", nullable = false)
    private Long miCreatedCount;

    @Column(name = "mi_wishlist_count", nullable = false)
    private Long miWishlistCount;

    @Column(name = "mi_imdb_id", nullable = false)
    private String miImdbId;


    public MovieInfo movieInfoUpdate(String ott) {
        this.miProvider = ott;
        this.miCreatedCount += 1;
        this.miUpdatedDate = CommonUtil.getDateTimeNow();
        return this;
    }

    @Builder
    public MovieInfo(String miId, String miTitle, String miSummary, String miReleaseDate, int miRuntime,
                     String miGenre, String miRating, double miPopularity, String miCast, String miCrew,
                     String miProvider, OffsetDateTime miCreatedDate, OffsetDateTime miUpdatedDate,
                     Long miCreatedCount, Long miWishlistCount, String miImdbId) {
        this.miId = miId;
        this.miTitle = miTitle;
        this.miSummary = miSummary;
        this.miReleaseDate = miReleaseDate;
        this.miRuntime = miRuntime;
        this.miGenre = miGenre;
        this.miRating = miRating;
        this.miPopularity = miPopularity;
        this.miCast = miCast;
        this.miCrew = miCrew;
        this.miProvider = miProvider;
        this.miCreatedDate = miCreatedDate;
        this.miUpdatedDate = miUpdatedDate;
        this.miCreatedCount = miCreatedCount;
        this.miWishlistCount = miWishlistCount;
        this.miImdbId = miImdbId;
    }
}