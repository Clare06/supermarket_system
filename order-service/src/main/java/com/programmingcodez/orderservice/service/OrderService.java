package com.programmingcodez.orderservice.service;

import com.programmingcodez.orderservice.controller.ChargeController;
import com.programmingcodez.orderservice.dto.*;
import com.programmingcodez.orderservice.entity.Order;
import com.programmingcodez.orderservice.entity.OrderLineItem;
import com.programmingcodez.orderservice.exception.ItemsNotInStockException;
import com.programmingcodez.orderservice.repository.OrderRepository;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
//    private final ChargeController chargeController;
    @Transactional
    public void removeOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
    @Transactional
    public Long placeOrder(OrderRequest orderRequest) throws ItemsNotInStockException {

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
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
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderLineItemsList(orderLineItems);
            order.setStatus(Order.OrderStatus.PENDING);
            orderRepository.save(order);
            return order.getId();
        } else {
            throw  new ItemsNotInStockException(invenResponse);
        }
    }
    @Transactional
    public String completeOrder(CompleteRequestDto completeRequestDto) {
        String chargeEndpoint = "http://localhost:8081/api/charge";
        Optional<Order> trnsOrder = orderRepository.findById(completeRequestDto.getOrderId());
        try {
            ChargeResponse response = webClientBuilder.build()
                    .post()
                    .uri(chargeEndpoint)
                    .bodyValue(completeRequestDto.getChargeRequest())
                    .retrieve()
                    .bodyToMono(ChargeResponse.class)
                    .block();
            if (response.getStatus().equals("succeeded")){
                trnsOrder.get().setStatus(Order.OrderStatus.COMPLETED);
                orderRepository.save(trnsOrder.get());
                return "order completed";
            }
        } catch (WebClientRequestException | WebClientResponseException  ex) {
            throw  ex;
        }

        return  "Order Cancelled";
    }
    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItems = new OrderLineItem();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
