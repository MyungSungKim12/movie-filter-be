package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.domain.entity.WishList;
import com.project.moviefilterbe.service.UserService;
import com.project.moviefilterbe.service.api.NaverApiService;
import com.project.moviefilterbe.service.api.TmdbApiService;
import com.project.moviefilterbe.service.api.YoutubeApiService;
import com.project.moviefilterbe.web.dto.TestRequestDto;
import com.project.moviefilterbe.web.dto.detail.NaverReviewResponseDTO;
import com.project.moviefilterbe.web.dto.detail.YoutubeVideoResponseDTO;
import com.project.moviefilterbe.web.dto.movie.MovieListResponseDto;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/wishlist")
    public void saveWishlist(@RequestBody WishlistRequestDto wishlistRequestDto) {
        userService.updateWishlist(wishlistRequestDto);
    }
    @GetMapping("/wishlist/{uiId}")
    public List<WishList> getWishlist(@PathVariable String uiId) {
        return userService.getWishlistByUiId(uiId);
    }

}