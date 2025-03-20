package com.example.ecommerce.service;


import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    // place an Order from Cart
    @Transactional
    public Order placeOrder(User user, ShippingAddress shippingAddress){
        // Get cart items
        List<CartItem> cartItems = cartService.getUserCartItems(user);
        if(cartItems.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cartItems){
            Product product = cartItem.getProduct();
            // check stock
            if(product.getStock() < cartItem.getQuantity()){
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            // Deduct Stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);

            totalAmount += cartItem.getQuantity() * product.getPrice();

//            order.getOrderItems().add(orderItem);
        }
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Save OrderItems in database
//        for(OrderItem orderItem : order.getOrderItems()){
//            orderItemRepository.save(orderItem);
//        }
        // Clear cart after successful order placement
        cartService.clearCart(user);
        return savedOrder;
    }

    // Get Orders by User
    public List<Order> getOrdersByUser(User user){
        return orderRepository.findByUser(user);
    }

    // Get Order by Id
    public Optional<Order> getOrderById(Long orderId){
        return orderRepository.findById(orderId);
    }

    // Cancel Order
    @Transactional
    public void cancelOrder(Long orderId,User user){
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if(orderOpt.isPresent()){
            Order order = orderOpt.get();
            if(!order.getUser().getId().equals(user.getId())){
                throw new RuntimeException("You can only cancel your own orders!");
            }
            if(!order.getStatus().equals(OrderStatus.PENDING )){
                throw new RuntimeException("Only pending orders can be cancelled!");
            }

            // Restock products when order is cancelled
            for(OrderItem item : order.getOrderItems()){
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }else{
            throw new RuntimeException("Order not found!");
        }
    }

    // Get order history for a specific user
    public List<Order> getUserOrderHistory(User user){
        return orderRepository.findByUser(user);
    }

    // Get all orders (for admin)
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

}
