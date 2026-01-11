package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.ChatFAQAdapter;
import vn.shoestore.domain.model.ChatFAQ;
import vn.shoestore.infrastructure.repository.entity.ChatFAQEntity;
import vn.shoestore.infrastructure.repository.repository.ChatFAQRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ChatFAQAdapterImpl implements ChatFAQAdapter {
  
  private final ChatFAQRepository faqRepository;
  
  @Override
  @Transactional
  public ChatFAQ save(ChatFAQ faq) {
    ChatFAQEntity entity = ModelMapperUtils.mapper(faq, ChatFAQEntity.class);
    ChatFAQEntity saved = faqRepository.save(entity);
    return ModelMapperUtils.mapper(saved, ChatFAQ.class);
  }
  
  @Override
  public List<ChatFAQ> findAll() {
    List<ChatFAQEntity> entities = faqRepository.findAll();
    return ModelMapperUtils.mapList(entities, ChatFAQ.class);
  }
  
  @Override
  public List<ChatFAQ> findActive() {
    List<ChatFAQEntity> entities = faqRepository.findByIsActiveTrueOrderByPriorityDescViewCountDesc();
    return ModelMapperUtils.mapList(entities, ChatFAQ.class);
  }
  
  @Override
  public List<ChatFAQ> searchByQuestion(String keyword) {
    List<ChatFAQEntity> entities = faqRepository.searchByQuestion(keyword);
    return ModelMapperUtils.mapList(entities, ChatFAQ.class);
  }
  
  @Override
  @Transactional
  public void incrementViewCount(Long faqId) {
    faqRepository.findById(faqId).ifPresent(entity -> {
      entity.setViewCount(entity.getViewCount() + 1);
      faqRepository.save(entity);
    });
  }
  
  @Override
  @Transactional
  public void incrementHelpfulCount(Long faqId) {
    faqRepository.findById(faqId).ifPresent(entity -> {
      entity.setHelpfulCount(entity.getHelpfulCount() + 1);
      faqRepository.save(entity);
    });
  }
}
