package com.menuwise.seed;

import com.menuwise.domain.category.Category;
import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.inventory.Supplier;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.menu.ItemIngredientId;
import com.menuwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final ItemRepository itemRepository;
    private final ItemIngredientRepository itemIngredientRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial seeding.");
            return;
        }

        log.info("Starting MenuWise initial data seeding (8 menu items, 15 ingredients, BOM)...");

        // 1. Seed Categories
        Category appetizers = categoryRepository
                .save(Category.builder().name("Appetizers").description("Starters and finger foods").build());
        Category mains = categoryRepository
                .save(Category.builder().name("Mains").description("Main courses and entrees").build());
        Category desserts = categoryRepository
                .save(Category.builder().name("Desserts").description("Sweet treats and pastries").build());
        Category beverages = categoryRepository
                .save(Category.builder().name("Beverages").description("Hot and cold drinks").build());

        // 2. Seed Suppliers
        Supplier farmFresh = supplierRepository.save(Supplier.builder().name("Farm Fresh Organics")
                .contactInfo("sales@farmfresh.com").leadTimeDays(2).build());
        Supplier primeMeats = supplierRepository.save(Supplier.builder().name("Prime Meats & Poultry")
                .contactInfo("orders@primemeats.com").leadTimeDays(1).build());
        Supplier dairyWholesale = supplierRepository.save(
                Supplier.builder().name("Metro Dairy Ltd").contactInfo("info@metrodairy.com").leadTimeDays(1).build());

        // 3. Seed 15 Ingredients
        LocalDate today = LocalDate.now();
        Ingredient chickenBreast = ingredientRepository.save(Ingredient.builder().name("Chicken Breast").unit("kg")
                .currentStock(25.0).minimumStock(10.0).costPerUnit(7.50).expiryDate(today.plusDays(4)).build());
        Ingredient beefPatty = ingredientRepository.save(Ingredient.builder().name("Beef Patty").unit("pcs")
                .currentStock(50.0).minimumStock(15.0).costPerUnit(3.00).expiryDate(today.plusDays(6)).build());
        Ingredient burgerBuns = ingredientRepository.save(Ingredient.builder().name("Brioche Buns").unit("pcs")
                .currentStock(60.0).minimumStock(20.0).costPerUnit(0.60).expiryDate(today.plusDays(3)).build());
        Ingredient cheddarCheese = ingredientRepository.save(Ingredient.builder().name("Cheddar Cheese").unit("kg")
                .currentStock(12.0).minimumStock(5.0).costPerUnit(8.00).expiryDate(today.plusDays(10)).build());
        Ingredient tomatoes = ingredientRepository.save(Ingredient.builder().name("Fresh Tomatoes").unit("kg")
                .currentStock(15.0).minimumStock(5.0).costPerUnit(2.20).expiryDate(today.plusDays(3)).build());
        Ingredient lettuce = ingredientRepository.save(Ingredient.builder().name("Romaine Lettuce").unit("kg")
                .currentStock(8.0).minimumStock(4.0).costPerUnit(2.50).expiryDate(today.plusDays(2)).build());
        Ingredient onions = ingredientRepository.save(Ingredient.builder().name("Red Onions").unit("kg")
                .currentStock(20.0).minimumStock(5.0).costPerUnit(1.50).expiryDate(today.plusDays(14)).build());
        Ingredient pasta = ingredientRepository.save(Ingredient.builder().name("Fettuccine Pasta").unit("kg")
                .currentStock(30.0).minimumStock(10.0).costPerUnit(2.00).expiryDate(today.plusDays(60)).build());
        Ingredient heavyCream = ingredientRepository.save(Ingredient.builder().name("Heavy Cream").unit("L")
                .currentStock(10.0).minimumStock(4.0).costPerUnit(4.00).expiryDate(today.plusDays(5)).build());
        Ingredient garlic = ingredientRepository.save(Ingredient.builder().name("Fresh Garlic").unit("kg")
                .currentStock(5.0).minimumStock(1.0).costPerUnit(4.50).expiryDate(today.plusDays(30)).build());
        Ingredient oliveOil = ingredientRepository.save(Ingredient.builder().name("Extra Virgin Olive Oil").unit("L")
                .currentStock(15.0).minimumStock(3.0).costPerUnit(9.00).expiryDate(today.plusDays(90)).build());
        Ingredient potatoes = ingredientRepository.save(Ingredient.builder().name("Russet Potatoes").unit("kg")
                .currentStock(40.0).minimumStock(15.0).costPerUnit(1.20).expiryDate(today.plusDays(20)).build());
        Ingredient mushrooms = ingredientRepository.save(Ingredient.builder().name("Button Mushrooms").unit("kg")
                .currentStock(6.0).minimumStock(3.0).costPerUnit(5.00).expiryDate(today.plusDays(2)).build());
        Ingredient chocolate = ingredientRepository.save(Ingredient.builder().name("Belgian Dark Chocolate").unit("kg")
                .currentStock(10.0).minimumStock(2.0).costPerUnit(12.00).expiryDate(today.plusDays(45)).build());
        Ingredient milk = ingredientRepository.save(Ingredient.builder().name("Whole Milk").unit("L").currentStock(18.0)
                .minimumStock(5.0).costPerUnit(1.50).expiryDate(today.plusDays(4)).build());

        // 4. Seed 8 Menu Items
        Item classicBurger = itemRepository.save(Item.builder().name("Classic Cheeseburger").category(mains)
                .costPrice(4.80).sellingPrice(13.99).prepTimeMin(15).build());
        Item chickenAlfredo = itemRepository.save(Item.builder().name("Creamy Chicken Fettuccine").category(mains)
                .costPrice(5.50).sellingPrice(16.50).prepTimeMin(20).build());
        Item mushroomSoup = itemRepository.save(Item.builder().name("Wild Mushroom Soup").category(appetizers)
                .costPrice(2.40).sellingPrice(8.50).prepTimeMin(12).build());
        Item loadedFries = itemRepository.save(Item.builder().name("Crispy Loaded Fries").category(appetizers)
                .costPrice(1.80).sellingPrice(6.99).prepTimeMin(10).build());
        Item caesarSalad = itemRepository.save(Item.builder().name("Classic Caesar Salad").category(appetizers)
                .costPrice(2.20).sellingPrice(9.50).prepTimeMin(8).build());
        Item lavaCake = itemRepository.save(Item.builder().name("Molten Chocolate Lava Cake").category(desserts)
                .costPrice(2.50).sellingPrice(7.99).prepTimeMin(15).build());
        Item creamyHotCocoa = itemRepository.save(Item.builder().name("Gourmet Hot Cocoa").category(beverages)
                .costPrice(1.20).sellingPrice(4.50).prepTimeMin(5).build());
        Item icedLatte = itemRepository.save(Item.builder().name("Iced Vanilla Latte").category(beverages)
                .costPrice(1.00).sellingPrice(4.25).prepTimeMin(4).build());

        // 5. Seed ItemIngredient Recipe Bill of Materials (BOM)
        // Classic Burger
        createBOM(classicBurger, beefPatty, 1.0);
        createBOM(classicBurger, burgerBuns, 1.0);
        createBOM(classicBurger, cheddarCheese, 0.05);
        createBOM(classicBurger, lettuce, 0.03);
        createBOM(classicBurger, tomatoes, 0.05);

        // Chicken Alfredo
        createBOM(chickenAlfredo, chickenBreast, 0.20);
        createBOM(chickenAlfredo, pasta, 0.15);
        createBOM(chickenAlfredo, heavyCream, 0.10);
        createBOM(chickenAlfredo, garlic, 0.01);

        // Mushroom Soup
        createBOM(mushroomSoup, mushrooms, 0.15);
        createBOM(mushroomSoup, heavyCream, 0.08);
        createBOM(mushroomSoup, onions, 0.03);

        // Loaded Fries
        createBOM(loadedFries, potatoes, 0.25);
        createBOM(loadedFries, cheddarCheese, 0.05);

        // Lava Cake
        createBOM(lavaCake, chocolate, 0.08);
        createBOM(lavaCake, milk, 0.05);

        log.info("Database seeding successfully completed! 8 items, 15 ingredients, recipes configured.");
    }

    private void createBOM(Item item, Ingredient ingredient, Double quantity) {
        ItemIngredientId id = new ItemIngredientId(item.getId(), ingredient.getId());
        ItemIngredient itemIngredient = ItemIngredient.builder()
                .id(id)
                .item(item)
                .ingredient(ingredient)
                .quantityRequired(quantity)
                .build();
        itemIngredientRepository.save(itemIngredient);
    }
}
