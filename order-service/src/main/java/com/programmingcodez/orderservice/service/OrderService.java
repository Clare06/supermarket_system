package com.programmingcodez.orderservice.service;

import com.programmingcodez.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;

}
