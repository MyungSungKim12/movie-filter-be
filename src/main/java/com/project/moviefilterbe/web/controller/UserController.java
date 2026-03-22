package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.login.dto.JwtRefreshResponseDto;
import com.project.moviefilterbe.login.jwt.JwtService;
import com.project.moviefilterbe.service.app.UserService;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/wishlist")
    public void saveWishlist(@RequestBody WishlistRequestDto wishlistRequestDto) {
        userService.updateWishlist(wishlistRequestDto);
    }

    @PostMapping("/uploadImage")
    public void profileUploadImage(@RequestPart("files") MultipartFile multipartFile, @RequestPart("userId") String userId) {
        userService.updateProfileImage(multipartFile, userId);
    }

    @PostMapping("/clickLog")
    public void saveClickLog(@RequestBody WishlistRequestDto wishlistRequestDto) {
        userService.updateClickLog(wishlistRequestDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<JwtRefreshResponseDto> reissue(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return jwtService.reissue(refreshToken);
    }
}