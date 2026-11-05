package org.example.dao;

// ==========================================
// Fichier : dao/TrajetDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Trajet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrajetDAO {

    public List<Trajet> getAllTrajets() throws SQLException {
        List<Trajet> trajets = new ArrayList<>();
        String sql = "SELECT t.*, b.immatriculation, l.nom_ligne, h.heure_depart " +
                "FROM TRAJET t " +
                "JOIN BUS b ON t.id_bus = b.id_bus " +
                "JOIN LIGNE l ON t.id_ligne = l.id_ligne " +
                "JOIN HORAIRE h ON t.id_horaire = h.id_horaire " +
                "ORDER BY t.date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Trajet trajet = new Trajet();
                trajet.setIdTrajet(rs.getInt("id_trajet"));
                trajet.setDate(rs.getDate("date"));
                trajet.setHeureReelle(rs.getTime("heure_reelle"));
                trajet.setStatut(rs.getString("statut"));
                trajet.setIdBus(rs.getInt("id_bus"));
                trajet.setIdLigne(rs.getInt("id_ligne"));
                trajet.setIdHoraire(rs.getInt("id_horaire"));
                trajet.setBusImmatriculation(rs.getString("immatriculation"));
                trajet.setLigneNom(rs.getString("nom_ligne"));
                trajets.add(trajet);
            }
        }
        return trajets;
    }

    public void ajouterTrajet(Trajet trajet) throws SQLException {
        String sql = "INSERT INTO TRAJET (date, heure_reelle, statut, id_bus, id_ligne, id_horaire) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, trajet.getDate());
            pstmt.setTime(2, trajet.getHeureReelle());
            pstmt.setString(3, trajet.getStatut());
            pstmt.setInt(4, trajet.getIdBus());
            pstmt.setInt(5, trajet.getIdLigne());
            pstmt.setInt(6, trajet.getIdHoraire());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                trajet.setIdTrajet(rs.getInt(1));
            }
        }
    }

    public void modifierTrajet(Trajet trajet) throws SQLException {
        String sql = "UPDATE TRAJET SET date=?, heure_reelle=?, statut=?, id_bus=?, id_ligne=?, id_horaire=? " +
                "WHERE id_trajet=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, trajet.getDate());
            pstmt.setTime(2, trajet.getHeureReelle());
            pstmt.setString(3, trajet.getStatut());
            pstmt.setInt(4, trajet.getIdBus());
            pstmt.setInt(5, trajet.getIdLigne());
            pstmt.setInt(6, trajet.getIdHoraire());
            pstmt.setInt(7, trajet.getIdTrajet());
            pstmt.executeUpdate();
        }
    }

    public void supprimerTrajet(int idTrajet) throws SQLException {
        String sql = "DELETE FROM TRAJET WHERE id_trajet=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTrajet);
            pstmt.executeUpdate();
        }
    }

    public List<Trajet> getTrajetsRecents(int limit) throws SQLException {
        List<Trajet> trajets = new ArrayList<>();
        String sql = "SELECT t.*, b.immatriculation, l.nom_ligne, h.heure_depart " +
                "FROM TRAJET t " +
                "JOIN BUS b ON t.id_bus = b.id_bus " +
                "JOIN LIGNE l ON t.id_ligne = l.id_ligne " +
                "JOIN HORAIRE h ON t.id_horaire = h.id_horaire " +
                "ORDER BY t.date DESC, t.heure_reelle DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Trajet trajet = new Trajet();
                trajet.setIdTrajet(rs.getInt("id_trajet"));
                trajet.setDate(rs.getDate("date"));
                trajet.setHeureReelle(rs.getTime("heure_reelle"));
                trajet.setStatut(rs.getString("statut"));
                trajet.setIdBus(rs.getInt("id_bus"));
                trajet.setIdLigne(rs.getInt("id_ligne"));
                trajet.setIdHoraire(rs.getInt("id_horaire"));
                trajet.setBusImmatriculation(rs.getString("immatriculation"));
                trajet.setLigneNom(rs.getString("nom_ligne"));
                trajets.add(trajet);
            }
        }
        return trajets;
    }
}