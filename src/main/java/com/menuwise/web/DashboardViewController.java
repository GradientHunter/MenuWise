package com.menuwise.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("activeRoute", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        return "pages/dashboard";
    }

    @GetMapping("/pos")
    public String pos(Model model) {
        model.addAttribute("activeRoute", "pos");
        model.addAttribute("pageTitle", "Point of Sale");
        return "pages/pos";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("activeRoute", "inventory");
        model.addAttribute("pageTitle", "Live Inventory");
        return "pages/inventory";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("activeRoute", "analytics");
        model.addAttribute("pageTitle", "Margin Analytics");
        return "pages/analytics";
    }

    @GetMapping("/simulator")
    public String simulator(Model model) {
        model.addAttribute("activeRoute", "simulator");
        model.addAttribute("pageTitle", "What-If Simulator");
        return "pages/simulator";
    }
}
