package com.programmingcodez.orderservice.dto;

import com.programmingcodez.orderservice.entity.Order;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingInfo {

    @Id
    private String orderNumber;

    private Order.TrackingStatus orderStatus;

}
