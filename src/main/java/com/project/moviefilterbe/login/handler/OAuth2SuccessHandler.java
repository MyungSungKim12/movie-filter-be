package com.project.moviefilterbe.login.handler;

import com.project.moviefilterbe.login.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = "";

        // 1. 구글 추출
        if (attributes.containsKey("email")) {
            email = (String) attributes.get("email");
        }
        // 2. 네이버 추출 (response 라는 키 안에 들어있음)
        else if (attributes.containsKey("response")) {
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            email = (String) naverResponse.get("email");
        }
        // 3. 카카오 추출 (kakao_account 라는 키 안에 들어있음)
        else if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        // email이 없다면? (사용자가 이메일 제공 거부 시)
        if (email == null || email.isEmpty()) {
            // sub(ID)값이라도 넣어서 토큰이 깨지지 않게 방어
            email = oAuth2User.getName();
        }
        // 2. JWT 토큰 생성
        String token = tokenProvider.createToken(email, "USER");

        // 3. 프론트엔드(Next.js) 리다이렉트 URL 생성
        // 예: http://localhost:3000/login-success?token=ey...
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login-success")
                .queryParam("token", token)
                .build().toUriString();

        // 4. 리다이렉트 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}