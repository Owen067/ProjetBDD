package org.example.dao;

// ==========================================
// Fichier : dao/IncidentDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Incident;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    public List<Incident> getAllIncidents() throws SQLException {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, t.id_trajet as trajet_id, b.immatriculation, l.nom_ligne " +
                "FROM INCIDENT i " +
                "JOIN TRAJET t ON i.id_trajet = t.id_trajet " +
                "JOIN BUS b ON t.id_bus = b.id_bus " +
                "JOIN LIGNE l ON t.id_ligne = l.id_ligne " +
                "ORDER BY i.date_heure DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Incident incident = new Incident();
                incident.setIdIncident(rs.getInt("id_incident"));
                incident.setType(rs.getString("type"));
                incident.setDescription(rs.getString("description"));
                incident.setDateHeure(rs.getTimestamp("date_heure"));
                incident.setIdTrajet(rs.getInt("id_trajet"));
                incident.setTrajetInfo("Trajet #" + rs.getInt("trajet_id"));
                incident.setBusInfo(rs.getString("immatriculation"));
                incident.setLigneInfo(rs.getString("nom_ligne"));
                incidents.add(incident);
            }
        }
        return incidents;
    }

    public void ajouterIncident(Incident incident) throws SQLException {
        String sql = "INSERT INTO INCIDENT (type, description, date_heure, id_trajet) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, incident.getType());
            pstmt.setString(2, incident.getDescription());
            pstmt.setTimestamp(3, incident.getDateHeure());
            pstmt.setInt(4, incident.getIdTrajet());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                incident.setIdIncident(rs.getInt(1));
            }
        }
    }

    public void modifierIncident(Incident incident) throws SQLException {
        String sql = "UPDATE INCIDENT SET type=?, description=?, date_heure=?, id_trajet=? WHERE id_incident=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, incident.getType());
            pstmt.setString(2, incident.getDescription());
            pstmt.setTimestamp(3, incident.getDateHeure());
            pstmt.setInt(4, incident.getIdTrajet());
            pstmt.setInt(5, incident.getIdIncident());
            pstmt.executeUpdate();
        }
    }

    public void supprimerIncident(int idIncident) throws SQLException {
        String sql = "DELETE FROM INCIDENT WHERE id_incident=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idIncident);
            pstmt.executeUpdate();
        }
    }

    public List<Incident> getIncidentsRecents(int limit) throws SQLException {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, t.id_trajet as trajet_id, b.immatriculation, l.nom_ligne " +
                "FROM INCIDENT i " +
                "JOIN TRAJET t ON i.id_trajet = t.id_trajet " +
                "JOIN BUS b ON t.id_bus = b.id_bus " +
                "JOIN LIGNE l ON t.id_ligne = l.id_ligne " +
                "ORDER BY i.date_heure DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Incident incident = new Incident();
                incident.setIdIncident(rs.getInt("id_incident"));
                incident.setType(rs.getString("type"));
                incident.setDescription(rs.getString("description"));
                incident.setDateHeure(rs.getTimestamp("date_heure"));
                incident.setIdTrajet(rs.getInt("id_trajet"));
                incident.setTrajetInfo("Trajet #" + rs.getInt("trajet_id"));
                incident.setBusInfo(rs.getString("immatriculation"));
                incident.setLigneInfo(rs.getString("nom_ligne"));
                incidents.add(incident);
            }
        }
        return incidents;
    }

    public int getIncidentsAujourdhui() throws SQLException {
        String sql = "SELECT COUNT(*) FROM INCIDENT WHERE DATE(date_heure) = CURDATE()";
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
