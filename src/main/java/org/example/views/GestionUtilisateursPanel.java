package org.example.views;

// ==========================================
// Fichier : views/GestionUtilisateursPanel.java
// ==========================================


import org.example.dao.UtilisateurDAO;
import org.example.models.Utilisateur;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class GestionUtilisateursPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable userTable;
    private UtilisateurDAO utilisateurDAO;
    private DefaultTableModel tableModel;

    public GestionUtilisateursPanel() {
        utilisateurDAO = new UtilisateurDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadUserData();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("👥 Gestion des Utilisateurs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Nom Complet", "Rôle", "Actif", "Date Création", "Dernier Login"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setBackground(BG_SECONDAIRE);
        userTable.setForeground(TEXT_COLOR);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setBackground(ACCENT_COLOR);
        userTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUserData() {
        try {
            List<Utilisateur> users = utilisateurDAO.getAllUtilisateurs();
            tableModel.setRowCount(0);
            for (Utilisateur u : users) {
                tableModel.addRow(new Object[]{
                        u.getIdUtilisateur(),
                        u.getUsername(),
                        u.getNomComplet(),
                        u.getRole(),
                        u.isActif() ? "✅ Oui" : "❌ Non",
                        u.getDateCreation(),
                        u.getDernierLogin() != null ? u.getDernierLogin() : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
}