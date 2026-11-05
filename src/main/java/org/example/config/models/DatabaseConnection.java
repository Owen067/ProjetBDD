package org.example.config.models;

// ==========================================
// Fichier : config/DatabaseConnection.java (CORRIGÉ)
// ==========================================


import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/ProjetBDD?useSSL=false&serverTimezone=UTC&autoReconnect=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ← METTRE VOTRE MOT DE PASSE ICI
    private static Connection connection = null;

    public static synchronized Connection getConnection() throws SQLException {
        try {
            // Vérifier si la connexion existe et est valide
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Charger le driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Créer une nouvelle connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base de données établie avec succès");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour tester la connexion
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
}