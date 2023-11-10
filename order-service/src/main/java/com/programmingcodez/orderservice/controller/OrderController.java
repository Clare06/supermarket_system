package com.programmingcodez.orderservice.controller;

import com.programmingcodez.orderservice.dto.CompleteRequestDto;
import com.programmingcodez.orderservice.dto.InventoryResponse;
import com.programmingcodez.orderservice.dto.InventoryUpdateRequestDto;
import com.programmingcodez.orderservice.dto.OrderRequest;
import com.programmingcodez.orderservice.exception.ItemsNotInStockException;
import com.programmingcodez.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    @Autowired
    private final OrderService orderService;
    private final WebClient.Builder webClientBuilder;

    @PostMapping("{username}")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest,@PathVariable("username") String username) {
        try {
            InventoryUpdateRequestDto inventoryUpdateRequestDto = orderService.placeOrder(orderRequest,username);
            return ResponseEntity.ok(inventoryUpdateRequestDto); // Return the order ID if successful
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
            orderService.removeOrder(completeRequestDto.getInventoryUpdateRequest().getOrderID());
            String rollBackInventory= webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8084/api/inventory/roll-back")
                    .bodyValue(completeRequestDto.getInventoryUpdateRequest().getInventoryRequests())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/viewOrders/{userName}")
    public List<ViewOrdersDto> viewOrdersByUser(@PathVariable String userName){
        return this.orderService.viewOrdersByUser(userName);
    }

    @PutMapping("updateTracking")
    public ResponseEntity<Void> updateTracking(@RequestBody TrackingInfo trackingInfo){
        return this.orderService.updateTrackingStatus(trackingInfo);
    }
}