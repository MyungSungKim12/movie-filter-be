package com.project.moviefilterbe.domain.entity;

import com.project.moviefilterbe.util.CommonUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mf_refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "rt_id", length = 100, nullable = false)
    private String rtId;

    @Column(name = "ui_id", length = 100, nullable = false)
    private String uiId;

    @Column(name = "rt_token", nullable = false)
    private String rtToken;

    @Column(name = "rt_created_date", nullable = false)
    private OffsetDateTime rtCreatedDate;

    @Column(name = "rt_expires_date", nullable = false)
    private OffsetDateTime rtExpiresDate;

    public RefreshToken updateToken(String token, OffsetDateTime rtExpiresDate) {
        this.rtToken = token;
        this.rtExpiresDate = rtExpiresDate;
        return this;
    }
    @Builder
    public RefreshToken(String rtId, String uiId, String rtToken, OffsetDateTime rtExpiresDate) {
        this.rtId = rtId;
        this.uiId = uiId;
        this.rtToken = rtToken;
        this.rtCreatedDate = CommonUtil.getDateTimeNow();
        this.rtExpiresDate = rtExpiresDate;
    }
}
