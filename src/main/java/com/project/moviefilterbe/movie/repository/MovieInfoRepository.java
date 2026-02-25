package com.project.moviefilterbe.movie.repository;

import com.project.moviefilterbe.movie.entity.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends JpaRepository<MovieInfo, String> {
    // 기본 저장/조회 기능 포함
}