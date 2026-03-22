package com.project.moviefilterbe.login.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
public class JwtTokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpires;
    private Long refreshTokenExpires;
    private OffsetDateTime accessTokenExpiresDate;
    private OffsetDateTime refreshTokenExpiresDate;
}
