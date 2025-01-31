package com.nouradine.crudspringboot.service;

import com.nouradine.crudspringboot.models.Produit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProduitService {

    List<Produit> produits = new ArrayList<Produit>();

    public List<Produit>getAllProduits(){
        return produits;
    }
    public void ajouter (Produit produit){
        produits.add(produit);
    }


}
