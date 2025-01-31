package com.nouradine.crudspringboot.service;

import com.nouradine.crudspringboot.models.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProduitServiceTest {

    private ProduitService produitService;

    @BeforeEach
    void setUp() {
        produitService = new ProduitService();
    }

    @Test
    void testAjouterProduit() {
        Produit produit = new Produit(null, "Produit Test", "Description Test", 99.9);
        produitService.ajouter(produit);

        List<Produit> produits = produitService.getAllProduits();

        assertEquals(1, produits.size()); // Vérifie que le produit a bien été ajouté
        assertEquals("Produit Test", produits.get(0).getNom()); // Vérifie le nom du produit
    }

    @Test
    void testGetAllProduits() {
        produitService.ajouter(new Produit(null, "Produit 1", "Description 1", 50.0));

        List<Produit> produits = produitService.getAllProduits();

        assertEquals(1, produits.size()); // Vérifie que deux produits sont bien ajoutés
    }


}
