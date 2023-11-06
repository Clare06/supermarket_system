package com.programmingcodez.inventoryservice.repository;
import com.programmingcodez.inventoryservice.dto.InventoryResponse;
import com.programmingcodez.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
