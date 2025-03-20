package com.example.ecommerce.service;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // Add item to cart
    public CartItem addItemToCart(User user, Long productId,int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product not found:"));

        // Check if item already in cart
        List<CartItem> cartItems = cartRepository.findByUser(user);
        for(CartItem item : cartItems){
            if(item.getProduct().getId().equals(productId)){
                item.setQuantity(item.getQuantity() + quantity);
                return cartRepository.save(item);
            }
        }
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    // Get User Cart Items
    public List<CartItem> getUserCartItems(User user){
        return cartRepository.findByUser(user);
    }

    // Update Cart Item Quantity
    public CartItem updateCartItem(Long cartItemId,int quantity,User user){
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(()-> new RuntimeException("Cart Item not found"));
        if(!cartItem.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You can only update your own cart items");
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    // Remove Item from Cart
    public void removeCartItem(Long cartItemId,User user){
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(()-> new RuntimeException("Cart Item not found"));
        if(!cartItem.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You can only remove your own cart items");
        }
        cartRepository.deleteById(cartItemId);
    }

    // Clear Cart after Order Placement
    public void clearCart(User user){
        cartRepository.deleteByUser(user);
    }

}
