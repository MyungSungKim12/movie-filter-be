package com.project.moviefilterbe.web.controller;

import com.project.moviefilterbe.service.app.UserService;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/wishlist")
    public void saveWishlist(@RequestBody WishlistRequestDto wishlistRequestDto) {
        userService.updateWishlist(wishlistRequestDto);
    }
}