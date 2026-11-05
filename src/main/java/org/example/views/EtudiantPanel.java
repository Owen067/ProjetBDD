package org.example.views;

// ==========================================
// Fichier : views/EtudiantPanel.java
// ==========================================


import org.example.dao.EtudiantDAO;
import org.example.models.Etudiant;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class EtudiantPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable etudiantTable;
    private EtudiantDAO etudiantDAO;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public EtudiantPanel() {
        etudiantDAO = new EtudiantDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadEtudiantData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showEtudiantDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = etudiantTable.getSelectedRow();
            if (selectedRow >= 0) {
                String matricule = (String) etudiantTable.getValueAt(selectedRow, 0);
                try {
                    List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
                    Etudiant etudiant = etudiants.stream().filter(et -> et.getMatricule().equals(matricule)).findFirst().orElse(null);
                    if (etudiant != null) showEtudiantDialog(etudiant);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner un étudiant à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = etudiantTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet étudiant ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String matricule = (String) etudiantTable.getValueAt(selectedRow, 0);
                    try {
                        etudiantDAO.supprimerEtudiant(matricule);
                        loadEtudiantData();
                        showSuccess("Étudiant supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un étudiant à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadEtudiantData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_PRINCIPAL);
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setForeground(TEXT_COLOR);
        searchField = new JTextField(20);
        searchField.setBackground(BG_SECONDAIRE);
        searchField.setForeground(TEXT_COLOR);
        searchField.setCaretColor(ACCENT_COLOR);
        JButton searchButton = createButton("🔍 Rechercher", ACCENT_COLOR);

        searchButton.addActionListener(e -> searchEtudiant());
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchEtudiant();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        String[] columns = {"Matricule", "Nom", "Prénom", "Email", "Téléphone", "Ligne Abonnée"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        etudiantTable = new JTable(tableModel);
        etudiantTable.setBackground(BG_SECONDAIRE);
        etudiantTable.setForeground(TEXT_COLOR);
        etudiantTable.setRowHeight(30);
        etudiantTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etudiantTable.getTableHeader().setBackground(ACCENT_COLOR);
        etudiantTable.getTableHeader().setForeground(Color.WHITE);
        etudiantTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        etudiantTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(etudiantTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        add(topPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadEtudiantData() {
        try {
            List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
            tableModel.setRowCount(0);
            for (Etudiant e : etudiants) {
                tableModel.addRow(new Object[]{
                        e.getMatricule(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getEmail(),
                        e.getTelephone(),
                        e.getLigneAbonnee() != null ? e.getLigneAbonnee() : "Non abonné"
                });
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void searchEtudiant() {
        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            loadEtudiantData();
            return;
        }
        try {
            List<Etudiant> etudiants = etudiantDAO.rechercherEtudiants(search);
            tableModel.setRowCount(0);
            for (Etudiant e : etudiants) {
                tableModel.addRow(new Object[]{
                        e.getMatricule(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getEmail(),
                        e.getTelephone(),
                        e.getLigneAbonnee() != null ? e.getLigneAbonnee() : "Non abonné"
                });
            }
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void showEtudiantDialog(Etudiant etudiant) {
        JDialog dialog = new JDialog();
        dialog.setTitle(etudiant == null ? "Ajouter un Étudiant" : "Modifier un Étudiant");
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_SECONDAIRE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel matriculeLabel = new JLabel("Matricule:");
        matriculeLabel.setForeground(TEXT_COLOR);
        JTextField matriculeField = new JTextField(20);
        matriculeField.setBackground(BG_PRINCIPAL);
        matriculeField.setForeground(TEXT_COLOR);
        matriculeField.setCaretColor(ACCENT_COLOR);

        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setForeground(TEXT_COLOR);
        JTextField nomField = new JTextField(20);
        nomField.setBackground(BG_PRINCIPAL);
        nomField.setForeground(TEXT_COLOR);

        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setForeground(TEXT_COLOR);
        JTextField prenomField = new JTextField(20);
        prenomField.setBackground(BG_PRINCIPAL);
        prenomField.setForeground(TEXT_COLOR);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(TEXT_COLOR);
        JTextField emailField = new JTextField(20);
        emailField.setBackground(BG_PRINCIPAL);
        emailField.setForeground(TEXT_COLOR);

        JLabel telephoneLabel = new JLabel("Téléphone:");
        telephoneLabel.setForeground(TEXT_COLOR);
        JTextField telephoneField = new JTextField(20);
        telephoneField.setBackground(BG_PRINCIPAL);
        telephoneField.setForeground(TEXT_COLOR);

        if (etudiant != null) {
            matriculeField.setText(etudiant.getMatricule());
            matriculeField.setEditable(false);
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            emailField.setText(etudiant.getEmail());
            telephoneField.setText(etudiant.getTelephone());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(matriculeLabel, gbc);
        gbc.gridx = 1;
        panel.add(matriculeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nomLabel, gbc);
        gbc.gridx = 1;
        panel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(prenomLabel, gbc);
        gbc.gridx = 1;
        panel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(telephoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(telephoneField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (matriculeField.getText().isEmpty() || nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
                showWarning("Veuillez remplir tous les champs obligatoires");
                return;
            }

            try {
                Etudiant newEtudiant = new Etudiant();
                newEtudiant.setMatricule(matriculeField.getText());
                newEtudiant.setNom(nomField.getText());
                newEtudiant.setPrenom(prenomField.getText());
                newEtudiant.setEmail(emailField.getText());
                newEtudiant.setTelephone(telephoneField.getText());

                if (etudiant == null) {
                    etudiantDAO.ajouterEtudiant(newEtudiant);
                    showSuccess("Étudiant ajouté avec succès");
                } else {
                    etudiantDAO.modifierEtudiant(newEtudiant);
                    showSuccess("Étudiant modifié avec succès");
                }
                loadEtudiantData();
                dialog.dispose();
            } catch (SQLException ex) {
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