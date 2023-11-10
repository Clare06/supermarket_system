package com.programmingcodez.orderservice.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;
@Entity
@Table(name = "t_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String userName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderLineItem> orderLineItemsList;
    private OrderStatus status;
    private Timestamp timestamp;

    //tracking
    private TrackingStatus trackingStatus;

    public enum TrackingStatus{
        PROCESSING,
        SHIPPED,
        DELIVERED
    }

    public enum OrderStatus{
        PENDING,COMPLETED,FAILED
    }
}

