package com.programmingcodez.trackingservice.entity;


import com.programmingcodez.trackingservice.Enum.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "tracking_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TrackingInfo {

    @Id
    private String orderNumber;

    private OrderStatus orderStatus;


    private String carrierId;
}
