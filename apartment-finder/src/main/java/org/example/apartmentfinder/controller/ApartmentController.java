package org.example.apartmentfinder.controller;

import org.example.apartmentfinder.dto.ApartmentDto;
import org.example.apartmentfinder.service.ApartmentService;
import org.example.apartmentfinder.service.BankApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final BankApiService bankApiService;

    @Autowired
    public ApartmentController(ApartmentService apartmentService, BankApiService bankApiService) {
        this.apartmentService = apartmentService;
        this.bankApiService = bankApiService;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/apartments")
    public String listApartments(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<ApartmentDto> apartments;

        if (district != null && !district.isEmpty()) {
            apartments = apartmentService.getApartmentsByDistrict(district);
        } else if (rooms != null) {
            apartments = apartmentService.getApartmentsByRooms(rooms);
        } else if (minPrice != null && maxPrice != null) {
            apartments = apartmentService.getApartmentsByPriceRange(minPrice, maxPrice);
        } else {
            apartments = apartmentService.getAllApartments();
        }

        BigDecimal usdRate = bankApiService.getUsdRate();

        model.addAttribute("apartments", apartments);
        model.addAttribute("usdRate", usdRate);
        model.addAttribute("district", district);
        model.addAttribute("rooms", rooms);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "apartments";
    }

    @GetMapping("/manual-update")
    public String manualUpdate(Model model) {
        try {
            apartmentService.manualParseAndSave();
            model.addAttribute("message", "Дані про квартири успішно оновлено");
        } catch (Exception e) {
            model.addAttribute("error", "Помилка при оновленні даних: " + e.getMessage());
        }
        return "redirect:/apartments";
    }
}
