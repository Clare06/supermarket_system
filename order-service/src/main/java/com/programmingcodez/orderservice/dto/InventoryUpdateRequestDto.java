package com.programmingcodez.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryUpdateRequestDto {
    private Long orderID;
    private List<InventoryRequest> inventoryRequests;
}
