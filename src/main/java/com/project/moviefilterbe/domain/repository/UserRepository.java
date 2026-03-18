package com.project.moviefilterbe.domain.repository;

import com.project.moviefilterbe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // 이메일로 기존 가입자인지 확인하기 위한 메서드
    Optional<User> findByEmail(String email);
}