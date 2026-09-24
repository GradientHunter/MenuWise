package com.menuwise;

import com.menuwise.repository.CategoryRepository;
import com.menuwise.repository.IngredientRepository;
import com.menuwise.repository.ItemIngredientRepository;
import com.menuwise.repository.ItemRepository;
import com.menuwise.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuWiseApplicationTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemIngredientRepository itemIngredientRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    void contextLoads() {
        assertThat(categoryRepository).isNotNull();
        assertThat(ingredientRepository).isNotNull();
        assertThat(itemRepository).isNotNull();
    }

    @Test
    void databaseSeeder_ShouldPopulateInitialData() {
        assertThat(categoryRepository.count()).isGreaterThanOrEqualTo(4);
        assertThat(supplierRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(ingredientRepository.count()).isGreaterThanOrEqualTo(15);
        assertThat(itemRepository.count()).isGreaterThanOrEqualTo(8);
        assertThat(itemIngredientRepository.count()).isGreaterThanOrEqualTo(8);
    }
}
