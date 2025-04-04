package com.example.test.repository;

import com.example.test.entity.OrderEntity;
import com.example.test.entity.OrderType;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Integer> {



    Flux<OrderEntity> findTop10ByTypeOrderByPriceDesc(OrderType type);


    Flux<OrderEntity> findTop10ByTypeOrderByPriceAsc(OrderType type);


    Mono<OrderEntity> save(OrderEntity order);

    // Pronalazi prvi najjeftiniji  SELL order
    Mono<OrderEntity> findFirstByTypeAndPriceLessThanEqualOrderByPriceAsc(OrderType type, double price);

    // Pronalazi prvi nasjkuplji BUY order
    Mono<OrderEntity> findFirstByTypeAndPriceGreaterThanEqualOrderByPriceDesc(OrderType type, double price);

}
