package com.project.moviefilterbe.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mf_movie_score")
public class MovieScore {
    @Id
    private String msId;

    private String miId;
    private String uiId;

    private String msTitle;

    private Double msScoreRating;

    @CreatedDate
    private LocalDateTime msCreatedDate;
}