package com.menuwise.repository;

import com.menuwise.domain.menu.RecipeVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeVariantRepository extends JpaRepository<RecipeVariant, Long> {
    List<RecipeVariant> findByItemId(Long itemId);
}
