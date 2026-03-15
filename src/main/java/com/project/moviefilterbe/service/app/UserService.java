package com.project.moviefilterbe.service.app;

import com.project.moviefilterbe.domain.entity.*;
import com.project.moviefilterbe.domain.repository.*;
import com.project.moviefilterbe.util.CommonUtil;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WishListRepository wishListRepository;

    @Transactional
    public void updateWishlist(WishlistRequestDto wishlistRequestDto) {
        Optional<WishList> existingWishlist = wishListRepository.findByUiIdAndMiId(
                wishlistRequestDto.getUiId(),
                wishlistRequestDto.getMiId()
        );

        if (existingWishlist.isPresent()) {
            wishListRepository.delete(existingWishlist.get());
        } else {
            WishList wishList = WishList.builder()
                    .wlId(CommonUtil.getGenerateId("wl"))
                    .uiId(wishlistRequestDto.getUiId())
                    .miId(wishlistRequestDto.getMiId())
                    .wlCreatedDate(CommonUtil.getDateTimeNow())
                    .build();
            wishListRepository.save(wishList);
        }
    }
}