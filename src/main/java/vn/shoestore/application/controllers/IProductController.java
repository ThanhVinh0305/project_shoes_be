package vn.shoestore.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.ProductRequest;
import vn.shoestore.application.response.BaseResponse;

@RestController
@RequestMapping("/api/v1/product/")
public interface IProductController {

  @PostMapping("/save")
  ResponseEntity<BaseResponse> saveOrUpdateProduct(@RequestBody ProductRequest request);

  @DeleteMapping("{id}")
  ResponseEntity<BaseResponse> deleteProduct(@PathVariable Long id);
}
