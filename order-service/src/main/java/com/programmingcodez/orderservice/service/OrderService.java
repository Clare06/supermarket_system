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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    @Transactional
    public void removeOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
    @Transactional
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
                .uri("http://inventory-service/api/inventory/stock")
                .bodyValue(skuCodes) // Set the list as the request body
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
        Boolean notAllStockIn=invenResponse.stream().anyMatch(item -> item.isInStock()==false);

        if(!notAllStockIn){
            String inventory= webClientBuilder.build()
                    .put()
                    .uri("http://inventory-service/api/inventory")
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
    @Transactional
    public String completeOrder(CompleteRequestDto completeRequestDto) {
        String chargeEndpoint = "http://order-service/api/charge";
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
                                .uri("http://tracking-service/api/tracking/createStatus")
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
                    .uri("http://inventory-service/api/inventory/roll-back")
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

    public ResponseEntity<Void> updateTrackingStatus (TrackingInfo trackingInfo){
        try{
            Order order = this.orderRepository.findByOrderNumber(trackingInfo.getOrderNumber());
            order.setTrackingStatus(trackingInfo.getOrderStatus());
            this.orderRepository.save(order);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
