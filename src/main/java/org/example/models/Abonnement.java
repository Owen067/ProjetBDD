package org.example.models;

// ==========================================
// Fichier : models/Abonnement.java
// ==========================================

import java.sql.Date;

public class Abonnement {
    private int idAbonnement;
    private Date dateDebut;
    private Date dateFin;
    private boolean actif;
    private String matricule;
    private int idLigne;
    private String nomEtudiant;
    private String nomLigne;

    public Abonnement() {}

    public int getIdAbonnement() { return idAbonnement; }
    public void setIdAbonnement(int idAbonnement) { this.idAbonnement = idAbonnement; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
    public String getNomEtudiant() { return nomEtudiant; }
    public void setNomEtudiant(String nomEtudiant) { this.nomEtudiant = nomEtudiant; }
    public String getNomLigne() { return nomLigne; }
    public void setNomLigne(String nomLigne) { this.nomLigne = nomLigne; }
}