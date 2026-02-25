package com.project.moviefilterbe.movie.repository;

import com.project.moviefilterbe.movie.entity.MovieScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScoreRepository extends JpaRepository<MovieScore, String> {
}