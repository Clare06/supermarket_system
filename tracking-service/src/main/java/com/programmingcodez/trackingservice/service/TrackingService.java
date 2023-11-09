package com.programmingcodez.trackingservice.service;

import com.programmingcodez.trackingservice.entity.TrackingInfo;
import com.programmingcodez.trackingservice.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    public ResponseEntity<TrackingInfo> trackOrder(String orderNumber){
        if(this.trackingRepository.existsById(orderNumber)){
            return new ResponseEntity<>(this.trackingRepository.findById(orderNumber).orElse(null), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> setOrderStatus(TrackingInfo orderInfo){
        this.trackingRepository.save(orderInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
