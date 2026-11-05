package org.example.models;

// ==========================================
// Fichier : models/Ligne.java
// ==========================================


import java.util.List;

public class Ligne {
    private int idLigne;
    private String nomLigne;
    private String pointDepart;
    private String pointArrivee;
    private int nbStations;
    private int nbEtudiants;

    public Ligne() {}

    public Ligne(int idLigne, String nomLigne, String pointDepart, String pointArrivee) {
        this.idLigne = idLigne;
        this.nomLigne = nomLigne;
        this.pointDepart = pointDepart;
        this.pointArrivee = pointArrivee;
    }

    // Getters et Setters
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
    public String getNomLigne() { return nomLigne; }
    public void setNomLigne(String nomLigne) { this.nomLigne = nomLigne; }
    public String getPointDepart() { return pointDepart; }
    public void setPointDepart(String pointDepart) { this.pointDepart = pointDepart; }
    public String getPointArrivee() { return pointArrivee; }
    public void setPointArrivee(String pointArrivee) { this.pointArrivee = pointArrivee; }
    public int getNbStations() { return nbStations; }
    public void setNbStations(int nbStations) { this.nbStations = nbStations; }
    public int getNbEtudiants() { return nbEtudiants; }
    public void setNbEtudiants(int nbEtudiants) { this.nbEtudiants = nbEtudiants; }
}