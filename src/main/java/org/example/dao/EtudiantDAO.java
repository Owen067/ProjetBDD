package org.example.dao;

// ==========================================
// Fichier : dao/EtudiantDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Etudiant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    public List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, l.nom_ligne FROM ETUDIANT e " +
                "LEFT JOIN ABONNEMENT a ON e.matricule = a.matricule AND a.actif = 1 " +
                "LEFT JOIN LIGNE l ON a.id_ligne = l.id_ligne";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Etudiant e = new Etudiant();
                e.setMatricule(rs.getString("matricule"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setEmail(rs.getString("email"));
                e.setTelephone(rs.getString("telephone"));
                e.setLigneAbonnee(rs.getString("nom_ligne"));
                etudiants.add(e);
            }
        }
        return etudiants;
    }

    public void ajouterEtudiant(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO ETUDIANT (matricule, nom, prenom, email, telephone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etudiant.getMatricule());
            pstmt.setString(2, etudiant.getNom());
            pstmt.setString(3, etudiant.getPrenom());
            pstmt.setString(4, etudiant.getEmail());
            pstmt.setString(5, etudiant.getTelephone());
            pstmt.executeUpdate();
        }
    }

    public void modifierEtudiant(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE ETUDIANT SET nom=?, prenom=?, email=?, telephone=? WHERE matricule=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getPrenom());
            pstmt.setString(3, etudiant.getEmail());
            pstmt.setString(4, etudiant.getTelephone());
            pstmt.setString(5, etudiant.getMatricule());
            pstmt.executeUpdate();
        }
    }

    public void supprimerEtudiant(String matricule) throws SQLException {
        String sql = "DELETE FROM ETUDIANT WHERE matricule=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricule);
            pstmt.executeUpdate();
        }
    }

    public List<Etudiant> rechercherEtudiants(String search) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, l.nom_ligne FROM ETUDIANT e " +
                "LEFT JOIN ABONNEMENT a ON e.matricule = a.matricule AND a.actif = 1 " +
                "LEFT JOIN LIGNE l ON a.id_ligne = l.id_ligne " +
                "WHERE e.nom LIKE ? OR e.matricule LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Etudiant e = new Etudiant();
                e.setMatricule(rs.getString("matricule"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setEmail(rs.getString("email"));
                e.setTelephone(rs.getString("telephone"));
                e.setLigneAbonnee(rs.getString("nom_ligne"));
                etudiants.add(e);
            }
        }
        return etudiants;
    }

    public int getTotalEtudiants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ETUDIANT";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}