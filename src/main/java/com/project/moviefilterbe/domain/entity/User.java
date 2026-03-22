package com.project.moviefilterbe.domain.entity;

import com.project.moviefilterbe.util.CommonUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mf_users_info")
public class User {

    @Id
    @Column(name = "ui_id", length = 100, nullable = false)
    private String uiId;

    @Column(name = "ui_name", length = 100, nullable = false)
    private String uiName;

    @Column(name = "ui_image", length = 255, nullable = false)
    private String uiImage;

    @Column(name = "ui_role", length = 50, nullable = false)
    private String uiRole;

    @Column(name = "ui_status", length = 50, nullable = false)
    private String uiStatus;

    @Column(name = "ui_email", length = 100, nullable = false)
    private String uiEmail;

    @Column(name = "ui_social_provider", length = 50, nullable = false)
    private String uiSocialProvider;

    @Column(name = "ui_social_id", length = 50, nullable = false)
    private String uiSocialId;

    @Column(name = "ui_created_date", nullable = false)
    private OffsetDateTime uiCreatedDate;

    @Column(name = "ui_Updated_date", nullable = false)
    private OffsetDateTime uiUpdatedDate;

    public User userInfoUpdate(String name) {
        this.uiName = name;
        this.uiUpdatedDate = CommonUtil.getDateTimeNow();
        return this;
    }

    public User profileImageUpdate(String imageUrl) {
        this.uiImage = imageUrl;
        return this;
    }

    @Builder
    public User(String uiId, String uiName, String uiImage, String uiRole, String uiStatus, String uiEmail,
                String uiSocialProvider, String uiSocialId) {
        this.uiId = uiId;
        this.uiName = uiName;
        this.uiImage = uiImage;
        this.uiRole = uiRole;
        this.uiStatus = uiStatus;
        this.uiEmail = uiEmail;
        this.uiSocialProvider = uiSocialProvider;
        this.uiSocialId = uiSocialId;
        this.uiCreatedDate = CommonUtil.getDateTimeNow();
        this.uiUpdatedDate = CommonUtil.getDateTimeNow();
    }
}