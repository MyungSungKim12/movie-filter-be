package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.MovieScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScoreRepository extends JpaRepository<MovieScore, String> {
}