package org.example.dao;

// ==========================================
// Fichier : dao/UtilisateurDAO.java
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.models.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE username = ? AND password = ? AND actif = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            boolean authenticated = rs.next();

            if (authenticated) {
                // Mettre à jour la date du dernier login
                updateLastLogin(username);
            }

            return authenticated;
        }
    }

    public Utilisateur getUtilisateur(String username) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setIdUtilisateur(rs.getInt("id_utilisateur"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNomComplet(rs.getString("nom_complet"));
                user.setRole(rs.getString("role"));
                user.setActif(rs.getBoolean("actif"));
                user.setDateCreation(rs.getTimestamp("date_creation"));
                user.setDernierLogin(rs.getTimestamp("dernier_login"));
                return user;
            }
        }
        return null;
    }

    private void updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET dernier_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    public List<Utilisateur> getAllUtilisateurs() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM UTILISATEUR ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setIdUtilisateur(rs.getInt("id_utilisateur"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNomComplet(rs.getString("nom_complet"));
                user.setRole(rs.getString("role"));
                user.setActif(rs.getBoolean("actif"));
                user.setDateCreation(rs.getTimestamp("date_creation"));
                user.setDernierLogin(rs.getTimestamp("dernier_login"));
                utilisateurs.add(user);
            }
        }
        return utilisateurs;
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO UTILISATEUR (username, password, nom_complet, role, actif) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getNomComplet());
            pstmt.setString(4, utilisateur.getRole());
            pstmt.setBoolean(5, utilisateur.isActif());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                utilisateur.setIdUtilisateur(rs.getInt(1));
            }
        }
    }

    public void modifierUtilisateur(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET nom_complet = ?, role = ?, actif = ? WHERE id_utilisateur = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getNomComplet());
            pstmt.setString(2, utilisateur.getRole());
            pstmt.setBoolean(3, utilisateur.isActif());
            pstmt.setInt(4, utilisateur.getIdUtilisateur());
            pstmt.executeUpdate();
        }
    }

    public void changerMotDePasse(int idUtilisateur, String nouveauMotDePasse) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET password = ? WHERE id_utilisateur = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauMotDePasse);
            pstmt.setInt(2, idUtilisateur);
            pstmt.executeUpdate();
        }
    }

    public void supprimerUtilisateur(int idUtilisateur) throws SQLException {
        String sql = "DELETE FROM UTILISATEUR WHERE id_utilisateur = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            pstmt.executeUpdate();
        }
    }
}