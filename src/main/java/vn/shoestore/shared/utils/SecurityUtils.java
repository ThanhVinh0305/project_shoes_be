package vn.shoestore.shared.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.dto.CustomUserDetails;

public class SecurityUtils {
  public static User getCurrentUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && !(authentication.getPrincipal() instanceof String)) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      return userDetails.getUser();
    }
    return null;
  }
}
