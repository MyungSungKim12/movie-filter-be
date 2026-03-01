package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, String> {
    Optional<WishList> findByUiIdAndMiId(String uiId, String miId);
}