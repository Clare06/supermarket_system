package com.programmingcodez.deliveryservice.service;

import com.programmingcodez.deliveryservice.Enum.OrderStatus;
import com.programmingcodez.deliveryservice.dto.AcceptDeliveryDto;
import com.programmingcodez.deliveryservice.entity.Delivery;
import com.programmingcodez.deliveryservice.entity.OrderList;
import com.programmingcodez.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService{

    @Autowired
    private DeliveryRepository deliveryRepository;

    private final WebClient.Builder webClientBuilder;

    @Override
    public List<OrderList> getAvailableOrders() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8083/api/tracking/toDeliver")
                .retrieve()
                .bodyToFlux(OrderList.class)
                .collectList()
                .block();
    }

    @Override
    public List<Delivery> getAllOrdersOfCouriers() {
        return this.deliveryRepository.findAll();
    }

    @Override
    public Delivery getOrdersByCourier(String courierId) {
        return this.deliveryRepository.findById(courierId).orElse(new Delivery());
    }

    @Override
    public ResponseEntity<Void> acceptDelivery(AcceptDeliveryDto acceptDeliveryDto) {
        try {
            this.webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8083/api/tracking/acceptDeliver")
                    .bodyValue(acceptDeliveryDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            Delivery delivery = this.deliveryRepository.findById(acceptDeliveryDto.getCarrierId())
                    .orElse(new Delivery(acceptDeliveryDto.getCarrierId(), new ArrayList<>()));

            List<OrderList> orderLists = delivery.getOrderList();
            orderLists.add(new OrderList(acceptDeliveryDto.getOrderNumber(), OrderStatus.SHIPPED));
            delivery.setOrderList(orderLists);
            this.deliveryRepository.save(delivery);

            return ResponseEntity.ok().build();

        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> completeDelivery(AcceptDeliveryDto deliveryDto) {
        try {
            this.webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8083/api/tracking/complete/{orderNumber}", deliveryDto.getOrderNumber())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            Delivery delivery = this.deliveryRepository.findById(deliveryDto.getCarrierId()).orElse(null);

            List<OrderList> orderList = delivery.getOrderList();

            for (OrderList order : orderList){
                if (order.getOrderNumber().equals(deliveryDto.getOrderNumber())){
                    order.setOrderStatus(OrderStatus.DELIVERED);
                }
            }
            this.deliveryRepository.save(delivery);

            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
