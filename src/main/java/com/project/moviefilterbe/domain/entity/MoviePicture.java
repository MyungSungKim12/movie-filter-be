package com.project.moviefilterbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

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

    private String mpPoster;

    private String mpBackdrop;
    private String mpType;
    private String mpAlt;

    private OffsetDateTime mpCreatedDate;
}