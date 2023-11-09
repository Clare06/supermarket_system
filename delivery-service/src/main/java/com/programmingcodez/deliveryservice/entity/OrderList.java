package com.programmingcodez.deliveryservice.entity;

import com.programmingcodez.deliveryservice.Enum.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_order_list")
public class OrderList {

    @Id
    private String orderNumber;

    private OrderStatus orderStatus;
}
