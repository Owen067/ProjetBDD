package org.example.models;
// ==========================================
// Fichier : models/Bus.java
// ==========================================


public class Bus {
    private int idBus;
    private String immatriculation;
    private int capacite;
    private String modele;
    private String ligneAffectee;

    public Bus() {}

    public Bus(int idBus, String immatriculation, int capacite, String modele) {
        this.idBus = idBus;
        this.immatriculation = immatriculation;
        this.capacite = capacite;
        this.modele = modele;
    }

    // Getters et Setters
    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public String getLigneAffectee() { return ligneAffectee; }
    public void setLigneAffectee(String ligneAffectee) { this.ligneAffectee = ligneAffectee; }
}