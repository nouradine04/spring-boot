package com.nouradine.crudspringboot.controller;

import com.nouradine.crudspringboot.models.Produit;
import com.nouradine.crudspringboot.service.ProduitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitService produitService;

    @Test
    void testAfficherProduits() throws Exception {
        mockMvc.perform(get("/produits"))
                .andExpect(status().isOk())
                .andExpect(view().name("produits"))
                .andExpect(model().attributeExists("produits"));
    }

    @Test
    void testAjouterProduit() throws Exception {
        mockMvc.perform(post("/produits")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nom", "Produit Test")
                        .param("prix", "200"))
                .andExpect(status().is3xxRedirection()) // Redirection après ajout
                .andExpect(redirectedUrl("/produits"));

        // Vérification après l'ajout
        assertEquals(1, produitService.getAllProduits().size());
    }
}
