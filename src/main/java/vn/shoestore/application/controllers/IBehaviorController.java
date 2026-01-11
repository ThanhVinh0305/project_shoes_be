package vn.shoestore.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.behavior.*;
import vn.shoestore.application.response.BaseResponse;

@RestController
@RequestMapping("/api/v1/behavior")
public interface IBehaviorController {
  
  /**
   * Track product view
   */
  @PostMapping("/view")
  ResponseEntity<BaseResponse> trackView(@RequestBody TrackViewRequest request);
  
  /**
   * Track search
   */
  @PostMapping("/search")
  ResponseEntity<BaseResponse> trackSearch(@RequestBody TrackSearchRequest request);
  
  /**
   * Track click
   */
  @PostMapping("/click")
  ResponseEntity<BaseResponse> trackClick(@RequestBody TrackClickRequest request);
  
  /**
   * Track add to cart
   */
  @PostMapping("/add-to-cart")
  ResponseEntity<BaseResponse> trackAddToCart(@RequestBody TrackCartRequest request);
}
