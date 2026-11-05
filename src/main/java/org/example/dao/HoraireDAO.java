package org.example.dao;

// ==========================================
// Fichier : dao/HoraireDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Horaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoraireDAO {

    public List<Horaire> getAllHoraires() throws SQLException {
        List<Horaire> horaires = new ArrayList<>();
        String sql = "SELECT h.*, l.nom_ligne FROM HORAIRE h " +
                "JOIN LIGNE l ON h.id_ligne = l.id_ligne " +
                "ORDER BY h.jour, h.heure_depart";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Horaire horaire = new Horaire();
                horaire.setIdHoraire(rs.getInt("id_horaire"));
                horaire.setHeureDepart(rs.getTime("heure_depart"));
                horaire.setIdLigne(rs.getInt("id_ligne"));
                horaire.setHeureRetour(rs.getTime("heure_retour"));
                horaire.setJour(rs.getString("jour"));
                horaire.setNomLigne(rs.getString("nom_ligne"));
                horaires.add(horaire);
            }
        }
        return horaires;
    }

    public void ajouterHoraire(Horaire horaire) throws SQLException {
        String sql = "INSERT INTO HORAIRE ( heure_depart, id_ligne, heure_retour, jour) VALUES ( ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTime(1, horaire.getHeureDepart());
            pstmt.setInt(2, horaire.getIdLigne());
            pstmt.setTime(3, horaire.getHeureRetour());
            pstmt.setString(4, horaire.getJour());
            pstmt.executeUpdate();
        }
    }

    public void modifierHoraire(Horaire horaire) throws SQLException {
        String sql = "UPDATE HORAIRE SET heure_depart=?, id_ligne=?, heure_retour=?, jour=? WHERE id_horaire=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTime(1, horaire.getHeureDepart());
            pstmt.setInt(2, horaire.getIdLigne());
            pstmt.setTime(3, horaire.getHeureRetour());
            pstmt.setString(4, horaire.getJour());
            pstmt.setInt(5, horaire.getIdHoraire());
            pstmt.executeUpdate();
        }
    }

    public void supprimerHoraire(int idHoraire) throws SQLException {
        String sql = "DELETE FROM HORAIRE WHERE id_horaire=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHoraire);
            pstmt.executeUpdate();
        }
    }

    public List<Horaire> filtrerParLigne(int idLigne) throws SQLException {
        List<Horaire> horaires = new ArrayList<>();
        String sql = "SELECT h.*, l.nom_ligne FROM HORAIRE h " +
                "JOIN LIGNE l ON h.id_ligne = l.id_ligne " +
                "WHERE h.id_ligne = ? ORDER BY h.jour, h.heure_depart";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLigne);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Horaire horaire = new Horaire();
                horaire.setIdHoraire(rs.getInt("id_horaire"));
                horaire.setHeureDepart(rs.getTime("heure_depart"));
                horaire.setIdLigne(rs.getInt("id_ligne"));
                horaire.setHeureRetour(rs.getTime("heure_retour"));
                horaire.setJour(rs.getString("jour"));
                horaire.setNomLigne(rs.getString("nom_ligne"));
                horaires.add(horaire);
            }
        }
        return horaires;
    }
}