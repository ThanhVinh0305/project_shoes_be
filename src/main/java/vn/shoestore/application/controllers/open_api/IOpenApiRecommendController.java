package vn.shoestore.application.controllers.open_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.RecommendBlocksResponse;

@RequestMapping("/open-api/recommend")
@RestController
public interface IOpenApiRecommendController {

  /**
   * Trả về các block gợi ý:
   * - guestSale: sản phẩm đang giảm giá (nếu chưa đăng nhập)
   * - guestToday: random sản phẩm (chưa đăng nhập)
   * - userTopSearch: top sản phẩm gợi ý từ lịch sử tìm kiếm của user
   * - userTopViewed: top sản phẩm user xem nhiều nhất
   */
  @GetMapping("blocks")
  ResponseEntity<BaseResponse<RecommendBlocksResponse>> getBlocks(
      @RequestParam(value = "userId", required = false) Long userId);
}


