package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUiId(String uiId);
}