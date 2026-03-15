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
@Table(name = "mf_click_log")
public class ClickLog {

    @Id
    @Column(name = "cl_id", length = 100, nullable = false)
    private String clId;

    @Column(name = "ui_id", length = 100)
    private String uiId;

    @Column(name = "mi_id", length = 100)
    private String miId;

    @Column(name = "cl_click_count")
    private Integer clClickCount;

    @Column(name = "cl_created_date", nullable = false)
    private OffsetDateTime clCreatedDate;

    @Column(name = "cl_updated_date", nullable = false)
    private OffsetDateTime clUpdatedDate;

    public ClickLog clickCountUpdate() {
        this.clClickCount += 1;
        this.clUpdatedDate = CommonUtil.getDateTimeNow();
        return this;
    }

    @Builder
    public ClickLog(String clId, String uiId, String miId, int clClickCount,
                    OffsetDateTime clCreatedDate, OffsetDateTime clUpdatedDate) {
        this.clId = clId;
        this.uiId = uiId;
        this.miId = miId;
        this.clClickCount = clClickCount;
        this.clCreatedDate = clCreatedDate;
        this.clUpdatedDate = clUpdatedDate;
    }
}