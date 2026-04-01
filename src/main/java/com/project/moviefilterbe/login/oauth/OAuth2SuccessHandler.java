package com.project.moviefilterbe.login.oauth;

import com.project.moviefilterbe.domain.entity.AccountLog;
import com.project.moviefilterbe.domain.entity.RefreshToken;
import com.project.moviefilterbe.domain.repository.AccountLogRepository;
import com.project.moviefilterbe.domain.repository.RefreshTokenRepository;
import com.project.moviefilterbe.login.dto.JwtTokenResponseDto;
import com.project.moviefilterbe.login.jwt.JwtTokenProvider;
import com.project.moviefilterbe.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final AccountLogRepository accountLogRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String uiId = (String) oAuth2User.getAttributes().get("id");
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        AccountLog accountLog = AccountLog.builder()
                .alId(CommonUtil.getGenerateId("al"))
                .uiId(uiId)
                .alLoginIp(request.getRemoteAddr())
                .build();
        accountLogRepository.save(accountLog);

        JwtTokenResponseDto token = tokenProvider.generateToken(uiId, role);

        RefreshToken refreshToken = refreshTokenRepository.findByUiId(uiId)
                .map(entity -> entity.updateToken(token.getRefreshToken(), token.getRefreshTokenExpiresDate()))
                .orElse(RefreshToken.builder()
                        .rtId(CommonUtil.getGenerateId("rt"))
                        .uiId(uiId)
                        .rtToken(token.getRefreshToken())
                        .rtExpiresDate(token.getRefreshTokenExpiresDate())
                        .build());
        refreshTokenRepository.save(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .path("/")
                .httpOnly(true)                          // JS에서 접근 불가 (보안)
                .secure(true)                           // 로컬(http) 테스트 시 false, 배포(https) 시 true
                .sameSite("Lax")                         // 포트가 다른 로컬 환경에서 쿠키 전달 허용
                .maxAge(token.getRefreshTokenExpires())  // 7일 (DB 만료일과 맞춤)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        //String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login-success")
        String targetUrl = UriComponentsBuilder.fromUriString("https://movie-filter-fe-mauve.vercel.app/login-success")
                .queryParam("accessToken", token.getAccessToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}