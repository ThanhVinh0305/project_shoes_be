package vn.shoestore.application.controllers.open_api;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.CommentResponse;

@RequestMapping("/open-api/comments")
@RestController
public interface IOpenApiCommentController {
  @GetMapping("get-all/{productId}")
  ResponseEntity<BaseResponse<List<CommentResponse>>> getComments(@PathVariable Long productId);
}
