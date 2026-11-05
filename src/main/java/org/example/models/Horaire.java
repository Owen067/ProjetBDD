package org.example.models;

// ==========================================
// Fichier : models/Horaire.java
// ==========================================

import java.sql.Time;

public class Horaire {
    private int idHoraire;
    private Time heureDepart;
    private int idLigne;
    private Time heureRetour;
    private String jour;
    private String nomLigne;

    public Horaire() {}

    public Horaire(int idHoraire, Time heureDepart, int idLigne,
                   Time heureRetour, String jour) {
        this.idHoraire = idHoraire;
        this.heureDepart = heureDepart;
        this.idLigne = idLigne;
        this.heureRetour = heureRetour;
        this.jour = jour;
    }

    // Getters et Setters
    public int getIdHoraire() { return idHoraire; }
    public void setIdHoraire(int idHoraire) { this.idHoraire = idHoraire; }
    public Time getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Time heureDepart) { this.heureDepart = heureDepart; }
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
    public Time getHeureRetour() { return heureRetour; }
    public void setHeureRetour(Time heureRetour) { this.heureRetour = heureRetour; }
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }
    public String getNomLigne() { return nomLigne; }
    public void setNomLigne(String nomLigne) { this.nomLigne = nomLigne; }
}