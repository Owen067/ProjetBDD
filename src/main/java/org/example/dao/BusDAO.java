package org.example.dao;
// ==========================================
// Fichier : dao/BusDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Bus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusDAO {

    public List<Bus> getAllBus() throws SQLException {
        List<Bus> busList = new ArrayList<>();
        String sql = "SELECT b.*, l.nom_ligne FROM BUS b " +
                "LEFT JOIN AFFECTER af ON b.id_bus = af.id_bus AND CURDATE() BETWEEN af.date_debut AND af.date_fin " +
                "LEFT JOIN LIGNE l ON af.id_ligne = l.id_ligne";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setImmatriculation(rs.getString("immatriculation"));
                bus.setCapacite(rs.getInt("capacite"));
                bus.setModele(rs.getString("modele"));
                bus.setLigneAffectee(rs.getString("nom_ligne"));
                busList.add(bus);
            }
        }
        return busList;
    }

    public void ajouterBus(Bus bus) throws SQLException {
        String sql = "INSERT INTO BUS (id_bus, immatriculation, capacite, modele) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bus.getIdBus());
            pstmt.setString(2, bus.getImmatriculation());
            pstmt.setInt(3, bus.getCapacite());
            pstmt.setString(4, bus.getModele());
            pstmt.executeUpdate();
        }
    }

    public void modifierBus(Bus bus) throws SQLException {
        String sql = "UPDATE BUS SET immatriculation=?, capacite=?, modele=? WHERE id_bus=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bus.getImmatriculation());
            pstmt.setInt(2, bus.getCapacite());
            pstmt.setString(3, bus.getModele());
            pstmt.setInt(4, bus.getIdBus());
            pstmt.executeUpdate();
        }
    }

    public void supprimerBus(int idBus) throws SQLException {
        String sql = "DELETE FROM BUS WHERE id_bus=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBus);
            pstmt.executeUpdate();
        }
    }

    public List<Bus> rechercherBus(String search) throws SQLException {
        List<Bus> busList = new ArrayList<>();
        String sql = "SELECT b.*, l.nom_ligne FROM BUS b " +
                "LEFT JOIN AFFECTER af ON b.id_bus = af.id_bus AND CURDATE() BETWEEN af.date_debut AND af.date_fin " +
                "LEFT JOIN LIGNE l ON af.id_ligne = l.id_ligne " +
                "WHERE b.immatriculation LIKE ? OR b.modele LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setImmatriculation(rs.getString("immatriculation"));
                bus.setCapacite(rs.getInt("capacite"));
                bus.setModele(rs.getString("modele"));
                bus.setLigneAffectee(rs.getString("nom_ligne"));
                busList.add(bus);
            }
        }
        return busList;
    }

    public int getTotalBus() throws SQLException {
        String sql = "SELECT COUNT(*) FROM BUS";
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