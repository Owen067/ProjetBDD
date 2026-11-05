package org.example.dao;

// ==========================================
// Fichier : dao/AbonnementDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Abonnement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementDAO {

    public List<Abonnement> getAllAbonnements() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, e.nom as nom_etudiant, e.prenom, l.nom_ligne " +
                "FROM ABONNEMENT a " +
                "JOIN ETUDIANT e ON a.matricule = e.matricule " +
                "JOIN LIGNE l ON a.id_ligne = l.id_ligne " +
                "ORDER BY a.date_debut DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Abonnement abo = new Abonnement();
                abo.setIdAbonnement(rs.getInt("id_abonnement"));
                abo.setDateDebut(rs.getDate("date_debut"));
                abo.setDateFin(rs.getDate("date_fin"));
                abo.setActif(rs.getBoolean("actif"));
                abo.setMatricule(rs.getString("matricule"));
                abo.setIdLigne(rs.getInt("id_ligne"));
                abo.setNomEtudiant(rs.getString("nom_etudiant") + " " + rs.getString("prenom"));
                abo.setNomLigne(rs.getString("nom_ligne"));
                abonnements.add(abo);
            }
        }
        return abonnements;
    }

    public void ajouterAbonnement(Abonnement abonnement) throws SQLException {
        // Désactiver les anciens abonnements actifs de l'étudiant
        String desactiverSql = "UPDATE ABONNEMENT SET actif = 0 WHERE matricule = ? AND actif = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(desactiverSql)) {
            pstmt.setString(1, abonnement.getMatricule());
            pstmt.executeUpdate();
        }

        // Ajouter le nouvel abonnement
        String sql = "INSERT INTO ABONNEMENT (id_abonnement, date_debut, date_fin, actif, matricule, id_ligne) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, abonnement.getIdAbonnement());
            pstmt.setDate(2, abonnement.getDateDebut());
            pstmt.setDate(3, abonnement.getDateFin());
            pstmt.setBoolean(4, abonnement.isActif());
            pstmt.setString(5, abonnement.getMatricule());
            pstmt.setInt(6, abonnement.getIdLigne());
            pstmt.executeUpdate();
        }
    }

    public void desactiverAbonnement(int idAbonnement) throws SQLException {
        String sql = "UPDATE ABONNEMENT SET actif = 0 WHERE id_abonnement = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAbonnement);
            pstmt.executeUpdate();
        }
    }

    public void supprimerAbonnement(int idAbonnement) throws SQLException {
        String sql = "DELETE FROM ABONNEMENT WHERE id_abonnement = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAbonnement);
            pstmt.executeUpdate();
        }
    }

    public List<Abonnement> getAbonnementsByEtudiant(String matricule) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, l.nom_ligne FROM ABONNEMENT a " +
                "JOIN LIGNE l ON a.id_ligne = l.id_ligne " +
                "WHERE a.matricule = ? ORDER BY a.date_debut DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricule);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Abonnement abo = new Abonnement();
                abo.setIdAbonnement(rs.getInt("id_abonnement"));
                abo.setDateDebut(rs.getDate("date_debut"));
                abo.setDateFin(rs.getDate("date_fin"));
                abo.setActif(rs.getBoolean("actif"));
                abo.setMatricule(rs.getString("matricule"));
                abo.setIdLigne(rs.getInt("id_ligne"));
                abo.setNomLigne(rs.getString("nom_ligne"));
                abonnements.add(abo);
            }
        }
        return abonnements;
    }
}
