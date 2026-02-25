package com.project.moviefilterbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mf_movies_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfo {

    @Id
    @Column(name = "mi_id")
    private String miId; // mi_id 대신 miId로 수정

    @Column(name = "mi_title", nullable = false)
    private String miTitle; // mi_title 대신 miTitle로 수정

    @Column(name = "mi_summary")
    private String miSummary;

    @Column(name = "mi_release_date")
    private String miReleaseDate;

    @Column(name = "mi_runtime")
    private Integer miRuntime;

    @Column(name = "mi_genre")
    private String miGenre;

    @Column(name = "mi_rating")
    private String miRating;

    @Column(name = "mi_popularity")
    private Double miPopularity;

    @Column(name = "mi_cast")
    private String miCast;

    @Column(name = "mi_crew")
    private String miCrew;

    @Column(name = "mi_provider")
    private String miProvider;

    @Column(name = "mi_created_date")
    private String miCreatedDate;

    @Column(name = "mi_created_count")
    private Long miCreatedCount;

    @Column(name = "mi_wishlist_count")
    private Long miWishlistCount;

    @Column(name = "mi_imdb_id")
    private String miImdbId;

}