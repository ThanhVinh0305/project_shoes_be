package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.SearchHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryAdapter {
  
  /**
   * Save a search history entry
   */
  SearchHistory save(SearchHistory searchHistory);
  
  /**
   * Find search history by user ID
   */
  List<SearchHistory> findByUserId(Long userId);
  
  /**
   * Find recent searches by user
   */
  List<SearchHistory> findRecentByUserId(Long userId, int limit);
  
  /**
   * Find searches by keyword
   */
  List<SearchHistory> findBySearchKeyword(String keyword);
  
  /**
   * Find popular search keywords (top N by count)
   */
  List<String> findTopKeywords(int limit);
  
  /**
   * Find user's top keywords (ordered by frequency)
   */
  List<String> findTopKeywordsByUserId(Long userId, int limit);
  
  /**
   * Count searches by user within date range
   */
  Long countByUserIdAndCreatedDateAfter(Long userId, LocalDateTime after);
}
