package com.programmingcodez.inventoryservice.service;

import com.programmingcodez.inventoryservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
}
