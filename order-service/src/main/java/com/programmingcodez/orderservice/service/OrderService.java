package com.programmingcodez.orderservice.service;

import com.programmingcodez.orderservice.controller.ChargeController;
import com.programmingcodez.orderservice.dto.*;
import com.programmingcodez.orderservice.entity.Order;
import com.programmingcodez.orderservice.entity.OrderLineItem;
import com.programmingcodez.orderservice.exception.ItemsNotInStockException;
import com.programmingcodez.orderservice.repository.OrderRepository;
import com.stripe.exception.StripeException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    public static final String PLACE_ORDER_CIRCUIT_BREAKER = "place-order-circuit-breaker";
    public static final String PLACE_ORDER_RETRY = "place-order-retry";

    @Transactional
    public void removeOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
    @Transactional
    @CircuitBreaker(name = PLACE_ORDER_CIRCUIT_BREAKER, fallbackMethod = "placeOrderFallback")
    @Retry(name = PLACE_ORDER_RETRY)
    public InventoryUpdateRequestDto placeOrder(OrderRequest orderRequest, String username) throws ItemsNotInStockException {

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
            String inventory= webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8084/api/inventory")
                    .bodyValue(skuCodes)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (inventory.equals("inventory-updated")) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setUserName(username);
            order.setOrderLineItemsList(orderLineItems);
            order.setStatus(Order.OrderStatus.PENDING);
            order.setTimestamp(new Timestamp(System.currentTimeMillis()));
            orderRepository.save(order);
            return new InventoryUpdateRequestDto(order.getId(), skuCodes);
            }else {
                throw new IllegalStateException("Inventory-error");
            }
        } else {
            throw  new ItemsNotInStockException(invenResponse);
        }
    }

    // Fallback method to handle place-order-circuit-breaker open state
    private InventoryUpdateRequestDto placeOrderFallback(OrderRequest orderRequest, String username, Throwable throwable) {
        System.out.println("Error: Service is currently unavailable. Please try again later.");
        return null;
    }
    @Transactional
    public String completeOrder(CompleteRequestDto completeRequestDto) {
        String chargeEndpoint = "http://localhost:8081/api/charge";
        Optional<Order> trnsOrder = orderRepository.findById(completeRequestDto.getInventoryUpdateRequest().getOrderID());
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
                //set tracking status
                trnsOrder.get().setTrackingStatus(Order.TrackingStatus.PROCESSING);
                TrackingInfo trackingInfo = new TrackingInfo(trnsOrder.get().getOrderNumber(), Order.TrackingStatus.PROCESSING);

                this.webClientBuilder.build()
                                .post()
                                .uri("http://localhost:8083/api/tracking/setStatus")
                                .bodyValue(trackingInfo)
                                .retrieve()
                                .toBodilessEntity()
                                .block();

                orderRepository.save(trnsOrder.get());
                return "order completed";
            }
        } catch (WebClientRequestException | WebClientResponseException  ex) {
            throw  ex;
        }

        return  "Order Cancelled";
    }
    @Scheduled(fixedRate = 6000)
    public void cleanupPendingOrders() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Order> pendingOrders = orderRepository.findByStatusAndTimestampBefore(Order.OrderStatus.PENDING, Timestamp.valueOf(tenMinutesAgo));
        for (Order order : pendingOrders) {
            List<OrderLineItem> orderLineItems= order.getOrderLineItemsList();
            List<InventoryRequest> skuCodes = orderLineItems.stream()
                    .map(item-> new InventoryRequest(item.getSkuCode(),item.getQuantity()))
                    .toList();
            String rollBackInventory= webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8084/api/inventory/roll-back")
                    .bodyValue(skuCodes)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if(rollBackInventory.equals("inventory-updated")) {
                orderRepository.delete(order);
            }
        }
    }
    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItems = new OrderLineItem();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
