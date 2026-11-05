package org.example.views;

// ==========================================
// Fichier : views/TrajetPanel.java
// ==========================================


import org.example.dao.TrajetDAO;
import org.example.dao.BusDAO;
import org.example.dao.LigneDAO;
import org.example.dao.HoraireDAO;
import org.example.models.Trajet;
import org.example.models.Bus;
import org.example.models.Ligne;
import org.example.models.Horaire;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class TrajetPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color WARNING_COLOR = new Color(255, 152, 0);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable trajetTable;
    private TrajetDAO trajetDAO;
    private BusDAO busDAO;
    private LigneDAO ligneDAO;
    private HoraireDAO horaireDAO;
    private DefaultTableModel tableModel;
    private JComboBox<String> ligneFilter;
    private JTextField dateFilter;

    public TrajetPanel() {
        trajetDAO = new TrajetDAO();
        busDAO = new BusDAO();
        ligneDAO = new LigneDAO();
        horaireDAO = new HoraireDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadTrajetData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Trajets");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showTrajetDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idTrajet = (int) trajetTable.getValueAt(selectedRow, 0);
                try {
                    List<Trajet> trajets = trajetDAO.getAllTrajets();
                    Trajet trajet = trajets.stream().filter(t -> t.getIdTrajet() == idTrajet).findFirst().orElse(null);
                    if (trajet != null) showTrajetDialog(trajet);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner un trajet à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce trajet ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idTrajet = (int) trajetTable.getValueAt(selectedRow, 0);
                    try {
                        trajetDAO.supprimerTrajet(idTrajet);
                        loadTrajetData();
                        showSuccess("Trajet supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un trajet à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadTrajetData());

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

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setForeground(TEXT_COLOR);
        dateFilter = new JTextField(12);
        dateFilter.setBackground(BG_SECONDAIRE);
        dateFilter.setForeground(TEXT_COLOR);
        dateFilter.setCaretColor(ACCENT_COLOR);

        JButton filterButton = createButton("🔍 Filtrer", ACCENT_COLOR);
        filterButton.addActionListener(e -> filterTrajets());

        filterPanel.add(ligneLabel);
        filterPanel.add(ligneFilter);
        filterPanel.add(dateLabel);
        filterPanel.add(dateFilter);
        filterPanel.add(filterButton);

        String[] columns = {"ID", "Date", "Ligne", "Bus", "Heure Réelle", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trajetTable = new JTable(tableModel);
        trajetTable.setBackground(BG_SECONDAIRE);
        trajetTable.setForeground(TEXT_COLOR);
        trajetTable.setRowHeight(30);
        trajetTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trajetTable.getTableHeader().setBackground(ACCENT_COLOR);
        trajetTable.getTableHeader().setForeground(Color.WHITE);
        trajetTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        trajetTable.setSelectionBackground(ACCENT_COLOR);

        // Custom renderer for status
        trajetTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5 && value != null) {
                    String status = value.toString();
                    if (status.equals("a_lheure")) {
                        c.setForeground(SUCCESS_COLOR);
                        setText("✅ À l'heure");
                    } else if (status.equals("retard")) {
                        c.setForeground(WARNING_COLOR);
                        setText("⚠️ Retard");
                    } else {
                        c.setForeground(new Color(244, 67, 54));
                        setText("❌ Annulé");
                    }
                } else {
                    c.setForeground(TEXT_COLOR);
                }
                c.setBackground(isSelected ? ACCENT_COLOR : BG_SECONDAIRE);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(trajetTable);
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

    private void loadTrajetData() {
        try {
            List<Trajet> trajets = trajetDAO.getAllTrajets();
            updateTable(trajets);
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void filterTrajets() {
        String selectedLigne = (String) ligneFilter.getSelectedItem();
        String date = dateFilter.getText().trim();

        try {
            List<Trajet> trajets = trajetDAO.getAllTrajets();

            if (!selectedLigne.equals("Toutes")) {
                trajets = trajets.stream()
                        .filter(t -> t.getLigneNom().equals(selectedLigne))
                        .collect(java.util.stream.Collectors.toList());
            }

            if (!date.isEmpty()) {
                Date filterDate = Date.valueOf(date);
                trajets = trajets.stream()
                        .filter(t -> t.getDate().equals(filterDate))
                        .collect(java.util.stream.Collectors.toList());
            }

            updateTable(trajets);
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void updateTable(List<Trajet> trajets) {
        tableModel.setRowCount(0);
        for (Trajet t : trajets) {
            tableModel.addRow(new Object[]{
                    t.getIdTrajet(),
                    t.getDate(),
                    t.getLigneNom(),
                    t.getBusImmatriculation(),
                    t.getHeureReelle(),
                    t.getStatut()
            });
        }
    }

    private void showTrajetDialog(Trajet trajet) {
        JDialog dialog = new JDialog();
        dialog.setTitle(trajet == null ? "Ajouter un Trajet" : "Modifier un Trajet");
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_SECONDAIRE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setForeground(TEXT_COLOR);
        JTextField dateField = new JTextField(20);
        dateField.setBackground(BG_PRINCIPAL);
        dateField.setForeground(TEXT_COLOR);
        dateField.setText(new Date(System.currentTimeMillis()).toString());

        JLabel busLabel = new JLabel("Bus:");
        busLabel.setForeground(TEXT_COLOR);
        JComboBox<String> busCombo = new JComboBox<>();
        try {
            List<Bus> busList = busDAO.getAllBus();
            for (Bus b : busList) {
                busCombo.addItem(b.getIdBus() + " - " + b.getImmatriculation());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        busCombo.setBackground(BG_SECONDAIRE);
        busCombo.setForeground(TEXT_COLOR);

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

        // Mettre à jour les horaires quand la ligne change
        JLabel horaireLabel = new JLabel("Horaire:");
        horaireLabel.setForeground(TEXT_COLOR);
        JComboBox<String> horaireCombo = new JComboBox<>();
        horaireCombo.setBackground(BG_SECONDAIRE);
        horaireCombo.setForeground(TEXT_COLOR);

        ligneCombo.addActionListener(e -> updateHorairesCombo(ligneCombo, horaireCombo));

        JLabel heureLabel = new JLabel("Heure Réelle (HH:MM:SS):");
        heureLabel.setForeground(TEXT_COLOR);
        JTextField heureField = new JTextField(20);
        heureField.setBackground(BG_PRINCIPAL);
        heureField.setForeground(TEXT_COLOR);
        heureField.setText(Time.valueOf("08:00:00").toString());

        JLabel statutLabel = new JLabel("Statut:");
        statutLabel.setForeground(TEXT_COLOR);
        String[] statuts = {"a_lheure", "retard", "annule"};
        JComboBox<String> statutCombo = new JComboBox<>(statuts);
        statutCombo.setBackground(BG_SECONDAIRE);
        statutCombo.setForeground(TEXT_COLOR);

        if (trajet != null) {
            dateField.setText(trajet.getDate().toString());
            heureField.setText(trajet.getHeureReelle().toString());
            statutCombo.setSelectedItem(trajet.getStatut());

            // Sélectionner le bus
            for (int i = 0; i < busCombo.getItemCount(); i++) {
                if (busCombo.getItemAt(i).startsWith(String.valueOf(trajet.getIdBus()))) {
                    busCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Sélectionner la ligne
            for (int i = 0; i < ligneCombo.getItemCount(); i++) {
                if (ligneCombo.getItemAt(i).startsWith(String.valueOf(trajet.getIdLigne()))) {
                    ligneCombo.setSelectedIndex(i);
                    break;
                }
            }

            updateHorairesCombo(ligneCombo, horaireCombo);

            // Sélectionner l'horaire
            for (int i = 0; i < horaireCombo.getItemCount(); i++) {
                if (horaireCombo.getItemAt(i).startsWith(String.valueOf(trajet.getIdHoraire()))) {
                    horaireCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(busLabel, gbc);
        gbc.gridx = 1;
        panel.add(busCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(ligneLabel, gbc);
        gbc.gridx = 1;
        panel.add(ligneCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(horaireLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaireCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(heureLabel, gbc);
        gbc.gridx = 1;
        panel.add(heureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(statutLabel, gbc);
        gbc.gridx = 1;
        panel.add(statutCombo, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (dateField.getText().isEmpty() || heureField.getText().isEmpty()) {
                showWarning("Veuillez remplir tous les champs");
                return;
            }

            try {
                Trajet newTrajet = new Trajet();
                newTrajet.setDate(Date.valueOf(dateField.getText()));
                newTrajet.setHeureReelle(Time.valueOf(heureField.getText()));
                newTrajet.setStatut((String) statutCombo.getSelectedItem());

                String busSelected = (String) busCombo.getSelectedItem();
                newTrajet.setIdBus(Integer.parseInt(busSelected.split(" - ")[0]));

                String ligneSelected = (String) ligneCombo.getSelectedItem();
                newTrajet.setIdLigne(Integer.parseInt(ligneSelected.split(" - ")[0]));

                String horaireSelected = (String) horaireCombo.getSelectedItem();
                newTrajet.setIdHoraire(Integer.parseInt(horaireSelected.split(" - ")[0]));

                if (trajet == null) {
                    trajetDAO.ajouterTrajet(newTrajet);
                    showSuccess("Trajet ajouté avec succès");
                } else {
                    newTrajet.setIdTrajet(trajet.getIdTrajet());
                    trajetDAO.modifierTrajet(newTrajet);
                    showSuccess("Trajet modifié avec succès");
                }
                loadTrajetData();
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

    private void updateHorairesCombo(JComboBox<String> ligneCombo, JComboBox<String> horaireCombo) {
        horaireCombo.removeAllItems();
        if (ligneCombo.getSelectedItem() != null) {
            String selected = (String) ligneCombo.getSelectedItem();
            int idLigne = Integer.parseInt(selected.split(" - ")[0]);
            try {
                List<Horaire> horaires = horaireDAO.filtrerParLigne(idLigne);
                for (Horaire h : horaires) {
                    horaireCombo.addItem(h.getIdHoraire() + " - " + h.getJour() + " " + h.getHeureDepart());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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