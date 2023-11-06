package com.programmingcodez.orderservice.controller;

import com.programmingcodez.orderservice.dto.CompleteRequestDto;
import com.programmingcodez.orderservice.dto.InventoryResponse;
import com.programmingcodez.orderservice.dto.OrderRequest;
import com.programmingcodez.orderservice.exception.ItemsNotInStockException;
import com.programmingcodez.orderservice.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    @Autowired
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Long orderId = orderService.placeOrder(orderRequest);
            return ResponseEntity.ok(orderId); // Return the order ID if successful
        } catch (ItemsNotInStockException ex) {
            List<InventoryResponse> inventoryResponses = ex.getInventoryResponses(); // Get the inventory responses from the exception
            if (inventoryResponses != null && !inventoryResponses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inventoryResponses);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Order placement failed due to items not in stock.");
            }
        }
    }
    @PostMapping("complete-order")
    public ResponseEntity<String> completeOrder(@RequestBody CompleteRequestDto completeRequestDto) {
        try {
            String result = orderService.completeOrder(completeRequestDto);
            return ResponseEntity.ok(result);
        } catch (WebClientException ex) {
            orderService.removeOrder(completeRequestDto.getOrderId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }


}