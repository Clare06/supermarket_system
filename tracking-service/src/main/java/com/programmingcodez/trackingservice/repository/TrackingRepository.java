package com.programmingcodez.trackingservice.repository;


import com.programmingcodez.trackingservice.Enum.OrderStatus;
import com.programmingcodez.trackingservice.entity.TrackingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingRepository extends JpaRepository<TrackingInfo, String> {
    List<TrackingInfo> findByOrderStatus(OrderStatus orderStatus);
}
