package org.example.models;

// ==========================================
// Fichier : models/Etudiant.java
// ==========================================


public class Etudiant {
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ligneAbonnee; // Pour affichage

    public Etudiant() {}

    public Etudiant(String matricule, String nom, String prenom,
                    String email, String telephone) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getLigneAbonnee() { return ligneAbonnee; }
    public void setLigneAbonnee(String ligneAbonnee) { this.ligneAbonnee = ligneAbonnee; }
}
