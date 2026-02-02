package com.project.moviefilterbe.movie.service;

import com.project.moviefilterbe.movie.dto.NaverSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final RestTemplate restTemplate;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public NaverSearchResponseDTO searchReviews(String movieTitle) {
        // 1. 검색어 가공 (정확도를 위해 '영화제목 + 후기'로 검색)
        String query = movieTitle + " 후기";

        // 2. URI 생성 (한글 인코딩 포함)
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/blog.json")
                .queryParam("query", query)
                .queryParam("display", 5) // 화면에 보여줄 개수
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 3. 헤더 설정 (네이버는 ID와 Secret이 각각 필요함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 4. API 호출
        NaverSearchResponseDTO response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                NaverSearchResponseDTO.class
        ).getBody();

        // 5. 데이터 정제 (태그 제거)
        if (response != null && response.getItems() != null) {
            response.getItems().forEach(item -> {
                item.setTitle(stripHtmlTags(item.getTitle()));
                item.setDescription(stripHtmlTags(item.getDescription()));
            });
        }

        return response;
    }

    // HTML 태그 제거용 헬퍼 메서드
    private String stripHtmlTags(String html) {
        if (html == null) return null;
        // <b>, </b> 및 기타 HTML 태그를 빈 문자열로 치환
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "")
                .replaceAll("&quot;", "\"") // 따옴표 특수문자 처리
                .replaceAll("&amp;", "&");  // & 특수문자 처리
    }
}