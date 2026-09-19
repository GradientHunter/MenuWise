package com.menuwise.repository;

import com.menuwise.domain.inventory.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE i.currentStock <= i.minimumStock OR (i.expiryDate IS NOT NULL AND i.expiryDate <= :thresholdDate)")
    List<Ingredient> findLowStockOrExpiringSoon(@Param("thresholdDate") LocalDate thresholdDate);

    @Query("SELECT i FROM Ingredient i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= :thresholdDate")
    List<Ingredient> findExpiringSoon(@Param("thresholdDate") LocalDate thresholdDate);
}
