package com.programmingcodez.deliveryservice.repository;

import com.programmingcodez.deliveryservice.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
}
