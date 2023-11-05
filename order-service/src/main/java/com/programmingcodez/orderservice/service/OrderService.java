package com.programmingcodez.orderservice.service;

import com.programmingcodez.orderservice.dto.InventoryRequest;
import com.programmingcodez.orderservice.dto.OrderLineItemsDto;
import com.programmingcodez.orderservice.dto.OrderRequest;
import com.programmingcodez.orderservice.entity.Order;
import com.programmingcodez.orderservice.entity.OrderLineItem;
import com.programmingcodez.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<InventoryRequest> skuCodes = orderLineItems.stream()
                .map(item-> new InventoryRequest(item.getSkuCode(),item.getQuantity()))
                .toList();

//        System.out.println(skuCodes);
        orderRepository.save(order);
        return skuCodes.toString()+orderLineItems.toString();
//        return skuCodes.toString();
    }

    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItems = new OrderLineItem();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
