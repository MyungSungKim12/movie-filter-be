package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieInfoRepository extends JpaRepository<MovieInfo, String> {
    List<MovieInfo> findAllByMiIdIn(List<String> tmdbIdList);
}