package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.ChatAnalyticsAdapter;
import vn.shoestore.domain.model.ChatAnalytics;
import vn.shoestore.infrastructure.repository.entity.ChatAnalyticsEntity;
import vn.shoestore.infrastructure.repository.repository.ChatAnalyticsRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

@Adapter
@RequiredArgsConstructor
public class ChatAnalyticsAdapterImpl implements ChatAnalyticsAdapter {
  
  private final ChatAnalyticsRepository analyticsRepository;
  
  @Override
  @Transactional
  public void trackEvent(ChatAnalytics analytics) {
    ChatAnalyticsEntity entity = ModelMapperUtils.mapper(analytics, ChatAnalyticsEntity.class);
    analyticsRepository.save(entity);
  }
}
