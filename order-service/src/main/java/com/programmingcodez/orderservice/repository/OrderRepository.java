package com.programmingcodez.orderservice.repository;

import com.programmingcodez.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatusAndTimestampBefore(Order.OrderStatus status, Timestamp timestamp);
}
