package com.menuwise.seed;

import com.menuwise.domain.category.Category;
import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.inventory.Supplier;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.menu.ItemIngredientId;
import com.menuwise.domain.order.Order;
import com.menuwise.domain.order.OrderItem;
import com.menuwise.domain.order.OrderStatus;
import com.menuwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        private final OrderRepository orderRepository;

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
                                .save(Category.builder().name("Appetizers").description("Starters and finger foods")
                                                .build());
                Category mains = categoryRepository
                                .save(Category.builder().name("Mains").description("Main courses and entrees").build());
                Category desserts = categoryRepository
                                .save(Category.builder().name("Desserts").description("Sweet treats and pastries")
                                                .build());
                Category beverages = categoryRepository
                                .save(Category.builder().name("Beverages").description("Hot and cold drinks").build());

                // 2. Seed Suppliers
                Supplier farmFresh = supplierRepository.save(Supplier.builder().name("Bengal Organics & Agro")
                                .contactInfo("sales@bengalagro.com").leadTimeDays(2).build());
                Supplier primeMeats = supplierRepository.save(Supplier.builder().name("Prime Halal Meats & Fisheries")
                                .contactInfo("orders@primemeats.com").leadTimeDays(1).build());
                Supplier dairyWholesale = supplierRepository.save(
                                Supplier.builder().name("Dhaka Dairy & Ghee Ltd").contactInfo("info@dhakadairy.com")
                                                .leadTimeDays(1).build());

                // 3. Seed 15 English & Bangladeshi Ingredients
                LocalDate today = LocalDate.now();
                Ingredient meatCubes = ingredientRepository
                                .save(Ingredient.builder().name("Prime Beef / Mutton Cubes").unit("kg")
                                                .currentStock(25.0).minimumStock(10.0).costPerUnit(750.0)
                                                .expiryDate(today.plusDays(5)).build());
                Ingredient chiniguraRice = ingredientRepository.save(Ingredient.builder().name("Chinigura Polao Rice").unit("kg")
                                .currentStock(80.0).minimumStock(20.0).costPerUnit(120.0).expiryDate(today.plusDays(90))
                                .build());
                Ingredient desiChicken = ingredientRepository.save(Ingredient.builder().name("Desi Chicken").unit("kg")
                                .currentStock(20.0).minimumStock(8.0).costPerUnit(320.0).expiryDate(today.plusDays(4))
                                .build());
                Ingredient fishFillet = ingredientRepository
                                .save(Ingredient.builder().name("Fresh Ilish / Cod Fillets").unit("kg")
                                                .currentStock(15.0).minimumStock(5.0).costPerUnit(650.0)
                                                .expiryDate(today.plusDays(3)).build());
                Ingredient potatoes = ingredientRepository.save(Ingredient.builder().name("Fresh Potatoes").unit("kg")
                                .currentStock(50.0).minimumStock(15.0).costPerUnit(45.0).expiryDate(today.plusDays(30))
                                .build());
                Ingredient mustardOilGhee = ingredientRepository.save(Ingredient.builder().name("Mustard Oil & Pure Ghee").unit("L")
                                .currentStock(15.0).minimumStock(5.0).costPerUnit(280.0).expiryDate(today.plusDays(60))
                                .build());
                Ingredient onions = ingredientRepository.save(Ingredient.builder().name("Red Onions").unit("kg")
                                .currentStock(30.0).minimumStock(10.0).costPerUnit(80.0).expiryDate(today.plusDays(20))
                                .build());
                Ingredient gingerGarlic = ingredientRepository.save(Ingredient.builder().name("Ginger Garlic Paste").unit("kg")
                                .currentStock(10.0).minimumStock(3.0).costPerUnit(220.0).expiryDate(today.plusDays(15))
                                .build());
                Ingredient tokDoi = ingredientRepository.save(Ingredient.builder().name("Fresh Yogurt / Tok Doi").unit("L")
                                .currentStock(14.0).minimumStock(4.0).costPerUnit(90.0).expiryDate(today.plusDays(5))
                                .build());
                Ingredient cheddarCheese = ingredientRepository.save(Ingredient.builder().name("English Cheddar Cheese").unit("kg")
                                .currentStock(10.0).minimumStock(4.0).costPerUnit(950.0).expiryDate(today.plusDays(30))
                                .build());
                Ingredient flour = ingredientRepository
                                .save(Ingredient.builder().name("Flour & Breadcrumbs").unit("kg")
                                                .currentStock(25.0).minimumStock(8.0).costPerUnit(65.0)
                                                .expiryDate(today.plusDays(90)).build());
                Ingredient teaLeaves = ingredientRepository.save(Ingredient.builder().name("English Breakfast Tea Leaves").unit("kg")
                                .currentStock(8.0).minimumStock(2.0).costPerUnit(450.0).expiryDate(today.plusDays(180))
                                .build());
                Ingredient wholeMilk = ingredientRepository
                                .save(Ingredient.builder().name("Whole Milk").unit("L")
                                                .currentStock(25.0).minimumStock(8.0).costPerUnit(85.0)
                                                .expiryDate(today.plusDays(4)).build());
                Ingredient biryaniSpices = ingredientRepository
                                .save(Ingredient.builder().name("Traditional Spices & Masala").unit("kg")
                                                .currentStock(6.0).minimumStock(2.0).costPerUnit(550.0)
                                                .expiryDate(today.plusDays(60)).build());
                Ingredient mintChillies = ingredientRepository.save(Ingredient.builder().name("Green Chillies & Mint").unit("kg")
                                .currentStock(5.0).minimumStock(1.5).costPerUnit(120.0).expiryDate(today.plusDays(6)).build());

                // 4. Seed 8 Menu Items (English & Bangladeshi Cuisine in BDT ৳)
                Item kacchiBiryani = itemRepository.save(Item.builder().name("Mutton Kacchi Biryani").category(mains)
                                .costPrice(240.0).sellingPrice(450.0).prepTimeMin(25).build());
                Item fishAndChips = itemRepository
                                .save(Item.builder().name("Traditional Fish & Chips").category(mains)
                                                .costPrice(280.0).sellingPrice(520.0).prepTimeMin(20).build());
                Item chickenRoast = itemRepository.save(Item.builder().name("Chicken Roast with Polao").category(mains)
                                .costPrice(190.0).sellingPrice(380.0).prepTimeMin(20).build());
                Item shepherdsPie = itemRepository.save(Item.builder().name("Classic Shepherd's Pie").category(mains)
                                .costPrice(250.0).sellingPrice(480.0).prepTimeMin(25).build());
                Item shingaraPlatter = itemRepository.save(Item.builder().name("Crispy Shingara & Samosa Platter").category(appetizers)
                                .costPrice(40.0).sellingPrice(120.0).prepTimeMin(10).build());
                Item mishtiDoi = itemRepository.save(Item.builder().name("Mishti Doi with Rasgulla").category(desserts)
                                .costPrice(65.0).sellingPrice(150.0).prepTimeMin(5).build());
                Item royalBorhani = itemRepository.save(Item.builder().name("Royal Borhani").category(beverages)
                                .costPrice(45.0).sellingPrice(120.0).prepTimeMin(5).build());
                Item englishMasalaTea = itemRepository.save(Item.builder().name("English Breakfast Masala Tea").category(beverages)
                                .costPrice(25.0).sellingPrice(80.0).prepTimeMin(5).build());

                // 5. Seed ItemIngredient Recipe Bill of Materials (BOM)
                // 1. Mutton Kacchi Biryani
                createBOM(kacchiBiryani, meatCubes, 0.25);
                createBOM(kacchiBiryani, chiniguraRice, 0.20);
                createBOM(kacchiBiryani, mustardOilGhee, 0.05);
                createBOM(kacchiBiryani, potatoes, 0.10);
                createBOM(kacchiBiryani, biryaniSpices, 0.02);

                // 2. Fish & Chips
                createBOM(fishAndChips, fishFillet, 0.22);
                createBOM(fishAndChips, potatoes, 0.25);
                createBOM(fishAndChips, flour, 0.08);

                // 3. Chicken Roast with Polao
                createBOM(chickenRoast, desiChicken, 0.25);
                createBOM(chickenRoast, chiniguraRice, 0.20);
                createBOM(chickenRoast, mustardOilGhee, 0.04);
                createBOM(chickenRoast, onions, 0.05);

                // 4. Shepherd's Pie
                createBOM(shepherdsPie, meatCubes, 0.20);
                createBOM(shepherdsPie, potatoes, 0.20);
                createBOM(shepherdsPie, cheddarCheese, 0.04);
                createBOM(shepherdsPie, onions, 0.03);

                // 5. Shingara Platter
                createBOM(shingaraPlatter, potatoes, 0.20);
                createBOM(shingaraPlatter, flour, 0.10);
                createBOM(shingaraPlatter, mustardOilGhee, 0.03);

                // 6. Mishti Doi with Rasgulla
                createBOM(mishtiDoi, wholeMilk, 0.30);
                createBOM(mishtiDoi, tokDoi, 0.05);

                // 7. Royal Borhani
                createBOM(royalBorhani, tokDoi, 0.25);
                createBOM(royalBorhani, mintChillies, 0.02);
                createBOM(royalBorhani, biryaniSpices, 0.01);

                // 8. English Breakfast Masala Tea
                createBOM(englishMasalaTea, teaLeaves, 0.02);
                createBOM(englishMasalaTea, wholeMilk, 0.10);

                // 6. Seed Sample Recent Orders
                if (orderRepository.count() == 0) {
                        LocalDateTime now = LocalDateTime.now();

                        Order order1 = Order.builder()
                                        .orderTimestamp(now.minusMinutes(42))
                                        .status(OrderStatus.COMPLETED)
                                        .totalAmount(900.0)
                                        .build();
                        OrderItem oi1 = OrderItem.builder().order(order1).item(kacchiBiryani).quantity(2).unitPrice(450.0).build();
                        order1.getItems().add(oi1);
                        orderRepository.save(order1);

                        Order order2 = Order.builder()
                                        .orderTimestamp(now.minusHours(1).minusMinutes(15))
                                        .status(OrderStatus.COMPLETED)
                                        .totalAmount(520.0)
                                        .build();
                        OrderItem oi2 = OrderItem.builder().order(order2).item(fishAndChips).quantity(1).unitPrice(520.0).build();
                        order2.getItems().add(oi2);
                        orderRepository.save(order2);

                        Order order3 = Order.builder()
                                        .orderTimestamp(now.minusHours(2).minusMinutes(30))
                                        .status(OrderStatus.COMPLETED)
                                        .totalAmount(500.0)
                                        .build();
                        OrderItem oi3a = OrderItem.builder().order(order3).item(chickenRoast).quantity(1).unitPrice(380.0).build();
                        OrderItem oi3b = OrderItem.builder().order(order3).item(royalBorhani).quantity(1).unitPrice(120.0).build();
                        order3.getItems().add(oi3a);
                        order3.getItems().add(oi3b);
                        orderRepository.save(order3);

                        Order order4 = Order.builder()
                                        .orderTimestamp(now.minusHours(3).minusMinutes(45))
                                        .status(OrderStatus.COMPLETED)
                                        .totalAmount(230.0)
                                        .build();
                        OrderItem oi4a = OrderItem.builder().order(order4).item(mishtiDoi).quantity(1).unitPrice(150.0).build();
                        OrderItem oi4b = OrderItem.builder().order(order4).item(englishMasalaTea).quantity(1).unitPrice(80.0).build();
                        order4.getItems().add(oi4a);
                        order4.getItems().add(oi4b);
                        orderRepository.save(order4);
                }

                log.info("Database seeding successfully completed! 8 items, 15 ingredients, recipes configured, sample orders seeded.");
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
