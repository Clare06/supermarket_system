package com.programmingcodez.inventoryservice.service;

import com.programmingcodez.inventoryservice.dto.InventoryDto;
import com.programmingcodez.inventoryservice.dto.InventoryRequest;
import com.programmingcodez.inventoryservice.dto.InventoryResponse;
import com.programmingcodez.inventoryservice.entity.Inventory;
import com.programmingcodez.inventoryservice.repository.InventoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InventoryService {
    @Autowired
    private final InventoryRepository inventoryRepository;

    private static final String ADD_INVENTORY_CIRCUIT_BREAKER = "add-inventory-circuit-breaker";
    private static final String ADD_INVENTORY_RETRY = "add-inventory-retry";
    private static final String DEDUCT_INVENTORY_CIRCUIT_BREAKER = "deduct-inventory-circuit-breaker";
    private static final String DEDUCT_INVENTORY_RETRY = "deduct-inventory-retry";

    @CircuitBreaker(name = ADD_INVENTORY_CIRCUIT_BREAKER, fallbackMethod = "addInventoryFallback")
    @Retry(name = ADD_INVENTORY_RETRY)
    public String addInventory(InventoryDto inventoryDto) {
        try{
            System.out.println(inventoryDto.getSkuCode());
            Inventory inventory=new Inventory();

            inventory.setSkuCode(inventoryDto.getSkuCode());
            inventory.setQuantity(inventoryDto.getQuantity());

            inventoryRepository.save(inventory);
            return "added";
        } catch (DataIntegrityViolationException e){
            return "Error: SKU code already exists.";
        } catch (Exception e) {
            throw e;
        }

    }

    // Fallback method to handle deduct-inventory-circuit-breaker open state
    private String addInventoryFallback(InventoryDto inventoryDto, Throwable throwable) {
        return "Error: Service is currently unavailable. Please try again later.";
    }

    public List<InventoryResponse> isInStock(List<InventoryRequest> inventoryRequest) {
        List<String> skuCodes = inventoryRequest.stream()
                .map(InventoryRequest::getSkuCode)
                .collect(Collectors.toList());

        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCodes);

        List<InventoryResponse> inventoryResponse = inventoryList.stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() >= this.getReqQuantity(inventory.getSkuCode(), inventoryRequest))
                        .build()
                )
                .collect(Collectors.toList());

        return inventoryResponse;
    }

    private Integer getReqQuantity(String skuCode, List<InventoryRequest> inventoryRequest) {
        for (InventoryRequest request : inventoryRequest) {
            if (skuCode.equals(request.getSkuCode())) {
                return request.getQuantity();
            }
        }
        return 0;
    }
    @Transactional
    public String updateInventory(List<InventoryRequest> inventoryRequest) {

        for (InventoryRequest inventoryReq : inventoryRequest) {
            String skuCode = inventoryReq.getSkuCode();
            Integer quantityToDeduct = inventoryReq.getQuantity();
           this.deductInventory(skuCode, quantityToDeduct);
        }
        return "inventory-updated";
    }
    @Transactional
    @CircuitBreaker(name = DEDUCT_INVENTORY_CIRCUIT_BREAKER, fallbackMethod = "deductInventoryFallback")
    @Retry(name = DEDUCT_INVENTORY_RETRY)
    public void deductInventory(String skuCode, Integer quantityToDeduct) {
        try {
            Inventory item = inventoryRepository.findBySkuCode(skuCode);

            if (item.getQuantity() >= quantityToDeduct) {
                item.setQuantity(item.getQuantity() - quantityToDeduct);
                inventoryRepository.save(item);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient quantity to deduct.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    // Fallback method to handle deduct-inventory-circuit-breaker open state
    private void deductInventoryFallback(String skuCode, Integer quantityToDeduct, Throwable throwable) {
        System.out.println("Error: Service is currently unavailable. Please try again later.");
    }

    @Transactional
    public void rollBackInventory(String skuCode, Integer quantityToDeduct) {
        Inventory item = inventoryRepository.findBySkuCode(skuCode);
        if(item.getQuantity() >= quantityToDeduct){
            item.setQuantity(item.getQuantity() + quantityToDeduct);
            inventoryRepository.save(item);
        }
    }
    @Transactional
    public String rollBackUpdate(List<InventoryRequest> inventoryRequest) {
        for (InventoryRequest inventoryReq : inventoryRequest) {
            String skuCode = inventoryReq.getSkuCode();
            Integer quantityToDeduct = inventoryReq.getQuantity();
            this.rollBackInventory(skuCode, quantityToDeduct);
        }
        return "inventory-updated";
    }
}
