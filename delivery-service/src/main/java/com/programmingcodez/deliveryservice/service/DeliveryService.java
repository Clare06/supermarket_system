package com.programmingcodez.deliveryservice.service;

import com.programmingcodez.deliveryservice.dto.AcceptDeliveryDto;
import com.programmingcodez.deliveryservice.entity.Delivery;
import com.programmingcodez.deliveryservice.entity.OrderList;
import com.programmingcodez.deliveryservice.repository.DeliveryRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DeliveryService {

    public List<OrderList> getAvailableOrders();

    public List<Delivery> getAllOrdersOfCouriers();

    public Delivery getOrdersByCourier(String courierId);

    public ResponseEntity<Void> acceptDelivery(AcceptDeliveryDto acceptDeliveryDto);

    public ResponseEntity<Void> completeDelivery(AcceptDeliveryDto deliveryDto);
}
