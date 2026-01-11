package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ChatAnalytics;

public interface ChatAnalyticsAdapter {
  
  void trackEvent(ChatAnalytics analytics);
}
