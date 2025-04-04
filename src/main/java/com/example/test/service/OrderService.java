package com.example.test.service;


import com.example.test.handler.OrderWebSocketHandler;
import com.example.test.entity.OrderEntity;
import com.example.test.entity.OrderType;
import com.example.test.repository.OrderRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {



    private final OrderRepository orderRepository;
    private final OrderWebSocketHandler orderWebSocketHandler;

    public OrderService(OrderRepository orderRepository, @Lazy OrderWebSocketHandler orderWebSocketHandler) {
        this.orderRepository = orderRepository;
        this.orderWebSocketHandler= orderWebSocketHandler;
    }

    public Mono<OrderEntity> createOrder(OrderEntity order) {
        return orderRepository.save(order)
                .doOnSuccess(orderWebSocketHandler::sendNewOrderUpdate)
                .doOnError(error -> System.err.println("Error saving order: " + error.getMessage()));
    }

    /*public Flux<OrderEntity> getTop10BuyOrders() {
        return orderRepository.findAll()
                .filter(order -> order.getType()==OrderType.BUY)
                .sort(Comparator.comparing(OrderEntity::getPrice).reversed())
                .take(10);
    }*/

    /*public Flux<OrderEntity> getTop10SellOrders() {
        return orderRepository.findAll()
                .filter(order -> order.getType() == OrderType.SELL)
                .sort(Comparator.comparing(OrderEntity::getPrice))
                .take(10);
    }*/


    public Flux<OrderEntity> getTop10BuyOrders() {
        return orderRepository.findTop10ByTypeOrderByPriceDesc(OrderType.BUY);
    }

    public Flux<OrderEntity> getTop10SellOrders() {
        return orderRepository.findTop10ByTypeOrderByPriceAsc(OrderType.SELL);
    }

   /*public Mono<OrderEntity> getMatchingSellOrder(OrderEntity buyOrder) {
        return orderRepository.findFirstByTypeAndPriceLessThanEqualOrderByPriceAsc(OrderType.SELL, buyOrder.getPrice());
    }*/

   /* public Mono<OrderEntity> getMatchingSellOrder(OrderEntity buyOrder) {
        return orderRepository.findTop10ByTypeOrderByPriceAsc(OrderType.SELL)
                .filter(sellOrder -> sellOrder.getPrice() <= buyOrder.getPrice() && sellOrder.getAmount() > 0) // Sell order sa nižom ili jednakom cenom
                .next();
    }*/

    public Mono<OrderEntity> getMatchingSellOrder(OrderEntity buyOrder) {
        return orderRepository.findTop10ByTypeOrderByPriceAsc(OrderType.SELL)
                .filter(sellOrder ->
                        sellOrder.getPrice() == buyOrder.getPrice() &&
                                sellOrder.getAmount() == buyOrder.getAmount() &&
                                sellOrder.getAmount() > 0
                )
                .next();  // Vraća prvi odgovarajući sell order ili null ako ne postoji
    }

    /*public Mono<OrderEntity> getMatchingBuyOrder(OrderEntity sellOrder) {
        return orderRepository.findTop10ByTypeOrderByPriceDesc(OrderType.BUY)
                .filter(buyOrder -> buyOrder.getPrice() >= sellOrder.getPrice() && buyOrder.getAmount() > 0) // Buy order sa višom ili jednakom cenom
                .next();  // Vraća prvi odgovarajući buy order ili null ako ne postoji
    }*/


    public Mono<OrderEntity> getMatchingBuyOrder(OrderEntity sellOrder) {
        return orderRepository.findTop10ByTypeOrderByPriceDesc(OrderType.BUY)
                .filter(buyOrder ->
                        buyOrder.getPrice() == sellOrder.getPrice() &&
                                buyOrder.getAmount() == sellOrder.getAmount() &&
                                buyOrder.getAmount() > 0
                )
                .next();  // Vraća prvi odgovarajući buy order ili null ako ne postoji
    }

    // Traži najskuplji BUY order koji može da pokrije SELL order
    /*public Mono<OrderEntity> getMatchingBuyOrder(OrderEntity sellOrder) {
        return orderRepository.findFirstByTypeAndPriceGreaterThanEqualOrderByPriceDesc(OrderType.BUY, sellOrder.getPrice());
    }*/






}
