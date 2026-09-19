package com.menuwise.repository;

import com.menuwise.domain.inventory.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findByIngredientIdOrderByTimestampDesc(Long ingredientId);
}
