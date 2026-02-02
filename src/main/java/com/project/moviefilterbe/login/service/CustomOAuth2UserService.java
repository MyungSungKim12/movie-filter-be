package com.project.moviefilterbe.login.service;

import com.project.moviefilterbe.domain.entity.User;
import com.project.moviefilterbe.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 어떤 서비스(google, kakao 등)를 통해 로그인했는지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = "";
        String name = "";
        String picture = "";
        String providerId = "";

        // 1. picture 부분: 구글은 "picture", 카카오는 "profile_image_url"을 사용
        // 2. providerId 부분: 구글은 "sub" (String), 카카오는 "id" (Long)를 사용

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            // 카카오는 profile_image_url 또는 thumbnail_image_url
            picture = (String) profile.get("profile_image_url");
            // 카카오 id는 Long이므로 String.valueOf로 변환
            providerId = String.valueOf(attributes.get("id"));
        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
            providerId = (String) attributes.get("sub");
        } else if ("naver".equals(registrationId)) {
            // 네이버는 "response"라는 키 안에 정보가 들어있음
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            picture = (String) response.get("profile_image");
            providerId = (String) response.get("id");
        }

        // 로그 확인 (기존 코드 유지)
        System.out.println("======= " + registrationId.toUpperCase() + " 로그인 정보 =======");
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Provider ID: " + providerId);
        System.out.println("===============================");

        // 람다식 내부에서 사용하기 위해 모든 변수를 final 혹은 effectively final로 만들기
        final String finalEmail = (email == null || email.isEmpty()) ? providerId + "@" + registrationId + ".com" : email;
        final String finalName = (name == null) ? "Unknown" : name;
        // DB에 not null로 되어있어서 없을 경우 특정 텍스트 넣기
        final String finalPicture = (picture == null || picture.isEmpty())
                ? "image null"  // 기본 이미지 주소 또는 ""
                : picture;
        final String finalRegistrationId = registrationId;
        final String finalProviderId = providerId;

        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .uiId(finalEmail)
                                .email(finalEmail)
                                .name(finalName)
                                .profileImage(finalPicture) // 에러 해결: final 변수 사용
                                .role("USER")
                                .status("ACTIVE")
                                .provider(finalRegistrationId) // 에러 해결: final 변수 사용
                                .providerId(finalProviderId)   // 에러 해결: final 변수 사용
                                .build()
                ));

        return oAuth2User;
    }
}