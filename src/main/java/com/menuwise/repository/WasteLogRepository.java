package com.menuwise.repository;

import com.menuwise.domain.inventory.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
    List<WasteLog> findByLogDateBetween(LocalDate startDate, LocalDate endDate);
    List<WasteLog> findByIngredientId(Long ingredientId);
}
