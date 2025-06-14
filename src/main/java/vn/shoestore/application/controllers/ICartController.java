package vn.shoestore.application.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.shoestore.application.request.AddToCartRequest;
import vn.shoestore.application.request.UpdateCartAmountRequest;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.application.response.CartResponse;

@RestController
@RequestMapping("/api/v1/cart")
public interface ICartController {
  @GetMapping
  ResponseEntity<BaseResponse<CartResponse>> getCart();

  @PostMapping("/add")
  ResponseEntity<BaseResponse> addToCard(@RequestBody @Valid AddToCartRequest request);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<BaseResponse> deleteFromCart(@PathVariable Long id);

  @PutMapping("/update")
  ResponseEntity<BaseResponse> updateAmount(@RequestBody @Valid UpdateCartAmountRequest request);
}
