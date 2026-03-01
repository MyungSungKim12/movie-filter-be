package com.project.moviefilterbe.domain.entity;

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
@Table(name = "mf_wish_list")
public class WishList {

    @Id
    @Column(name = "wl_id", length = 100, nullable = false)
    private String wlId;

    @Column(name = "ui_id", length = 100, nullable = false)
    private String uiId;

    @Column(name = "mi_id", length = 100, nullable = false)
    private String miId;

    @Column(name = "wl_created_date", nullable = false)
    private OffsetDateTime wlCreatedDate;

    @Builder
    public WishList(String wlId, String uiId, String miId, OffsetDateTime wlCreatedDate) {
        this.wlId = wlId;
        this.uiId = uiId;
        this.miId = miId;
        this.wlCreatedDate = wlCreatedDate;
    }
}