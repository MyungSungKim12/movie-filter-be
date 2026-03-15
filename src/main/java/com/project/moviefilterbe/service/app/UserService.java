package com.project.moviefilterbe.service.app;

import com.project.moviefilterbe.domain.entity.*;
import com.project.moviefilterbe.domain.repository.*;
import com.project.moviefilterbe.util.CommonUtil;
import com.project.moviefilterbe.util.ImageUtil;
import com.project.moviefilterbe.web.dto.user.WishlistRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;

    private final ImageUtil imageUtil;

    @Transactional
    public void updateWishlist(WishlistRequestDto wishlistRequestDto) {
        try {
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
        } catch(Exception e) {
            log.error("[USER] 즐겨찾기 업데이트 오류 : {}", e.getMessage());
        }
    }

    public void updateProfileImage(MultipartFile multipartFile, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId));
        String uploadImageUrl = null;
        String beforeImageUrl = user.getProfileImage();
        try {
            Map<String, String> uploadResult = imageUtil.imageUploadS3(multipartFile);
            uploadImageUrl = uploadResult.get("imgUrl");

            user.profileImageUpdate(uploadImageUrl);
            userRepository.save(user);

            if(beforeImageUrl != null && !beforeImageUrl.isEmpty()) {
                imageUtil.ImageDeleteS3(beforeImageUrl);
            }
        } catch(Exception e) {
            log.error("[USER] 이미지 업데이트 오류 : {}", e.getMessage());
            if (uploadImageUrl != null) {
                imageUtil.ImageDeleteS3(uploadImageUrl);
            }
            throw new RuntimeException("IMAGE_PROCESS_ERROR");
        }
    }
}