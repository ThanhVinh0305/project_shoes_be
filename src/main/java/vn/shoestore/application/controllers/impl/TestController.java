package vn.shoestore.application.controllers.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.domain.model.User;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.shared.utils.SecurityUtils;

@RequestMapping("/api/v1")
@RestController
public class TestController {

  @GetMapping("/ping")
  public ResponseEntity<BaseResponse<User>> ping() {
    return ResponseFactory.success(SecurityUtils.getCurrentUserDetails());
  }
}
