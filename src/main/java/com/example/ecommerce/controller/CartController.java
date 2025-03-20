package com.example.ecommerce.controller;


import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    // Add Item to Cart
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addToCart(@RequestParam Long productId, @RequestParam Integer quantity, Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found!"));
        CartItem cartItem = cartService.addItemToCart(user,productId,quantity);

        // Convert to DTO
        CartItemDTO response = new CartItemDTO();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());

        return ResponseEntity.ok(response);
    }

    // Get User's Cart Items
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getUserCart(Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found"));
        List<CartItem> cartItems = cartService.getUserCartItems(user);

        // Convert List<CartItem> to List<CartItemDTO>
        List<CartItemDTO> cartItemDTOs = cartItems.stream().map(cartItem ->{
            CartItemDTO dto = new CartItemDTO();
            dto.setId(cartItem.getId());
            dto.setProductId(cartItem.getProduct().getId());
            dto.setProductName(cartItem.getProduct().getName());
            dto.setProductPrice(cartItem.getProduct().getPrice());
            dto.setQuantity(cartItem.getQuantity());
            dto.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());
            return dto;
        }).toList();
        return ResponseEntity.ok(cartItemDTOs);
    }

    // Update Cart Item Quantity
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long cartItemId,@RequestParam Integer quantity,Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found!"));
        CartItem cartItem = cartService.updateCartItem(cartItemId,quantity,user);

        // Convert to DTO
        CartItemDTO response = new CartItemDTO();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());

        return ResponseEntity.ok(response);
    }

    // Remove Item from Cart
    @DeleteMapping("remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId,Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found!"));
        cartService.removeCartItem(cartItemId,user);
        return ResponseEntity.ok("Cart item removed successfully!");
    }
}
