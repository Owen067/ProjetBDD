package org.example.models;

// ==========================================
// Fichier : models/Station.java
// ==========================================


public class Station {
    private int idStation;
    private String nomStation;
    private String adresse;
    private String ville;

    public Station() {}

    public Station(int idStation, String nomStation, String adresse, String ville) {
        this.idStation = idStation;
        this.nomStation = nomStation;
        this.adresse = adresse;
        this.ville = ville;
    }

    // Getters et Setters
    public int getIdStation() { return idStation; }
    public void setIdStation(int idStation) { this.idStation = idStation; }
    public String getNomStation() { return nomStation; }
    public void setNomStation(String nomStation) { this.nomStation = nomStation; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
}