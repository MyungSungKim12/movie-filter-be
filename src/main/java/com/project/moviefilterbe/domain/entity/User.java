package com.project.moviefilterbe.domain.entity;

import com.project.moviefilterbe.util.CommonUtil;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "mf_users_info")
public class User {

    @Id
    @Column(name = "ui_id") // 2. DB 컬럼명 ui_id와 매핑
    private String uiId;

    @Column(name = "ui_name", nullable = false)
    private String name;

    @Column(name = "ui_image")
    private String profileImage;

    // Supabase
    @Column(name = "ui_email")
    private String email;

    @Column(name = "ui_role")
    private String role;

    @Column(name = "ui_status")
    private String status;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    public User profileImageUpdate(String imageUrl) {
        this.profileImage = imageUrl;
        return this;
    }
}