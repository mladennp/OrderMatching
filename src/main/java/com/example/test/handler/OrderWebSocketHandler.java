package com.example.test.handler;

import com.example.test.entity.OrderEntity;
import com.example.test.entity.OrderType;
import com.example.test.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public OrderWebSocketHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // Provera da li je zahtev za top BUY ili SELL
        if ("TOP_BUY".equalsIgnoreCase(payload)) {
            orderService.getTop10BuyOrders()
                    .collectList()
                    .subscribe(orders -> {
                        try {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(orders)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else if ("TOP_SELL".equalsIgnoreCase(payload)) {
            orderService.getTop10SellOrders()
                    .collectList()
                    .subscribe(orders -> {
                        try {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(orders)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            // Parsiranje ordera iz JSON-a
            OrderEntity order = objectMapper.readValue(payload, OrderEntity.class);


            orderService.createOrder(order)
                    .doOnSuccess(savedOrder -> {
                        //sendNewOrderUpdates
                    })
                    .flatMap(savedOrder -> {
                       // Proveravamo da li postoji odgovarajući suprotan nalog
                        if (savedOrder.getType() == OrderType.BUY) {
                            return orderService.getMatchingSellOrder(savedOrder)
                                    .doOnSuccess(matchingSellOrder -> {
                                        if (matchingSellOrder != null) {
                                            try {
                                                session.sendMessage(new TextMessage("Matching sell order found. The transaction will be processed."));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                        } else if (savedOrder.getType() == OrderType.SELL) {
                            return orderService.getMatchingBuyOrder(savedOrder)
                                    .doOnSuccess(matchingBuyOrder -> {
                                        if (matchingBuyOrder != null) {
                                            try {
                                                session.sendMessage(new TextMessage("Matching buy order found. The transaction will be processed."));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }


    public void sendNewOrderUpdate(OrderEntity orderSaved) {
        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(orderSaved);
            for (WebSocketSession session : sessions) {
                session.sendMessage(new TextMessage(jsonResponse));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }
}