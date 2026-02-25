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
@Table(name = "mf_movie_pictures")
public class MoviePicture {
    @Id
    private String mpId;

    private String miId;

    private String mpUrl;
    private String mpType;
    private String mpAlt;

    @CreatedDate
    private LocalDateTime mpCreatedDate;
}