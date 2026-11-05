package org.example.models;

// ==========================================
// Fichier : models/Utilisateur.java
// ==========================================


import java.sql.Timestamp;

public class Utilisateur {
    private int idUtilisateur;
    private String username;
    private String password;
    private String nomComplet;
    private String role;
    private boolean actif;
    private Timestamp dateCreation;
    private Timestamp dernierLogin;

    public Utilisateur() {}

    // Getters et Setters
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public Timestamp getDernierLogin() { return dernierLogin; }
    public void setDernierLogin(Timestamp dernierLogin) { this.dernierLogin = dernierLogin; }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
