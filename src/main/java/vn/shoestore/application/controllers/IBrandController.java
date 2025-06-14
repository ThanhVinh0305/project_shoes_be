package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.BrandRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.domain.model.Brand;

@RestController
@RequestMapping("/api/v1/brand")
public interface IBrandController {

  @PostMapping()
  ResponseEntity<BaseResponse<Brand>> createBrand(@RequestBody @Valid BrandRequest request);

  @PutMapping()
  ResponseEntity<BaseResponse<Brand>> updateBrand(@RequestBody @Valid BrandRequest request);

  @DeleteMapping("{id}")
  ResponseEntity<BaseResponse> deleteBrand(@PathVariable Long id);

  @GetMapping("{id}")
  ResponseEntity<BaseResponse<Brand>> getById(@PathVariable Long id);
}
