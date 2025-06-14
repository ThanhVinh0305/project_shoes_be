package vn.shoestore.application.controllers;

import com.amazonaws.services.codecommit.model.UpdateCommentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.CreateCommentRequest;
import vn.shoestore.application.request.EditCommentRequest;
import vn.shoestore.application.response.BaseResponse;

@RestController
@RequestMapping("/api/v1/comment")
public interface ICommentController {
  @PostMapping("create")
  ResponseEntity<BaseResponse> createComment(@RequestBody @Valid CreateCommentRequest request);

  @PutMapping("update")
  ResponseEntity<BaseResponse> updateComment(@RequestBody @Valid EditCommentRequest request);

  @DeleteMapping("{id}")
  ResponseEntity<BaseResponse> deleteComment(@PathVariable Long id);
}
