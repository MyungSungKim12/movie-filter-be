package com.project.moviefilterbe.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestRequestDto {

    private String id;
    private String type;
    private String title;
}
