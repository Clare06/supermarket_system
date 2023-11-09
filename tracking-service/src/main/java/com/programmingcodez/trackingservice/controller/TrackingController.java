package com.programmingcodez.trackingservice.controller;

import com.programmingcodez.trackingservice.dto.AcceptDeliveryDto;
import com.programmingcodez.trackingservice.dto.OrderListDto;
import com.programmingcodez.trackingservice.entity.TrackingInfo;
import com.programmingcodez.trackingservice.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping("/{orderNumber}")
    public ResponseEntity<TrackingInfo> trackOrder(@PathVariable String orderNumber){
        return this.trackingService.trackOrder(orderNumber);
    }

    @GetMapping("/trackAll")
    public ResponseEntity<List<TrackingInfo>> trackAllOrders(){
        return this.trackingService.getAllOrders();
    }

    @PostMapping("/createStatus")
    public ResponseEntity<Void> createOrderTracking(@RequestBody TrackingInfo orderInfo){
        return this.trackingService.createOrderTracking(orderInfo);
    }

    @GetMapping("/toDeliver")
    public ResponseEntity<List<OrderListDto>> getOrdersToDeliver(){
        return this.trackingService.getOrdersToDeliver();
    }

    @PutMapping("/acceptDeliver")
    public ResponseEntity<Void> acceptDelivery (@RequestBody AcceptDeliveryDto acceptDeliveryDto){
        return this.trackingService.acceptDelivery(acceptDeliveryDto);
    }

    @PutMapping("/complete/{orderNumber}")
    public ResponseEntity<Void> completeDelivery (@PathVariable String orderNumber){
        return this.trackingService.completeDelivery(orderNumber);
    }
}
