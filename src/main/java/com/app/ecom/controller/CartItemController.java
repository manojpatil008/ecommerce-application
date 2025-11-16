package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest cartItemRequest){
        if(!cartItemService.addToCart(userId, cartItemRequest))
            return ResponseEntity.badRequest().body("Product not found or Product Out of Stock or User not found");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId){
        if(cartItemService.deleteItemFromCart(userId, productId))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/items/{userId}")
    public ResponseEntity<List<CartItem>> getCartItemsByUserId(@PathVariable String userId){
        return ResponseEntity.ok(cartItemService.getCartItemsByUserId(userId));
    }
}
