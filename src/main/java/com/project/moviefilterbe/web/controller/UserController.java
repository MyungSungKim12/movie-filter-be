package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.login.dto.JwtRefreshResponseDto;
import com.project.moviefilterbe.login.jwt.JwtService;
import com.project.moviefilterbe.service.app.UserService;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    public ResponseEntity<Map<String, String>> profileUploadImage(
            @RequestPart("files") MultipartFile multipartFile,
            @RequestPart("userId") String userId) {
        String imageUrl = userService.updateProfileImage(multipartFile, userId);
        return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
    }

    @GetMapping("/profile-image")
    public ResponseEntity<Map<String, String>> getProfileImage(@RequestParam("userId") String userId) {
        String imageUrl = userService.getProfileImage(userId);
        return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl != null ? imageUrl : ""));
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