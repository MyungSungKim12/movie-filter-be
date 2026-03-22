package com.project.moviefilterbe.login.oauth;

import com.project.moviefilterbe.domain.entity.User;
import com.project.moviefilterbe.domain.repository.UserRepository;
import com.project.moviefilterbe.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = "";
        String name = "";
        String providerId = "";

        // 1. picture 부분: 구글은 "picture", 카카오는 "profile_image_url"을 사용
        // 2. providerId 부분: 구글은 "sub" (String), 카카오는 "id" (Long)를 사용

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            providerId = String.valueOf(attributes.get("id"));
        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerId = (String) attributes.get("sub");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            providerId = (String) response.get("id");
        }

        // 람다식 내부에서 사용하기 위해 모든 변수를 final 혹은 effectively final로 만들기
        final String finalEmail = (email == null || email.isEmpty()) ? providerId + "@" + registrationId + ".com" : email;
        final String finalName = (name == null) ? "Unknown" : name;
        final String finalRegistrationId = registrationId;
        final String finalProviderId = providerId;

        User user = userRepository.findByUiSocialId(finalProviderId)
                .map(entity -> entity.userInfoUpdate(finalName))
                .orElse(User.builder()
                        .uiId(CommonUtil.getGenerateId("ui"))
                        .uiName(finalName)
                        .uiImage("")
                        .uiRole("USER")
                        .uiStatus("Y")
                        .uiEmail(finalEmail)
                        .uiSocialProvider(finalRegistrationId)
                        .uiSocialId(finalProviderId)
                        .build());
        userRepository.save(user);

        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("id", user.getUiId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getUiRole())),
                customAttributes,
                "id"
        );
    }
}