package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import vn.shoestore.domain.adapter.SearchHistoryAdapter;
import vn.shoestore.domain.model.SearchHistory;
import vn.shoestore.infrastructure.repository.entity.SearchHistoryEntity;
import vn.shoestore.infrastructure.repository.repository.SearchHistoryRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.time.LocalDateTime;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class SearchHistoryAdapterImpl implements SearchHistoryAdapter {
  
  private final SearchHistoryRepository searchHistoryRepository;
  
  @Override
  public SearchHistory save(SearchHistory searchHistory) {
    SearchHistoryEntity entity = ModelMapperUtils.mapper(searchHistory, SearchHistoryEntity.class);
    SearchHistoryEntity saved = searchHistoryRepository.save(entity);
    return ModelMapperUtils.mapper(saved, SearchHistory.class);
  }
  
  @Override
  public List<SearchHistory> findByUserId(Long userId) {
    List<SearchHistoryEntity> entities = searchHistoryRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, SearchHistory.class);
  }
  
  @Override
  public List<SearchHistory> findRecentByUserId(Long userId, int limit) {
    List<SearchHistoryEntity> entities = searchHistoryRepository.findRecentByUserId(userId, limit);
    return ModelMapperUtils.mapList(entities, SearchHistory.class);
  }
  
  @Override
  public List<SearchHistory> findBySearchKeyword(String keyword) {
    List<SearchHistoryEntity> entities = searchHistoryRepository.findBySearchKeyword(keyword);
    return ModelMapperUtils.mapList(entities, SearchHistory.class);
  }
  
  @Override
  public List<String> findTopKeywords(int limit) {
    return searchHistoryRepository.findTopKeywords(limit);
  }
  
  @Override
  public List<String> findTopKeywordsByUserId(Long userId, int limit) {
    return searchHistoryRepository.findTopKeywordsByUserId(userId, limit);
  }
  
  @Override
  public Long countByUserIdAndCreatedDateAfter(Long userId, LocalDateTime after) {
    return searchHistoryRepository.countByUserIdAndCreatedDateAfter(userId, after);
  }
}
