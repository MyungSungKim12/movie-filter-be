package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUiSocialId(String email);
}