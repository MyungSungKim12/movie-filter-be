package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.MovieLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieLogRepository extends JpaRepository<MovieLog, String> {
    // 기본 저장/조회 기능 포함
}