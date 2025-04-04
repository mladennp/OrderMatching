package com.example.test.configuration;

import com.example.test.handler.OrderWebSocketHandler;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@EnableWebSocket
@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final OrderWebSocketHandler orderWebSocketHandler;

    public WebSocketConfiguration(OrderWebSocketHandler orderWebSocketHandler) {
        this.orderWebSocketHandler = orderWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Registruje webSocket
        registry.addHandler(orderWebSocketHandler, "/ws/orders")
                .setAllowedOrigins("*");

    }


}



