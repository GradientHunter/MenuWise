package com.menuwise.web;

import com.menuwise.repository.IngredientRepository;
import com.menuwise.repository.ItemRepository;
import com.menuwise.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("totalMenuItems", itemRepository.count());
        model.addAttribute("totalIngredients", ingredientRepository.count());
        model.addAttribute("activeOrdersCount", orderRepository.countOrdersSince(LocalDateTime.now().minusHours(12)));
        model.addAttribute("lowStockAlerts", ingredientRepository.findLowStockOrExpiringSoon(LocalDate.now().plusDays(2)).size());
        return "pages/dashboard";
    }

    @GetMapping("/pos")
    public String pos(Model model) {
        model.addAttribute("activeTab", "pos");
        model.addAttribute("menuItems", itemRepository.findAll());
        return "pages/pos";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("activeTab", "inventory");
        model.addAttribute("ingredients", ingredientRepository.findAll());
        model.addAttribute("alerts", ingredientRepository.findLowStockOrExpiringSoon(LocalDate.now().plusDays(2)));
        return "pages/inventory";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("activeTab", "analytics");
        model.addAttribute("topItems", itemRepository.findTopProfitableItems());
        return "pages/analytics";
    }

    @GetMapping("/simulator")
    public String simulator(Model model) {
        model.addAttribute("activeTab", "simulator");
        model.addAttribute("menuItems", itemRepository.findAll());
        return "pages/simulator";
    }
}
