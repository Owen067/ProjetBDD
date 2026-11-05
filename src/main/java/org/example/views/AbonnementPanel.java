package org.example.views;

// ==========================================
// Fichier : views/AbonnementPanel.java
// ==========================================


import org.example.dao.AbonnementDAO;
import org.example.dao.EtudiantDAO;
import org.example.dao.LigneDAO;
import org.example.models.Abonnement;
import org.example.models.Etudiant;
import org.example.models.Ligne;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class AbonnementPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable abonnementTable;
    private AbonnementDAO abonnementDAO;
    private EtudiantDAO etudiantDAO;
    private LigneDAO ligneDAO;
    private DefaultTableModel tableModel;

    public AbonnementPanel() {
        abonnementDAO = new AbonnementDAO();
        etudiantDAO = new EtudiantDAO();
        ligneDAO = new LigneDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadAbonnementData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Abonnements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton desactiverButton = createButton("🔴 Désactiver", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showAbonnementDialog(null));
        desactiverButton.addActionListener(e -> {
            int selectedRow = abonnementTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idAbonnement = (int) abonnementTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous désactiver cet abonnement ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        abonnementDAO.desactiverAbonnement(idAbonnement);
                        loadAbonnementData();
                        showSuccess("Abonnement désactivé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un abonnement à désactiver");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = abonnementTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet abonnement ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idAbonnement = (int) abonnementTable.getValueAt(selectedRow, 0);
                    try {
                        abonnementDAO.supprimerAbonnement(idAbonnement);
                        loadAbonnementData();
                        showSuccess("Abonnement supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un abonnement à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadAbonnementData());

        buttonPanel.add(addButton);
        buttonPanel.add(desactiverButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Matricule", "Étudiant", "Ligne", "Date Début", "Date Fin", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        abonnementTable = new JTable(tableModel);
        abonnementTable.setBackground(BG_SECONDAIRE);
        abonnementTable.setForeground(TEXT_COLOR);
        abonnementTable.setRowHeight(30);
        abonnementTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        abonnementTable.getTableHeader().setBackground(ACCENT_COLOR);
        abonnementTable.getTableHeader().setForeground(Color.WHITE);
        abonnementTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        abonnementTable.setSelectionBackground(ACCENT_COLOR);

        // Custom renderer for status badge
        abonnementTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 6) {
                    String status = value.toString();
                    if (status.equals("Actif")) {
                        c.setForeground(SUCCESS_COLOR);
                        setText("🟢 Actif");
                    } else {
                        c.setForeground(new Color(158, 158, 158));
                        setText("⚪ Inactif");
                    }
                } else {
                    c.setForeground(TEXT_COLOR);
                }
                c.setBackground(isSelected ? ACCENT_COLOR : BG_SECONDAIRE);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(abonnementTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAbonnementData() {
        try {
            List<Abonnement> abonnements = abonnementDAO.getAllAbonnements();
            tableModel.setRowCount(0);
            for (Abonnement a : abonnements) {
                tableModel.addRow(new Object[]{
                        a.getIdAbonnement(),
                        a.getMatricule(),
                        a.getNomEtudiant(),
                        a.getNomLigne(),
                        a.getDateDebut(),
                        a.getDateFin(),
                        a.isActif() ? "Actif" : "Inactif"
                });
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void showAbonnementDialog(Abonnement abonnement) {
        JDialog dialog = new JDialog();
        dialog.setTitle(abonnement == null ? "Ajouter un Abonnement" : "Modifier un Abonnement");
        dialog.setSize(500, 450);
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

        JLabel etudiantLabel = new JLabel("Étudiant:");
        etudiantLabel.setForeground(TEXT_COLOR);
        JComboBox<String> etudiantCombo = new JComboBox<>();
        try {
            List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
            for (Etudiant e : etudiants) {
                etudiantCombo.addItem(e.getMatricule() + " - " + e.getNom() + " " + e.getPrenom());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        etudiantCombo.setBackground(BG_SECONDAIRE);
        etudiantCombo.setForeground(TEXT_COLOR);

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

        JLabel debutLabel = new JLabel("Date Début (YYYY-MM-DD):");
        debutLabel.setForeground(TEXT_COLOR);
        JTextField debutField = new JTextField(20);
        debutField.setBackground(BG_PRINCIPAL);
        debutField.setForeground(TEXT_COLOR);

        JLabel finLabel = new JLabel("Date Fin (YYYY-MM-DD):");
        finLabel.setForeground(TEXT_COLOR);
        JTextField finField = new JTextField(20);
        finField.setBackground(BG_PRINCIPAL);
        finField.setForeground(TEXT_COLOR);

        Calendar cal = Calendar.getInstance();
        if (abonnement == null) {
            debutField.setText(new Date(System.currentTimeMillis()).toString());
            cal.add(Calendar.YEAR, 1);
            finField.setText(new Date(cal.getTimeInMillis()).toString());
        } else {
            idField.setText(String.valueOf(abonnement.getIdAbonnement()));
            idField.setEditable(false);
            // Sélectionner l'étudiant
            for (int i = 0; i < etudiantCombo.getItemCount(); i++) {
                if (etudiantCombo.getItemAt(i).startsWith(abonnement.getMatricule())) {
                    etudiantCombo.setSelectedIndex(i);
                    break;
                }
            }
            // Sélectionner la ligne
            for (int i = 0; i < ligneCombo.getItemCount(); i++) {
                if (ligneCombo.getItemAt(i).startsWith(String.valueOf(abonnement.getIdLigne()))) {
                    ligneCombo.setSelectedIndex(i);
                    break;
                }
            }
            debutField.setText(abonnement.getDateDebut().toString());
            finField.setText(abonnement.getDateFin().toString());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(etudiantLabel, gbc);
        gbc.gridx = 1;
        panel.add(etudiantCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(ligneLabel, gbc);
        gbc.gridx = 1;
        panel.add(ligneCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(debutLabel, gbc);
        gbc.gridx = 1;
        panel.add(debutField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(finLabel, gbc);
        gbc.gridx = 1;
        panel.add(finField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty() || debutField.getText().isEmpty() || finField.getText().isEmpty()) {
                showWarning("Veuillez remplir tous les champs");
                return;
            }

            try {
                Abonnement newAbonnement = new Abonnement();
                newAbonnement.setIdAbonnement(Integer.parseInt(idField.getText()));
                String etudiantSelected = (String) etudiantCombo.getSelectedItem();
                newAbonnement.setMatricule(etudiantSelected.split(" - ")[0]);
                String ligneSelected = (String) ligneCombo.getSelectedItem();
                newAbonnement.setIdLigne(Integer.parseInt(ligneSelected.split(" - ")[0]));
                newAbonnement.setDateDebut(Date.valueOf(debutField.getText()));
                newAbonnement.setDateFin(Date.valueOf(finField.getText()));
                newAbonnement.setActif(true);

                if (abonnement == null) {
                    abonnementDAO.ajouterAbonnement(newAbonnement);
                    showSuccess("Abonnement ajouté avec succès");
                } else {
                    // Pour modification, on ajoute un nouvel abonnement actif et on désactive l'ancien
                    abonnementDAO.ajouterAbonnement(newAbonnement);
                    showSuccess("Abonnement modifié avec succès");
                }
                loadAbonnementData();
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