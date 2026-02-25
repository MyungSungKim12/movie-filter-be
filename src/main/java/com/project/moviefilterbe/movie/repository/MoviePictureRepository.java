package com.project.moviefilterbe.movie.repository;

import com.project.moviefilterbe.movie.entity.MoviePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviePictureRepository extends JpaRepository<MoviePicture, String> {
}