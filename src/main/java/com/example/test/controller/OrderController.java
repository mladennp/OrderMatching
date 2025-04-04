package com.example.test.controller;

import com.example.test.entity.OrderEntity;
import com.example.test.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<OrderEntity> createOrder(@RequestBody OrderEntity order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/top-buy")
    public Flux<OrderEntity> getTopBuyOrders() {
        return orderService.getTop10BuyOrders();
    }

    @GetMapping("/top-sell")
    public Flux<OrderEntity> getTopSellOrders() {
        return orderService.getTop10SellOrders();
    }


}
