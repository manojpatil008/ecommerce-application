package com.app.ecom.service;

import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.model.*;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final CartItemService cartItemService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderService(CartItemService cartItemService, UserRepository userRepository, OrderRepository orderRepository) {
        this.cartItemService = cartItemService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<OrderResponse> createOrder(String userId) {
        //Validate for cart items
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        //Validate for user
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty()){
            return Optional.empty();
        }

        User user = userOpt.get();


        //Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        //Clear the cart
        cartItemService.clearCart(userId);

        return Optional.of(mapOrderToOrderResponse(savedOrder));

    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProduct().getId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }
}
