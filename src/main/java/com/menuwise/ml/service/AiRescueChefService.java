package com.menuwise.ml.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.menuwise.domain.category.Category;
import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.menu.ItemIngredientId;
import com.menuwise.ml.dto.AiRescueRecipeDto;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.repository.CategoryRepository;
import com.menuwise.repository.IngredientRepository;
import com.menuwise.repository.ItemIngredientRepository;
import com.menuwise.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiRescueChefService {

    private final WastePredictionService wastePredictionService;
    private final IngredientRepository ingredientRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemIngredientRepository itemIngredientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${menuwise.ai.gemini-api-key:}")
    private String geminiApiKey;

    @Value("${menuwise.ai.model:gemini-3.8-flash}")
    private String modelName;

    /**
     * Synthesizes an intelligent, zero-waste recipe using all current at-risk expiring inventory.
     */
    public AiRescueRecipeDto generateRescueRecipe() {
        List<SpoilageForecastDto> atRisk = wastePredictionService.getAtRiskIngredients();
        List<Ingredient> candidateIngredients = new ArrayList<>();

        if (!atRisk.isEmpty()) {
            for (SpoilageForecastDto f : atRisk) {
                ingredientRepository.findById(f.getIngredientId()).ifPresent(candidateIngredients::add);
            }
        } else {
            // Fallback: take ingredients closest to expiry
            candidateIngredients = ingredientRepository.findAll().stream()
                    .filter(i -> i.getExpiryDate() != null)
                    .sorted(Comparator.comparing(Ingredient::getExpiryDate))
                    .limit(5)
                    .toList();
        }

        if (candidateIngredients.isEmpty()) {
            candidateIngredients = ingredientRepository.findAll().stream().limit(4).toList();
        }

        // Try LLM generation if Gemini API key is configured
        if (geminiApiKey != null && !geminiApiKey.isBlank() && !geminiApiKey.startsWith("mock")) {
            try {
                AiRescueRecipeDto llmRecipe = callGeminiForRecipe(candidateIngredients);
                if (llmRecipe != null) {
                    return llmRecipe;
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed, falling back to built-in smart culinary engine: {}", ex.getMessage());
            }
        }

        // Built-in smart culinary composition engine
        return buildSmartCulinaryRecipe(candidateIngredients);
    }

    /**
     * Adopts the AI-generated recipe as an official Menu Item, making it immediately available in the POS.
     */
    @Transactional
    public Item adoptRecipeAsSpecial(AiRescueRecipeDto recipe) {
        Category category = categoryRepository.findByName("Chef's Specials")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Chef's Specials")
                        .description("Limited-edition zero-waste dishes crafted by AI Chef")
                        .build()));

        Item newItem = Item.builder()
                .name(recipe.getDishName())
                .category(category)
                .costPrice(recipe.getEstimatedCostPrice())
                .sellingPrice(recipe.getSuggestedSellingPrice())
                .prepTimeMin(recipe.getEstimatedPrepTimeMin())
                .build();

        newItem = itemRepository.save(newItem);

        // Bind Bill of Materials so ordering deducts the at-risk ingredients
        if (recipe.getUsedIngredients() != null) {
            for (AiRescueRecipeDto.IngredientUsage usage : recipe.getUsedIngredients()) {
                if (usage.getIngredientId() == null) continue;
                Ingredient ing = ingredientRepository.findById(usage.getIngredientId()).orElse(null);
                if (ing != null) {
                    ItemIngredient itemIngredient = ItemIngredient.builder()
                            .id(new ItemIngredientId(newItem.getId(), ing.getId()))
                            .item(newItem)
                            .ingredient(ing)
                            .quantityRequired(usage.getQuantityPerPortion() != null ? usage.getQuantityPerPortion() : 0.2)
                            .build();
                    itemIngredientRepository.save(itemIngredient);
                }
            }
        }

        log.info("Adopted AI Rescue Recipe as Item ID: {}, Name: {}", newItem.getId(), newItem.getName());
        return newItem;
    }

    private AiRescueRecipeDto buildSmartCulinaryRecipe(List<Ingredient> ingredients) {
        LocalDate today = LocalDate.now();

        // Categorize candidate ingredients
        Ingredient protein = null;
        Ingredient dairy = null;
        Ingredient starch = null;
        List<Ingredient> aromaticsAndSpices = new ArrayList<>();
        List<AiRescueRecipeDto.IngredientUsage> usages = new ArrayList<>();

        double totalBatchCost = 0.0;
        double totalWasteSavedKg = 0.0;

        for (Ingredient ing : ingredients) {
            String nameLower = ing.getName().toLowerCase();
            long daysLeft = ing.getExpiryDate() != null ? ChronoUnit.DAYS.between(today, ing.getExpiryDate()) : 10;
            String urgency = daysLeft <= 1 ? "Expires Tomorrow" : (daysLeft <= 3 ? "Expires in " + daysLeft + "d" : "Near Expiry");

            double portionQty;
            if (nameLower.contains("chicken") || nameLower.contains("beef") || nameLower.contains("mutton") || nameLower.contains("fish")) {
                if (protein == null) protein = ing;
                portionQty = 0.25; // 250g
            } else if (nameLower.contains("yogurt") || nameLower.contains("milk") || nameLower.contains("doi") || nameLower.contains("cheese")) {
                if (dairy == null) dairy = ing;
                portionQty = 0.15; // 150ml/g
            } else if (nameLower.contains("potato") || nameLower.contains("rice") || nameLower.contains("flour")) {
                if (starch == null) starch = ing;
                portionQty = 0.20; // 200g
            } else {
                aromaticsAndSpices.add(ing);
                portionQty = 0.05; // 50g
            }

            double ingCost = (ing.getCostPerUnit() != null ? ing.getCostPerUnit() : 100.0) * portionQty;
            totalBatchCost += ingCost;
            totalWasteSavedKg += portionQty;

            usages.add(AiRescueRecipeDto.IngredientUsage.builder()
                    .ingredientId(ing.getId())
                    .ingredientName(ing.getName())
                    .quantityPerPortion(portionQty)
                    .unit(ing.getUnit())
                    .currentStock(ing.getCurrentStock())
                    .expiryUrgency(urgency)
                    .build());
        }

        // Formulate recipe title and culinary synthesis
        String dishName;
        String tagline;
        String description;
        List<String> steps = new ArrayList<>();

        if (protein != null && dairy != null) {
            dishName = "Chef's Velvet Braised " + protein.getName().split(" / ")[0] + " in Cultured " + dairy.getName().split(" / ")[0];
            tagline = "A rich, tender slow-braise pairing tender protein with caramelized aromatics and a velvety reduction.";
            description = "Crafted specifically to rescue today's fresh " + protein.getName() + " and delicate " + dairy.getName() + ", simmered slowly with kitchen aromatics into a rich, decadent comfort dish.";
            steps.add("Marinate the " + protein.getName() + " in " + dairy.getName() + ", crushed ginger, garlic, and kitchen spices for 15 minutes.");
            steps.add("Sear the marinated cuts in a hot pan until a golden caramelized crust forms.");
            steps.add(starch != null ? "Fold in roasted " + starch.getName() + " to absorb the flavorful drippings and simmer for 12 minutes." : "Simmer gently over low flame for 15 minutes until tender.");
            steps.add("Garnish with fresh green chillies and serve steaming hot with fragrant table accompaniments.");
        } else if (protein != null) {
            dishName = "Pan-Seared " + protein.getName().split(" / ")[0] + " with Roasted " + (starch != null ? starch.getName().split(" / ")[0] : "Spiced Medley");
            tagline = "Crisp seared cuts paired with spiced roasted sides and aromatic glaze.";
            description = "Maximizes kitchen inventory efficiency by highlighting high-protein cuts seared to perfection alongside savory sides.";
            steps.add("Season the cuts generously with ground traditional spices and light sea salt.");
            steps.add("Sear on high heat for 4 minutes per side to seal in moisture and natural juices.");
            steps.add("Deglaze the pan with herb broth and toss with roasted sides until glazed.");
            steps.add("Plate with a drizzle of kitchen reduction.");
        } else if (dairy != null && starch != null) {
            dishName = "Heritage " + dairy.getName().split(" / ")[0] + " & " + starch.getName().split(" / ")[0] + " Velvet Casserole";
            tagline = "Rich baked comfort gratin infusing creamy dairy with tender root starches.";
            description = "A warm, satisfying specialty dish that completely clears expiring dairy and starch inventory in a comforting bake.";
            steps.add("Slice the " + starch.getName() + " thinly and parboil until tender.");
            steps.add("Whisk the " + dairy.getName() + " with spices, garlic paste, and gentle seasoning.");
            steps.add("Layer the sliced starches and creamy mixture in a baking skillet.");
            steps.add("Bake at 190°C for 20 minutes until bubbly and golden brown on top.");
        } else {
            dishName = "Chef's Garden Rescue Sauté with Spiced Reduction";
            tagline = "Vibrant pan-tossed medley utilizing peak-flavor produce and aromatics.";
            description = "A rapid high-heat wok toss highlighting fresh ingredients before maturity loss.";
            steps.add("Prep and dice all produce into uniform bite-sized pieces.");
            steps.add("Sizzle aromatics in hot oil until deeply fragrant.");
            steps.add("Wok-toss all ingredients on high heat for 6 minutes.");
            steps.add("Finish with fresh cracked pepper and serve immediately.");
        }

        double cost = Math.max(45.0, Math.round(totalBatchCost * 10.0) / 10.0);
        double suggestedPrice = Math.round((cost / 0.38) / 10.0) * 10.0; // ~62% gross margin

        return AiRescueRecipeDto.builder()
                .dishName(dishName)
                .tagline(tagline)
                .description(description)
                .category("Chef's Rescue Special")
                .usedIngredients(usages)
                .estimatedPrepTimeMin(22)
                .estimatedCostPrice(cost)
                .suggestedSellingPrice(suggestedPrice)
                .estimatedWasteSavedKg(Math.round(totalWasteSavedKg * 100.0) / 100.0)
                .preparationSteps(steps)
                .aiModelUsed("MenuWise Smart Culinary Engine")
                .build();
    }

    private AiRescueRecipeDto callGeminiForRecipe(List<Ingredient> ingredients) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;

            StringBuilder prompt = new StringBuilder("You are an executive restaurant chef. Create an appealing, gourmet restaurant special dish that ONLY uses or centers around these expiring at-risk ingredients:\n");
            for (Ingredient i : ingredients) {
                prompt.append("- ").append(i.getName()).append(" (Stock: ").append(i.getCurrentStock()).append(" ").append(i.getUnit()).append(", Expiry: ").append(i.getExpiryDate()).append(")\n");
            }
            prompt.append("\nReturn ONLY valid JSON matching this structure with no markdown backticks:\n");
            prompt.append("{\"dishName\":\"...\", \"tagline\":\"...\", \"description\":\"...\", \"prepTimeMin\":25, \"steps\":[\"step 1...\", \"step 2...\"]}");

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt.toString())
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                    return null;
                }

                JsonNode parts = candidates.get(0).path("content").path("parts");
                String text = null;
                if (parts.isArray()) {
                    for (JsonNode part : parts) {
                        if (part.has("text") && !part.path("text").asText().isBlank()) {
                            text = part.path("text").asText();
                            break;
                        }
                    }
                }

                if (text == null || text.isBlank()) {
                    return null;
                }

                int startIdx = text.indexOf('{');
                int endIdx = text.lastIndexOf('}');
                if (startIdx >= 0 && endIdx > startIdx) {
                    text = text.substring(startIdx, endIdx + 1);
                }

                JsonNode parsed = objectMapper.readTree(text);
                AiRescueRecipeDto base = buildSmartCulinaryRecipe(ingredients);
                base.setDishName(parsed.path("dishName").asText(base.getDishName()));
                base.setTagline(parsed.path("tagline").asText(base.getTagline()));
                base.setDescription(parsed.path("description").asText(base.getDescription()));
                if (parsed.has("prepTimeMin")) base.setEstimatedPrepTimeMin(parsed.get("prepTimeMin").asInt(base.getEstimatedPrepTimeMin()));
                if (parsed.has("steps") && parsed.get("steps").isArray()) {
                    List<String> steps = new ArrayList<>();
                    parsed.get("steps").forEach(s -> steps.add(s.asText()));
                    base.setPreparationSteps(steps);
                }
                base.setAiModelUsed("Intelligent suggestion");
                return base;
            }
        } catch (Exception e) {
            log.warn("Gemini invocation failed: {}", e.getMessage());
        }
        return null;
    }
}
