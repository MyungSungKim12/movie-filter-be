package com.project.moviefilterbe.login.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class JwtRefreshResponseDto {
    private String accessToken;
    private Long accessTokenExpires;
    private OffsetDateTime accessTokenExpiresDate;
}
