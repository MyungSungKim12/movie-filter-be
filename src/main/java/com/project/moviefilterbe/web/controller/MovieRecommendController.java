package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.service.MovieService;
import com.project.moviefilterbe.web.dto.TestRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommend") // 경로도 추천 전용으로 변경
@RequiredArgsConstructor
public class MovieRecommendController {

    private final MovieService movieService;


    /* 프론트에서 전송한 3가지 옵션을 받아
     * AI 추천 -> TMDB 상세 정보 수집 -> DB 저장을 한 번에 처리 */

    @PostMapping("/search")
    public String recommendAndSave(@RequestBody List<TestRequestDto> requestDto) {
        System.out.println("======= [DEBUG] 컨트롤러 접속 성공! =======");
        System.out.println("받은 데이터 양: " + (requestDto != null ? requestDto.size() : "null"));

        if (requestDto == null || requestDto.isEmpty()) {
            System.out.println("데이터가 비어서 들어왔습니다!");
            return "Fail: Empty Data";
        }

        return movieService.recommendAndSave(requestDto);
    }
}