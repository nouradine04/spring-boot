package com.nouradine.crudspringboot.models;

public class Produit {
    private Long id;
    private String nom;
    private String description;
    private Double prix;


    public Produit(){}
    public Produit(Long id, String nom, String description, Double prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
    }
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
