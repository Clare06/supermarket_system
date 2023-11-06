package com.programmingcodez.inventoryservice.controller;

import com.programmingcodez.inventoryservice.dto.InventoryDto;
import com.programmingcodez.inventoryservice.dto.InventoryRequest;
import com.programmingcodez.inventoryservice.dto.InventoryResponse;
import com.programmingcodez.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    @Autowired
    private final InventoryService inventoryService;

    @PostMapping
    public String addInventory(@RequestBody InventoryDto inventoryDto){
        return inventoryService.addInventory(inventoryDto);
    }
    @PostMapping("/stock")
    public List<InventoryResponse> isInStock(@RequestBody List<InventoryRequest> inventoryRequest){
        return inventoryService.isInStock(inventoryRequest);
    }
}