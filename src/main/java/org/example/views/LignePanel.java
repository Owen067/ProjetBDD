package org.example.views;

// ==========================================
// Fichier : views/LignePanel.java
// ==========================================


import org.example.dao.LigneDAO;
import org.example.models.Ligne;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class LignePanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable ligneTable;
    private LigneDAO ligneDAO;
    private DefaultTableModel tableModel;

    public LignePanel() {
        ligneDAO = new LigneDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadLigneData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Lignes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);
        JButton stationsButton = createButton("📍 Voir Stations", new Color(156, 39, 176));

        addButton.addActionListener(e -> showLigneDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = ligneTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idLigne = (int) ligneTable.getValueAt(selectedRow, 0);
                try {
                    List<Ligne> lignes = ligneDAO.getAllLignes();
                    Ligne ligne = lignes.stream().filter(l -> l.getIdLigne() == idLigne).findFirst().orElse(null);
                    if (ligne != null) showLigneDialog(ligne);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner une ligne à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = ligneTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette ligne ?\nAttention: Cela supprimera aussi les abonnements associés.",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idLigne = (int) ligneTable.getValueAt(selectedRow, 0);
                    try {
                        ligneDAO.supprimerLigne(idLigne);
                        loadLigneData();
                        showSuccess("Ligne supprimée avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner une ligne à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadLigneData());

        stationsButton.addActionListener(e -> {
            int selectedRow = ligneTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idLigne = (int) ligneTable.getValueAt(selectedRow, 0);
                String nomLigne = (String) ligneTable.getValueAt(selectedRow, 1);
                showStationsDialog(idLigne, nomLigne);
            } else {
                showWarning("Veuillez sélectionner une ligne");
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(stationsButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Nom Ligne", "Départ", "Arrivée", "Nb Stations", "Nb Étudiants"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ligneTable = new JTable(tableModel);
        ligneTable.setBackground(BG_SECONDAIRE);
        ligneTable.setForeground(TEXT_COLOR);
        ligneTable.setRowHeight(30);
        ligneTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ligneTable.getTableHeader().setBackground(ACCENT_COLOR);
        ligneTable.getTableHeader().setForeground(Color.WHITE);
        ligneTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ligneTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(ligneTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLigneData() {
        try {
            List<Ligne> lignes = ligneDAO.getAllLignes();
            tableModel.setRowCount(0);
            for (Ligne l : lignes) {
                tableModel.addRow(new Object[]{
                        l.getIdLigne(),
                        l.getNomLigne(),
                        l.getPointDepart(),
                        l.getPointArrivee(),
                        l.getNbStations(),
                        l.getNbEtudiants()
                });
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void showStationsDialog(int idLigne, String nomLigne) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Stations de la ligne: " + nomLigne);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SECONDAIRE);

        String[] columns = {"Ordre", "ID", "Nom Station", "Adresse", "Ville"};
        DefaultTableModel stationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable stationTable = new JTable(stationModel);
        stationTable.setBackground(BG_SECONDAIRE);
        stationTable.setForeground(TEXT_COLOR);
        stationTable.setRowHeight(30);
        stationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stationTable.getTableHeader().setBackground(ACCENT_COLOR);
        stationTable.getTableHeader().setForeground(Color.WHITE);

        try {
            List<LigneDAO.StationLigne> stations = ligneDAO.getStationsByLigne(idLigne);
            for (LigneDAO.StationLigne s : stations) {
                stationModel.addRow(new Object[]{
                        s.getOrdre(),
                        s.getIdStation(),
                        s.getNomStation(),
                        s.getAdresse(),
                        s.getVille()
                });
            }
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(stationTable);
        scrollPane.setBackground(BG_PRINCIPAL);

        JButton closeButton = createButton("Fermer", ACCENT_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_SECONDAIRE);
        buttonPanel.add(closeButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showLigneDialog(Ligne ligne) {
        JDialog dialog = new JDialog();
        dialog.setTitle(ligne == null ? "Ajouter une Ligne" : "Modifier une Ligne");
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_SECONDAIRE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("ID:");
        idLabel.setForeground(TEXT_COLOR);
        JTextField idField = new JTextField(20);
        idField.setBackground(BG_PRINCIPAL);
        idField.setForeground(TEXT_COLOR);
        idField.setCaretColor(ACCENT_COLOR);

        JLabel nomLabel = new JLabel("Nom Ligne:");
        nomLabel.setForeground(TEXT_COLOR);
        JTextField nomField = new JTextField(20);
        nomField.setBackground(BG_PRINCIPAL);
        nomField.setForeground(TEXT_COLOR);

        JLabel departLabel = new JLabel("Point Départ:");
        departLabel.setForeground(TEXT_COLOR);
        JTextField departField = new JTextField(20);
        departField.setBackground(BG_PRINCIPAL);
        departField.setForeground(TEXT_COLOR);

        JLabel arriveeLabel = new JLabel("Point Arrivée:");
        arriveeLabel.setForeground(TEXT_COLOR);
        JTextField arriveeField = new JTextField(20);
        arriveeField.setBackground(BG_PRINCIPAL);
        arriveeField.setForeground(TEXT_COLOR);

        if (ligne != null) {
            idField.setText(String.valueOf(ligne.getIdLigne()));
            idField.setEditable(false);
            nomField.setText(ligne.getNomLigne());
            departField.setText(ligne.getPointDepart());
            arriveeField.setText(ligne.getPointArrivee());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nomLabel, gbc);
        gbc.gridx = 1;
        panel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(departLabel, gbc);
        gbc.gridx = 1;
        panel.add(departField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(arriveeLabel, gbc);
        gbc.gridx = 1;
        panel.add(arriveeField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty() || nomField.getText().isEmpty()) {
                showWarning("Veuillez remplir les champs obligatoires (ID et Nom)");
                return;
            }

            try {
                Ligne newLigne = new Ligne();
                newLigne.setIdLigne(Integer.parseInt(idField.getText()));
                newLigne.setNomLigne(nomField.getText());
                newLigne.setPointDepart(departField.getText());
                newLigne.setPointArrivee(arriveeField.getText());

                if (ligne == null) {
                    ligneDAO.ajouterLigne(newLigne);
                    showSuccess("Ligne ajoutée avec succès");
                } else {
                    ligneDAO.modifierLigne(newLigne);
                    showSuccess("Ligne modifiée avec succès");
                }
                loadLigneData();
                dialog.dispose();
            } catch (SQLException | NumberFormatException ex) {
                showError("Erreur: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BG_SECONDAIRE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 15, 8, 15));
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}