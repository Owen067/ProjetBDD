package org.example.dao;

// ==========================================
// Fichier : dao/StationDAO.java (COMPLET)
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAO {

    public List<Station> getAllStations() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM STATION ORDER BY nom_station";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Station station = new Station();
                station.setIdStation(rs.getInt("id_station"));
                station.setNomStation(rs.getString("nom_station"));
                station.setAdresse(rs.getString("adresse"));
                station.setVille(rs.getString("ville"));
                stations.add(station);
            }
        }
        return stations;
    }

    public void ajouterStation(Station station) throws SQLException {
        String sql = "INSERT INTO STATION (id_station, nom_station, adresse, ville) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, station.getIdStation());
            pstmt.setString(2, station.getNomStation());
            pstmt.setString(3, station.getAdresse());
            pstmt.setString(4, station.getVille());
            pstmt.executeUpdate();
        }
    }

    public void modifierStation(Station station) throws SQLException {
        String sql = "UPDATE STATION SET nom_station=?, adresse=?, ville=? WHERE id_station=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, station.getNomStation());
            pstmt.setString(2, station.getAdresse());
            pstmt.setString(3, station.getVille());
            pstmt.setInt(4, station.getIdStation());
            pstmt.executeUpdate();
        }
    }

    public void supprimerStation(int idStation) throws SQLException {
        String sql = "DELETE FROM STATION WHERE id_station=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idStation);
            pstmt.executeUpdate();
        }
    }

    public List<Station> rechercherStationsParVille(String ville) throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM STATION WHERE ville LIKE ? ORDER BY nom_station";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + ville + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Station station = new Station();
                station.setIdStation(rs.getInt("id_station"));
                station.setNomStation(rs.getString("nom_station"));
                station.setAdresse(rs.getString("adresse"));
                station.setVille(rs.getString("ville"));
                stations.add(station);
            }
        }
        return stations;
    }
}