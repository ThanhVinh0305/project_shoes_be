package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.FindUserRequest;
import vn.shoestore.application.request.UpdateUserRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.UserResponse;
import vn.shoestore.domain.model.User;

@RestController
@RequestMapping("/api/v1/users/")
public interface IUserController {
  @PostMapping("/get-all")
  ResponseEntity<BaseResponse<UserResponse>> findAllUser(FindUserRequest request);

  @GetMapping("/get-one/{id}")
  ResponseEntity<BaseResponse<User>> findUserById(@PathVariable Long id);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<BaseResponse> delete(@PathVariable Long id);

  @PutMapping("/active/{id}")
  ResponseEntity<BaseResponse> active(@PathVariable Long id);

  @PutMapping("/update")
  ResponseEntity<BaseResponse> update(@RequestBody @Valid UpdateUserRequest request);
}
