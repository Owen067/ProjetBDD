// Voici le contenu de MainApp.java - C'est la classe d'entrée
package org.example;  // ou sans package selon votre structure
// ==========================================
// Fichier : MainApp.java (AVEC NOUVEAU LOGIN)
// ==========================================
import org.example.views.LoginDialog;
import org.example.views.MainFrame;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Appliquer le thème FlatLaf
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());

                // Afficher le dialogue de login
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);

                // Vérifier si l'utilisateur est authentifié
                if (loginDialog.isAuthenticated()) {
                    // Lancer l'application principale
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    // Quitter l'application si l'utilisateur annule la connexion
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur lors du démarrage: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}