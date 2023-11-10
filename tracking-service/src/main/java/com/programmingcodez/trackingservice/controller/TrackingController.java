package com.programmingcodez.trackingservice.controller;

import com.programmingcodez.trackingservice.entity.TrackingInfo;
import com.programmingcodez.trackingservice.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping("/{orderNumber}")

    public ResponseEntity<TrackingInfo> trackOrder(@PathVariable String orderNumber){
        return this.trackingService.trackOrder(orderNumber);
    }

    @PostMapping("/setStatus")
    public ResponseEntity<Void> setOrderStatus(@RequestBody TrackingInfo orderInfo){
        return this.trackingService.setOrderStatus(orderInfo);
    }

}
