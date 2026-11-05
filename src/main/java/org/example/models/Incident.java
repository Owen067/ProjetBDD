package org.example.models;

// ==========================================
// Fichier : models/Incident.java
// ==========================================

import java.sql.Timestamp;

public class Incident {
    private int idIncident;
    private String type;
    private String description;
    private Timestamp dateHeure;
    private int idTrajet;
    private String trajetInfo;
    private String busInfo;
    private String ligneInfo;

    public Incident() {}

    // Getters et Setters
    public int getIdIncident() { return idIncident; }
    public void setIdIncident(int idIncident) { this.idIncident = idIncident; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getDateHeure() { return dateHeure; }
    public void setDateHeure(Timestamp dateHeure) { this.dateHeure = dateHeure; }
    public int getIdTrajet() { return idTrajet; }
    public void setIdTrajet(int idTrajet) { this.idTrajet = idTrajet; }
    public String getTrajetInfo() { return trajetInfo; }
    public void setTrajetInfo(String trajetInfo) { this.trajetInfo = trajetInfo; }
    public String getBusInfo() { return busInfo; }
    public void setBusInfo(String busInfo) { this.busInfo = busInfo; }
    public String getLigneInfo() { return ligneInfo; }
    public void setLigneInfo(String ligneInfo) { this.ligneInfo = ligneInfo; }
}