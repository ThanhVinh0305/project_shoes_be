package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.behavior.TrackClickRequest;
import vn.shoestore.application.request.behavior.TrackSearchRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.logic.behavior.ITrackBehaviorUseCase;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.utils.AuthUtils;

@RestController
@RequestMapping("/v2/user-behaviors")
@RequiredArgsConstructor
public class UserBehaviorTrackingController {
    private final ITrackBehaviorUseCase trackBehaviorUseCase;


    @PostMapping("/click")
    public ResponseEntity<BaseResponse> trackClick(@RequestBody TrackClickRequest request) {
        Long userId = getCurrentUserIdOrThrow();
        trackBehaviorUseCase.trackClick(userId, request.getProductId(), request.getSource());
        return ResponseFactory.success();
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse> trackSearch(@RequestBody TrackSearchRequest request) {
        Long userId = getCurrentUserIdOrThrow();
        trackBehaviorUseCase.trackSearch(userId, request.getKeyword(), request.getFilters(), request.getResultCount());
        return ResponseFactory.success();
    }

    /**
     * Lấy userId từ token, nếu không có thì trả về lỗi 401
     */
    private Long getCurrentUserIdOrThrow() {
        CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();
        if (userDetails == null || userDetails.getUser() == null) {
            throw new RuntimeException("Unauthorized: User must be logged in to track behavior");
        }
        return userDetails.getUser().getId();
    }
}
