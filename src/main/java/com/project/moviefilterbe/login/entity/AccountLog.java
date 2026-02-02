package com.project.moviefilterbe.login.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mf_account_log")
@Getter
@NoArgsConstructor
public class AccountLog {

    @Id
    @Column(name = "al_id")
    private String alId; // al_ + UUID

    @Column(name = "al_login_ip")
    private String alLoginIp;

    @Column(name = "al_login_date")
    private LocalDateTime alLoginDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ui_id", nullable = false) // FK 참조 컬럼명: ui_id
    private User user;

    @Builder
    public AccountLog(String alId, User user, String alLoginIp) {
        this.alId = alId;
        this.user = user;
        this.alLoginIp = alLoginIp;
        this.alLoginDate = LocalDateTime.now(); // 로그인 시점 시간 저장
    }
}