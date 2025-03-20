package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.ShippingAddress;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    // Get Order History for the logged-in user
    @GetMapping("/history")
    public ResponseEntity<List<Order>> getOrderHistory(Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getUserOrderHistory(user);
        return ResponseEntity.ok(orders);
    }

    // Get All Orders (Admin Only)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders(){
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Place an order from cart
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody ShippingAddress shippingAddress, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.placeOrder(user,shippingAddress);
        return ResponseEntity.ok(order);
    }

    // Get Orders for logged-in User
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found!"));
        List<Order> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    // Get Order by Id(Admin or User)
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId, Principal principal){
        Optional<Order> orderOptional = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderOptional);
    }

    // Cancel Order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Principal principal){
        User user = userService.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("User not found!"));
        orderService.cancelOrder(orderId, user);
        return ResponseEntity.ok("Order cancelled successfully!");
    }
}
