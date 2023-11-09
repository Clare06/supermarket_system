package com.programmingcodez.orderservice.exception;

import com.programmingcodez.orderservice.dto.InventoryResponse;

import java.util.List;

public class ItemsNotInStockException extends RuntimeException {
    private List<InventoryResponse> inventoryResponses;

    public ItemsNotInStockException(List<InventoryResponse> inventoryResponses) {
        this.inventoryResponses = inventoryResponses;
    }

    public List<InventoryResponse> getInventoryResponses() {
        return inventoryResponses;
    }
}

