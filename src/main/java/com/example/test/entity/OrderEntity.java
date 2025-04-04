package com.example.test.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public class OrderEntity {

    @Id
    private Integer id;
    private int amount;
    private double price;

    @Column("type")  // Čuva se kao string u bazi
    private String typeString;

    @Transient  // Koristi se u kodu, ne pradstavlja kolonu u bazi
    private OrderType type;

    public OrderType getType() {
        return OrderType.valueOf(typeString);  // Konvertujem string u ENUM
    }

    public void setType(OrderType type) {
        this.type = type;
        this.typeString = type.name();  // Konvertujem ENUM u string pre upisa u bazu
    }
    public OrderEntity() {
    }

    public OrderEntity(int amount, double price, OrderType type) {
        this.amount = amount;
        this.price = price;
        this.type = type;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
