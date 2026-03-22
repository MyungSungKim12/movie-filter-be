package com.project.moviefilterbe.domain.entity;

import com.project.moviefilterbe.util.CommonUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mf_account_log")
@Getter
@NoArgsConstructor
public class AccountLog {

    @Id
    @Column(name = "al_id", length = 100, nullable = false)
    private String alId;

    @Column(name = "ui_id", length = 100, nullable = false)
    private String uiId;

    @Column(name = "al_login_ip", length = 50, nullable = false)
    private String alLoginIp;

    @Column(name = "al_login_date", nullable = false)
    private OffsetDateTime uiLoginDate;

    @Builder
    public AccountLog(String alId, String uiId, String alLoginIp) {
        this.alId = alId;
        this.uiId = uiId;
        this.alLoginIp = alLoginIp;
        this.uiLoginDate = CommonUtil.getDateTimeNow();
    }
}