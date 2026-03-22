package com.project.moviefilterbe.login.jwt;

import com.project.moviefilterbe.domain.entity.RefreshToken;
import com.project.moviefilterbe.domain.repository.RefreshTokenRepository;
import com.project.moviefilterbe.login.dto.JwtRefreshResponseDto;
import com.project.moviefilterbe.login.dto.JwtTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ResponseEntity<JwtRefreshResponseDto> reissue(String refreshToken) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .build();
        if (!tokenProvider.validateToken(refreshToken)) {
            log.error("유효하지 않은 리프레시 토큰입니다.");
//            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(null);
        }

        Authentication auth = tokenProvider.getAuthentication(refreshToken);
        String uiId = auth.getName();

        RefreshToken savedToken = refreshTokenRepository.findByUiId(uiId)
                .orElseThrow(() -> new RuntimeException("로그인 기록이 없는 사용자입니다."));

        if (!savedToken.getRtToken().equals(refreshToken)) {
            refreshTokenRepository.delete(savedToken);
            log.error("토큰이 일치하지 않습니다.");
//            throw new RuntimeException("토큰이 일치하지 않습니다. 다시 로그인하세요.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(null);
        }

        String role = auth.getAuthorities().iterator().next().getAuthority();
        JwtTokenResponseDto token = tokenProvider.generateToken(uiId, role);

        savedToken.updateToken(token.getRefreshToken(), token.getRefreshTokenExpiresDate());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .path("/")
                .httpOnly(true)                          // JS에서 접근 불가 (보안)
                .secure(false)                           // 로컬(http) 테스트 시 false, 배포(https) 시 true
                .sameSite("Lax")                         // 포트가 다른 로컬 환경에서 쿠키 전달 허용
                .maxAge(token.getRefreshTokenExpires())
                .build();

        JwtRefreshResponseDto jwtRefreshResponseDto = JwtRefreshResponseDto.builder()
                .accessToken(token.getAccessToken())
                .accessTokenExpires(token.getAccessTokenExpires())
                .accessTokenExpiresDate(token.getAccessTokenExpiresDate())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(jwtRefreshResponseDto);
    }
}
