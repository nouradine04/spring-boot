package com.nouradine.crudspringboot.controller;

import com.nouradine.crudspringboot.models.Produit;
import com.nouradine.crudspringboot.service.ProduitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping("/produits")
    public String afficherProduits(Model model) {
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("produit", new Produit());
        return "produits";
    }

    @PostMapping("/produits")
    public String ajouterProduit(@ModelAttribute Produit produit) {
        produitService.ajouter(produit);
        return "redirect:/produits";
    }
}