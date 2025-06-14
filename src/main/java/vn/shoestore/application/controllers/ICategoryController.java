package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.CategoryRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.domain.model.Category;

@RestController
@RequestMapping("/api/v1/category")
public interface ICategoryController {
  @PostMapping
  ResponseEntity<BaseResponse<Category>> createCategory(
      @RequestBody @Valid CategoryRequest request);

  @PutMapping
  ResponseEntity<BaseResponse<Category>> updateCategory(
      @RequestBody @Valid CategoryRequest request);

  @DeleteMapping("{id}")
  ResponseEntity<BaseResponse> deleteCategory(@PathVariable Long id);

  @GetMapping("{id}")
  ResponseEntity<BaseResponse<Category>> getById(@PathVariable Long id);
}
