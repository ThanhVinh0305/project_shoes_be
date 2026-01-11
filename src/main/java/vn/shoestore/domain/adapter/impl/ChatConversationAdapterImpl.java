package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.ChatConversationAdapter;
import vn.shoestore.domain.model.ChatConversation;
import vn.shoestore.infrastructure.repository.entity.ChatConversationEntity;
import vn.shoestore.infrastructure.repository.repository.ChatConversationRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class ChatConversationAdapterImpl implements ChatConversationAdapter {
  
  private final ChatConversationRepository conversationRepository;
  
  @Override
  @Transactional
  public ChatConversation save(ChatConversation conversation) {
    ChatConversationEntity entity = ModelMapperUtils.mapper(conversation, ChatConversationEntity.class);
    ChatConversationEntity saved = conversationRepository.save(entity);
    return ModelMapperUtils.mapper(saved, ChatConversation.class);
  }
  
  @Override
  public Optional<ChatConversation> findById(Long id) {
    return conversationRepository.findById(id)
        .map(entity -> ModelMapperUtils.mapper(entity, ChatConversation.class));
  }
  
  @Override
  public Optional<ChatConversation> findBySessionId(String sessionId) {
    return conversationRepository.findBySessionId(sessionId)
        .map(entity -> ModelMapperUtils.mapper(entity, ChatConversation.class));
  }
  
  @Override
  public List<ChatConversation> findByUserId(Long userId) {
    List<ChatConversationEntity> entities = conversationRepository.findByUserId(userId);
    return ModelMapperUtils.mapList(entities, ChatConversation.class);
  }
  
  @Override
  public Optional<ChatConversation> findActiveConversationByUserId(Long userId) {
    return conversationRepository.findActiveConversationByUserId(userId)
        .map(entity -> ModelMapperUtils.mapper(entity, ChatConversation.class));
  }
  
  @Override
  public Optional<ChatConversation> findActiveConversationBySessionId(String sessionId) {
    return conversationRepository.findActiveConversationBySessionId(sessionId)
        .map(entity -> ModelMapperUtils.mapper(entity, ChatConversation.class));
  }
  
  @Override
  @Transactional
  public void closeConversation(Long conversationId) {
    conversationRepository.findById(conversationId).ifPresent(entity -> {
      entity.setStatus("CLOSED");
      entity.setClosedDate(LocalDateTime.now());
      conversationRepository.save(entity);
    });
  }
}
