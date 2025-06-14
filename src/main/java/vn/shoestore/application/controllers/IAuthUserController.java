package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.request.ChangePasswordRequest;
import vn.shoestore.application.response.BaseResponse;

@RestController
@RequestMapping("/api/v1/auth/")
public interface IAuthUserController {

  @PostMapping("/change-password")
  ResponseEntity<BaseResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request);

  @PostMapping("/logout")
  ResponseEntity<BaseResponse> logout();
}
