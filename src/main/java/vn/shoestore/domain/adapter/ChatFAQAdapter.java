package vn.shoestore.domain.adapter;

import vn.shoestore.domain.model.ChatFAQ;

import java.util.List;

public interface ChatFAQAdapter {
  
  ChatFAQ save(ChatFAQ faq);
  
  List<ChatFAQ> findAll();
  
  List<ChatFAQ> findActive();
  
  List<ChatFAQ> searchByQuestion(String keyword);
  
  void incrementViewCount(Long faqId);
  
  void incrementHelpfulCount(Long faqId);
}
