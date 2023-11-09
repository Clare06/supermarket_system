package com.programmingcodez.trackingservice.service;

import com.programmingcodez.trackingservice.Enum.OrderStatus;
import com.programmingcodez.trackingservice.dto.AcceptDeliveryDto;
import com.programmingcodez.trackingservice.dto.OrderListDto;
import com.programmingcodez.trackingservice.entity.TrackingInfo;
import com.programmingcodez.trackingservice.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    public ResponseEntity<List<TrackingInfo>> getAllOrders(){
        return new ResponseEntity<>(this.trackingRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<TrackingInfo> trackOrder(String orderNumber){
        if(this.trackingRepository.existsById(orderNumber)){
            return new ResponseEntity<>(this.trackingRepository.findById(orderNumber).orElse(null), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> createOrderTracking(TrackingInfo orderInfo){
        this.trackingRepository.save(orderInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> acceptDelivery (AcceptDeliveryDto acceptDeliveryDto){
        if (this.trackingRepository.existsById(acceptDeliveryDto.getOrderNumber())){
            TrackingInfo trackingInfo = this.trackingRepository.findById(acceptDeliveryDto.getOrderNumber()).orElse(null);
            trackingInfo.setCarrierId(acceptDeliveryDto.getCarrierId());
            trackingInfo.setOrderStatus(OrderStatus.SHIPPED);
            this.trackingRepository.save(trackingInfo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> completeDelivery (String orderNumber){
        if (this.trackingRepository.existsById(orderNumber)){
            TrackingInfo trackingInfo = this.trackingRepository.findById(orderNumber).orElse(null);
            trackingInfo.setOrderStatus(OrderStatus.DELIVERED);
            this.trackingRepository.save(trackingInfo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<List<OrderListDto>> getOrdersToDeliver(){

        List<OrderListDto> orderListDto =  this.trackingRepository.findByOrderStatus(OrderStatus.PROCESSING)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ResponseEntity<>(orderListDto, HttpStatus.OK);

    }


    private OrderListDto mapToDto(TrackingInfo trackingInfo){
        OrderListDto orderListDto = new OrderListDto();
        orderListDto.setOrderNumber(trackingInfo.getOrderNumber());
        orderListDto.setOrderStatus(trackingInfo.getOrderStatus());
        return orderListDto;
    }
}
