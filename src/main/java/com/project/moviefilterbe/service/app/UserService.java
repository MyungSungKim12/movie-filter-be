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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final ClickLogRepository clickLogRepository;
    private final ImageUtil imageUtil;

    // ── 찜 목록 ──────────────────────────────────────────────────────────────
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
        } catch (Exception e) {
            log.error("[USER] 즐겨찾기 업데이트 오류 : {}", e.getMessage());
        }
    }

    // ── 프로필 이미지 조회 ────────────────────────────────────────────────────
    public String getProfileImage(String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    String img = user.getUiImage();
                    // 빈 문자열은 null 로 처리하여 프론트에서 기본 아바타 표시
                    return (img != null && !img.isEmpty()) ? img : null;
                })
                .orElse(null);
    }

    // ── 프로필 이미지 업로드 ──────────────────────────────────────────────────
    @Transactional
    public String updateProfileImage(MultipartFile multipartFile, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId));

        String beforeImageUrl = user.getUiImage();
        String uploadImageUrl = null;

        try {
            // 1. R2 업로드
            Map<String, String> uploadResult = imageUtil.imageUploadS3(multipartFile);
            if (uploadResult == null || uploadResult.get("imgUrl") == null) {
                throw new RuntimeException("R2 업로드 실패");
            }
            uploadImageUrl = uploadResult.get("imgUrl");

            // 2. DB 갱신
            user.profileImageUpdate(uploadImageUrl);
            userRepository.save(user);

            // 3. 이전 이미지 삭제 (URL이 있을 때만)
            if (beforeImageUrl != null && !beforeImageUrl.isEmpty()) {
                imageUtil.ImageDeleteS3(beforeImageUrl);
            }

            return uploadImageUrl;

        } catch (Exception e) {
            log.error("[USER] 이미지 업데이트 오류 : {}", e.getMessage());
            // 업로드는 됐지만 DB 저장 실패 시 업로드 파일 롤백
            if (uploadImageUrl != null) {
                imageUtil.ImageDeleteS3(uploadImageUrl);
            }
            throw new RuntimeException("IMAGE_PROCESS_ERROR");
        }
    }

    // ── 클릭 로그 ─────────────────────────────────────────────────────────────
    @Transactional
    public void updateClickLog(WishlistRequestDto wishlistRequestDto) {
        try {
            Optional<ClickLog> existingClickLog = clickLogRepository.findByUiIdAndMiId(
                    wishlistRequestDto.getUiId(),
                    wishlistRequestDto.getMiId()
            );
            if (existingClickLog.isPresent()) {
                existingClickLog.get().clickCountUpdate();
            } else {
                ClickLog clickLog = ClickLog.builder()
                        .clId(CommonUtil.getGenerateId("cl"))
                        .uiId(wishlistRequestDto.getUiId())
                        .miId(wishlistRequestDto.getMiId())
                        .clClickCount(1)
                        .clCreatedDate(CommonUtil.getDateTimeNow())
                        .clUpdatedDate(CommonUtil.getDateTimeNow())
                        .build();
                clickLogRepository.save(clickLog);
            }
        } catch (Exception e) {
            log.error("[USER] 클릭 로그 업데이트 오류 : {}", e.getMessage());
        }
    }
}
