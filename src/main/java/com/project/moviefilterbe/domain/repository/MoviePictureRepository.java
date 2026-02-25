package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.MoviePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviePictureRepository extends JpaRepository<MoviePicture, String> {
}