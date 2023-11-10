package com.programmingcodez.orderservice.dto;

import com.programmingcodez.orderservice.entity.Order;
import com.programmingcodez.orderservice.entity.OrderLineItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewOrdersDto {

    private String userName;
    private String orderNumber;
    private List<ProductInfoDto> productInfoList;
    private Timestamp timestamp;
    private Order.TrackingStatus trackingStatus;


}
