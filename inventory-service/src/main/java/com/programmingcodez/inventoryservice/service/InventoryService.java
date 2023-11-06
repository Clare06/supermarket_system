package com.programmingcodez.inventoryservice.service;

import com.programmingcodez.inventoryservice.dto.InventoryDto;
import com.programmingcodez.inventoryservice.dto.InventoryRequest;
import com.programmingcodez.inventoryservice.dto.InventoryResponse;
import com.programmingcodez.inventoryservice.entity.Inventory;
import com.programmingcodez.inventoryservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InventoryService {
    @Autowired
    private final InventoryRepository inventoryRepository;

    public String addInventory(InventoryDto inventoryDto) {
        System.out.println(inventoryDto.getSkuCode());
        Inventory inventory=new Inventory();

        inventory.setSkuCode(inventoryDto.getSkuCode());
        inventory.setQuantity(inventoryDto.getQuantity());

        inventoryRepository.save(inventory);
        return "added";
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

}
