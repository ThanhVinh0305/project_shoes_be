package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.shoestore.application.controllers.IBehaviorController;
import vn.shoestore.application.request.behavior.*;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.dto.CustomUserDetails;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.shared.utils.AuthUtils;
import vn.shoestore.usecases.logic.behavior.ITrackBehaviorUseCase;

@Component
@RequiredArgsConstructor
public class BehaviorControllerImpl implements IBehaviorController {
  
  private final ITrackBehaviorUseCase trackBehaviorUseCase;
  
  @Override
  public ResponseEntity<BaseResponse> trackView(TrackViewRequest request) {
    Long userId = getCurrentUserId();
    trackBehaviorUseCase.trackProductView(userId, request.getProductId(), request.getViewDuration());
    return ResponseFactory.success();
  }
  
  @Override
  public ResponseEntity<BaseResponse> trackSearch(TrackSearchRequest request) {
    Long userId = getCurrentUserId();
    trackBehaviorUseCase.trackSearch(userId, request.getKeyword(), request.getFilters(), request.getResultCount());
    return ResponseFactory.success();
  }
  
  @Override
  public ResponseEntity<BaseResponse> trackClick(TrackClickRequest request) {
    Long userId = getCurrentUserId();
    trackBehaviorUseCase.trackClick(userId, request.getProductId(), request.getSource());
    return ResponseFactory.success();
  }
  
  @Override
  public ResponseEntity<BaseResponse> trackAddToCart(TrackCartRequest request) {
    Long userId = getCurrentUserId();
    trackBehaviorUseCase.trackAddToCart(userId, request.getProductId());
    return ResponseFactory.success();
  }
  
  /**
   * Get current user ID from security context
   * Returns null if user is not authenticated (guest user)
   */
  private Long getCurrentUserId() {
    try {
      CustomUserDetails userDetails = AuthUtils.getAuthUserDetails();
      if (userDetails != null && userDetails.getUser() != null) {
        return userDetails.getUser().getId();
      }
    } catch (Exception e) {
      // User not authenticated, return null for guest tracking
    }
    return null;
  }
}
