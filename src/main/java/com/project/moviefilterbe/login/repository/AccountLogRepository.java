package com.project.moviefilterbe.login.repository;

import com.project.moviefilterbe.login.entity.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* [작업자: ms / 날짜: 2026-01-21] 로그인 이력 저장을 위한 Repository 생성 */
@Repository
public interface AccountLogRepository extends JpaRepository<AccountLog, String> {
}