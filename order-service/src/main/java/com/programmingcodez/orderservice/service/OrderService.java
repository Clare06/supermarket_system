package com.programmingcodez.orderservice.service;

import com.programmingcodez.orderservice.dto.InventoryRequest;
import com.programmingcodez.orderservice.dto.InventoryResponse;
import com.programmingcodez.orderservice.dto.OrderLineItemsDto;
import com.programmingcodez.orderservice.dto.OrderRequest;
import com.programmingcodez.orderservice.entity.Order;
import com.programmingcodez.orderservice.entity.OrderLineItem;
import com.programmingcodez.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
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
        List<InventoryResponse> invenResponse = webClientBuilder.build()
                .post()
                .uri("http://localhost:8084/api/inventory/stock")
                .bodyValue(skuCodes) // Set the list as the request body
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
        Boolean notAllStockIn=invenResponse.stream().anyMatch(item -> item.isInStock()==false);

        if(!notAllStockIn){
            orderRepository.save(order);
            return "added";
        }
//        System.out.println(notAllStockIn);

        return "not added";
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
