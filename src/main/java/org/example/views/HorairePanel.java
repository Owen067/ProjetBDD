package org.example.views;

// ==========================================
// Fichier : views/HorairePanel.java
// ==========================================


import org.example.dao.HoraireDAO;
import org.example.dao.LigneDAO;
import org.example.models.Horaire;
import org.example.models.Ligne;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class HorairePanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable horaireTable;
    private HoraireDAO horaireDAO;
    private LigneDAO ligneDAO;
    private JComboBox<String> ligneFilter;
    private JComboBox<String> jourFilter;
    private DefaultTableModel tableModel;

    public HorairePanel() {
        horaireDAO = new HoraireDAO();
        ligneDAO = new LigneDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadHoraireData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Horaires");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showHoraireDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = horaireTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idHoraire = (int) horaireTable.getValueAt(selectedRow, 0);
                try {
                    List<Horaire> horaires = horaireDAO.getAllHoraires();
                    Horaire horaire = horaires.stream().filter(h -> h.getIdHoraire() == idHoraire).findFirst().orElse(null);
                    if (horaire != null) showHoraireDialog(horaire);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner un horaire à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = horaireTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet horaire ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idHoraire = (int) horaireTable.getValueAt(selectedRow, 0);
                    try {
                        horaireDAO.supprimerHoraire(idHoraire);
                        loadHoraireData();
                        showSuccess("Horaire supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un horaire à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadHoraireData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BG_PRINCIPAL);

        JLabel ligneLabel = new JLabel("Filtrer par Ligne:");
        ligneLabel.setForeground(TEXT_COLOR);
        ligneFilter = new JComboBox<>();
        ligneFilter.addItem("Toutes");
        loadLignesToCombo();
        ligneFilter.setBackground(BG_SECONDAIRE);
        ligneFilter.setForeground(TEXT_COLOR);

        JLabel jourLabel = new JLabel("Jour:");
        jourLabel.setForeground(TEXT_COLOR);
        String[] jours = {"Tous", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        jourFilter = new JComboBox<>(jours);
        jourFilter.setBackground(BG_SECONDAIRE);
        jourFilter.setForeground(TEXT_COLOR);

        JButton filterButton = createButton("🔍 Filtrer", ACCENT_COLOR);
        filterButton.addActionListener(e -> filterHoraires());

        filterPanel.add(ligneLabel);
        filterPanel.add(ligneFilter);
        filterPanel.add(jourLabel);
        filterPanel.add(jourFilter);
        filterPanel.add(filterButton);

        String[] columns = {"ID", "Ligne", "Jour", "Heure Départ", "Heure Retour"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        horaireTable = new JTable(tableModel);
        horaireTable.setBackground(BG_SECONDAIRE);
        horaireTable.setForeground(TEXT_COLOR);
        horaireTable.setRowHeight(30);
        horaireTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        horaireTable.getTableHeader().setBackground(ACCENT_COLOR);
        horaireTable.getTableHeader().setForeground(Color.WHITE);
        horaireTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        horaireTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(horaireTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_PRINCIPAL);
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadLignesToCombo() {
        try {
            List<Ligne> lignes = ligneDAO.getAllLignes();
            for (Ligne ligne : lignes) {
                ligneFilter.addItem(ligne.getNomLigne());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHoraireData() {
        try {
            List<Horaire> horaires = horaireDAO.getAllHoraires();
            tableModel.setRowCount(0);
            for (Horaire h : horaires) {
                tableModel.addRow(new Object[]{
                        h.getIdHoraire(),
                        h.getNomLigne(),
                        h.getJour(),
                        h.getHeureDepart(),
                        h.getHeureRetour()
                });
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void filterHoraires() {
        String selectedLigne = (String) ligneFilter.getSelectedItem();
        String selectedJour = (String) jourFilter.getSelectedItem();

        try {
            List<Horaire> horaires = horaireDAO.getAllHoraires();
            tableModel.setRowCount(0);

            for (Horaire h : horaires) {
                boolean matchLigne = selectedLigne.equals("Toutes") || h.getNomLigne().equals(selectedLigne);
                boolean matchJour = selectedJour.equals("Tous") || h.getJour().equals(selectedJour);

                if (matchLigne && matchJour) {
                    tableModel.addRow(new Object[]{
                            h.getIdHoraire(),
                            h.getNomLigne(),
                            h.getJour(),
                            h.getHeureDepart(),
                            h.getHeureRetour()
                    });
                }
            }
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void showHoraireDialog(Horaire horaire) {
        JDialog dialog = new JDialog();
        dialog.setTitle(horaire == null ? "Ajouter un Horaire" : "Modifier un Horaire");
        dialog.setSize(450, 400);
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

        JLabel ligneLabel = new JLabel("Ligne:");
        ligneLabel.setForeground(TEXT_COLOR);
        JComboBox<String> ligneCombo = new JComboBox<>();
        try {
            List<Ligne> lignes = ligneDAO.getAllLignes();
            for (Ligne l : lignes) {
                ligneCombo.addItem(l.getIdLigne() + " - " + l.getNomLigne());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ligneCombo.setBackground(BG_SECONDAIRE);
        ligneCombo.setForeground(TEXT_COLOR);

        JLabel jourLabel = new JLabel("Jour:");
        jourLabel.setForeground(TEXT_COLOR);
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        JComboBox<String> jourCombo = new JComboBox<>(jours);
        jourCombo.setBackground(BG_SECONDAIRE);
        jourCombo.setForeground(TEXT_COLOR);

        JLabel heureDepLabel = new JLabel("Heure Départ (HH:MM:SS):");
        heureDepLabel.setForeground(TEXT_COLOR);
        JTextField heureDepField = new JTextField(20);
        heureDepField.setBackground(BG_PRINCIPAL);
        heureDepField.setForeground(TEXT_COLOR);

        JLabel heureRetLabel = new JLabel("Heure Retour (HH:MM:SS):");
        heureRetLabel.setForeground(TEXT_COLOR);
        JTextField heureRetField = new JTextField(20);
        heureRetField.setBackground(BG_PRINCIPAL);
        heureRetField.setForeground(TEXT_COLOR);

        if (horaire != null) {
            idField.setText(String.valueOf(horaire.getIdHoraire()));
            idField.setEditable(false);
            // Sélectionner la ligne dans le combo
            for (int i = 0; i < ligneCombo.getItemCount(); i++) {
                if (ligneCombo.getItemAt(i).startsWith(String.valueOf(horaire.getIdLigne()))) {
                    ligneCombo.setSelectedIndex(i);
                    break;
                }
            }
            jourCombo.setSelectedItem(horaire.getJour());
            heureDepField.setText(horaire.getHeureDepart().toString());
            heureRetField.setText(horaire.getHeureRetour().toString());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ligneLabel, gbc);
        gbc.gridx = 1;
        panel.add(ligneCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(jourLabel, gbc);
        gbc.gridx = 1;
        panel.add(jourCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(heureDepLabel, gbc);
        gbc.gridx = 1;
        panel.add(heureDepField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(heureRetLabel, gbc);
        gbc.gridx = 1;
        panel.add(heureRetField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty() || heureDepField.getText().isEmpty() || heureRetField.getText().isEmpty()) {
                showWarning("Veuillez remplir tous les champs");
                return;
            }

            try {
                Horaire newHoraire = new Horaire();
                newHoraire.setIdHoraire(Integer.parseInt(idField.getText()));
                String ligneSelected = (String) ligneCombo.getSelectedItem();
                int idLigne = Integer.parseInt(ligneSelected.split(" - ")[0]);
                newHoraire.setIdLigne(idLigne);
                newHoraire.setJour((String) jourCombo.getSelectedItem());
                newHoraire.setHeureDepart(Time.valueOf(heureDepField.getText()));
                newHoraire.setHeureRetour(Time.valueOf(heureRetField.getText()));

                if (horaire == null) {
                    horaireDAO.ajouterHoraire(newHoraire);
                    showSuccess("Horaire ajouté avec succès");
                } else {
                    horaireDAO.modifierHoraire(newHoraire);
                    showSuccess("Horaire modifié avec succès");
                }
                loadHoraireData();
                dialog.dispose();
            } catch (SQLException | IllegalArgumentException ex) {
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