package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.service.api.TestApi;
import com.project.moviefilterbe.web.dto.TestRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final TestApi testApi;
    
    @PostMapping("/search")
    public String reviewWrite(@RequestBody List<TestRequestDto> requestDto) {
        System.out.println("==== 요청 도달 완료 ====");
        System.out.println("받은 데이터: " + requestDto);
        System.out.println(testApi.tmdbSearchMovies("미스트"));
        System.out.println(testApi.omdbSearchMovies("The mist"));
        System.out.println(testApi.geminiSearchMovies("혼자", "우울", "힐링"));
        return "Success";
    }
}
