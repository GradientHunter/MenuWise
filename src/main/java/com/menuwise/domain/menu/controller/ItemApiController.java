package com.menuwise.domain.menu.controller;

import com.menuwise.common.exception.ResourceNotFoundException;
import com.menuwise.domain.category.Category;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.dto.ItemRequestDto;
import com.menuwise.repository.CategoryRepository;
import com.menuwise.repository.ItemRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }
                                                                                        
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemRequestDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Item item = Item.builder()
                .name(request.getName().trim())
                .category(category)
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .prepTimeMin(request.getPrepTimeMin())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(itemRepository.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDto request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        item.setName(request.getName().trim());
        item.setCategory(category);
        item.setCostPrice(request.getCostPrice());
        item.setSellingPrice(request.getSellingPrice());
        item.setPrepTimeMin(request.getPrepTimeMin());

        return ResponseEntity.ok(itemRepository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
