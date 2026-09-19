package com.menuwise.repository;

import com.menuwise.domain.menu.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryId(Long categoryId);

    @Query("SELECT i FROM Item i ORDER BY ((i.sellingPrice - i.costPrice) / i.sellingPrice) DESC")
    List<Item> findTopProfitableItems();
}
