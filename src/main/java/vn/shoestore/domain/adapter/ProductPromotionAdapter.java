package vn.shoestore.domain.adapter;

import org.springframework.data.domain.Page;
import vn.shoestore.application.request.FindPromotionRequest;
import vn.shoestore.domain.model.ProductPromotion;
import vn.shoestore.domain.model.Promotion;

import java.util.List;
import java.util.Optional;

public interface ProductPromotionAdapter {
  Promotion savePromotion(Promotion promotion);

  void deletePromotion(Long id);

  void saveProductPromotions(List<ProductPromotion> productPromotions);

  Optional<Promotion> findById(Long promotionId);

  List<ProductPromotion> findAllByPromotionId(Long promotionId);

  void deleteAllByProductIds(Long promotionId, List<Long> productIds);

  Page<Promotion> findPromotionByCondition(FindPromotionRequest request);

  List<Promotion> getPromotionByIds(List<Long> ids);

  List<ProductPromotion> getProductPromotionByProductIds(List<Long> productIds);
}
