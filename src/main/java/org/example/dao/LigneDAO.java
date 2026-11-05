package org.example.dao;

// ==========================================
// Fichier : dao/LigneDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Ligne;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneDAO {

    public List<Ligne> getAllLignes() throws SQLException {
        List<Ligne> lignes = new ArrayList<>();
        String sql = "SELECT l.*, " +
                "COUNT(DISTINCT d.id_station) as nb_stations, " +
                "COUNT(DISTINCT a.matricule) as nb_etudiants " +
                "FROM LIGNE l " +
                "LEFT JOIN DESSERVIR d ON l.id_ligne = d.id_ligne " +
                "LEFT JOIN ABONNEMENT a ON l.id_ligne = a.id_ligne AND a.actif = 1 " +
                "GROUP BY l.id_ligne";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ligne ligne = new Ligne();
                ligne.setIdLigne(rs.getInt("id_ligne"));
                ligne.setNomLigne(rs.getString("nom_ligne"));
                ligne.setPointDepart(rs.getString("point_depart"));
                ligne.setPointArrivee(rs.getString("point_arrivee"));
                ligne.setNbStations(rs.getInt("nb_stations"));
                ligne.setNbEtudiants(rs.getInt("nb_etudiants"));
                lignes.add(ligne);
            }
        }
        return lignes;
    }

    public void ajouterLigne(Ligne ligne) throws SQLException {
        String sql = "INSERT INTO LIGNE (id_ligne, nom_ligne, point_depart, point_arrivee) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ligne.getIdLigne());
            pstmt.setString(2, ligne.getNomLigne());
            pstmt.setString(3, ligne.getPointDepart());
            pstmt.setString(4, ligne.getPointArrivee());
            pstmt.executeUpdate();
        }
    }

    public void modifierLigne(Ligne ligne) throws SQLException {
        String sql = "UPDATE LIGNE SET nom_ligne=?, point_depart=?, point_arrivee=? WHERE id_ligne=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ligne.getNomLigne());
            pstmt.setString(2, ligne.getPointDepart());
            pstmt.setString(3, ligne.getPointArrivee());
            pstmt.setInt(4, ligne.getIdLigne());
            pstmt.executeUpdate();
        }
    }

    public void supprimerLigne(int idLigne) throws SQLException {
        String sql = "DELETE FROM LIGNE WHERE id_ligne=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLigne);
            pstmt.executeUpdate();
        }
    }

    public List<StationLigne> getStationsByLigne(int idLigne) throws SQLException {
        List<StationLigne> stations = new ArrayList<>();
        String sql = "SELECT s.id_station, s.nom_station, s.adresse, s.ville, d.ordre " +
                "FROM STATION s JOIN DESSERVIR d ON s.id_station = d.id_station " +
                "WHERE d.id_ligne = ? ORDER BY d.ordre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLigne);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StationLigne sl = new StationLigne();
                sl.setIdStation(rs.getInt("id_station"));
                sl.setNomStation(rs.getString("nom_station"));
                sl.setAdresse(rs.getString("adresse"));
                sl.setVille(rs.getString("ville"));
                sl.setOrdre(rs.getInt("ordre"));
                stations.add(sl);
            }
        }
        return stations;
    }

    public int getTotalLignes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM LIGNE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Classe interne pour les stations d'une ligne
    public static class StationLigne {
        private int idStation;
        private String nomStation;
        private String adresse;
        private String ville;
        private int ordre;

        // Getters et Setters
        public int getIdStation() { return idStation; }
        public void setIdStation(int idStation) { this.idStation = idStation; }
        public String getNomStation() { return nomStation; }
        public void setNomStation(String nomStation) { this.nomStation = nomStation; }
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public String getVille() { return ville; }
        public void setVille(String ville) { this.ville = ville; }
        public int getOrdre() { return ordre; }
        public void setOrdre(int ordre) { this.ordre = ordre; }
    }
}