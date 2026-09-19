package com.menuwise.repository;

import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.menu.ItemIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemIngredientRepository extends JpaRepository<ItemIngredient, ItemIngredientId> {
    List<ItemIngredient> findByItemId(Long itemId);
    List<ItemIngredient> findByIngredientId(Long ingredientId);
}
