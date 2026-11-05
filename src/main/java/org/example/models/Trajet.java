package org.example.models;
// ==========================================
// Fichier : models/Trajet.java
// ==========================================

import java.sql.Date;
import java.sql.Time;

public class Trajet {
    private int idTrajet;
    private Date date;
    private Time heureReelle;
    private String statut;
    private int idBus;
    private int idLigne;
    private int idHoraire;
    private String busImmatriculation;
    private String ligneNom;

    public Trajet() {}

    // Getters et Setters
    public int getIdTrajet() { return idTrajet; }
    public void setIdTrajet(int idTrajet) { this.idTrajet = idTrajet; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getHeureReelle() { return heureReelle; }
    public void setHeureReelle(Time heureReelle) { this.heureReelle = heureReelle; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
    public int getIdHoraire() { return idHoraire; }
    public void setIdHoraire(int idHoraire) { this.idHoraire = idHoraire; }
    public String getBusImmatriculation() { return busImmatriculation; }
    public void setBusImmatriculation(String busImmatriculation) { this.busImmatriculation = busImmatriculation; }
    public String getLigneNom() { return ligneNom; }
    public void setLigneNom(String ligneNom) { this.ligneNom = ligneNom; }
}