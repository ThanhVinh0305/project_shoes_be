package vn.shoestore.domain.adapter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vn.shoestore.domain.adapter.ChatMessageAdapter;
import vn.shoestore.domain.model.ChatMessage;
import vn.shoestore.infrastructure.repository.entity.ChatMessageEntity;
import vn.shoestore.infrastructure.repository.repository.ChatMessageRepository;
import vn.shoestore.shared.anotation.Adapter;
import vn.shoestore.shared.utils.ModelMapperUtils;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ChatMessageAdapterImpl implements ChatMessageAdapter {
  
  private final ChatMessageRepository messageRepository;
  
  @Override
  @Transactional
  public ChatMessage save(ChatMessage message) {
    ChatMessageEntity entity = ModelMapperUtils.mapper(message, ChatMessageEntity.class);
    ChatMessageEntity saved = messageRepository.save(entity);
    return ModelMapperUtils.mapper(saved, ChatMessage.class);
  }
  
  @Override
  public List<ChatMessage> findByConversationId(Long conversationId) {
    List<ChatMessageEntity> entities = messageRepository.findByConversationIdOrderByCreatedDateAsc(conversationId);
    return ModelMapperUtils.mapList(entities, ChatMessage.class);
  }
  
  @Override
  public List<ChatMessage> findRecentMessages(Long conversationId, int limit) {
    List<ChatMessageEntity> entities = messageRepository.findRecentMessagesWithLimit(conversationId, limit);
    return ModelMapperUtils.mapList(entities, ChatMessage.class);
  }
}
