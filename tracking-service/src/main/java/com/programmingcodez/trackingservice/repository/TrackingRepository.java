package com.programmingcodez.trackingservice.repository;

import com.programmingcodez.trackingservice.entity.TrackingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<TrackingInfo, String> {
}
