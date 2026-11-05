package org.example.views;

// ==========================================
// Fichier : views/IncidentPanel.java
// ==========================================


import org.example.dao.IncidentDAO;
import org.example.dao.TrajetDAO;
import org.example.models.Incident;
import org.example.models.Trajet;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class IncidentPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable incidentTable;
    private IncidentDAO incidentDAO;
    private TrajetDAO trajetDAO;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeFilter;

    public IncidentPanel() {
        incidentDAO = new IncidentDAO();
        trajetDAO = new TrajetDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadIncidentData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Incidents");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showIncidentDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = incidentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idIncident = (int) incidentTable.getValueAt(selectedRow, 0);
                try {
                    List<Incident> incidents = incidentDAO.getAllIncidents();
                    Incident incident = incidents.stream().filter(inc -> inc.getIdIncident() == idIncident).findFirst().orElse(null);
                    if (incident != null) showIncidentDialog(incident);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner un incident à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = incidentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet incident ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idIncident = (int) incidentTable.getValueAt(selectedRow, 0);
                    try {
                        incidentDAO.supprimerIncident(idIncident);
                        loadIncidentData();
                        showSuccess("Incident supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un incident à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadIncidentData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BG_PRINCIPAL);

        JLabel typeLabel = new JLabel("Filtrer par Type:");
        typeLabel.setForeground(TEXT_COLOR);
        String[] types = {"Tous", "Embouteillage", "Technique", "Météo", "Accident", "Travaux"};
        typeFilter = new JComboBox<>(types);
        typeFilter.setBackground(BG_SECONDAIRE);
        typeFilter.setForeground(TEXT_COLOR);

        JButton filterButton = createButton("🔍 Filtrer", ACCENT_COLOR);
        filterButton.addActionListener(e -> filterIncidents());

        filterPanel.add(typeLabel);
        filterPanel.add(typeFilter);
        filterPanel.add(filterButton);

        String[] columns = {"ID", "Type", "Description", "Date/Heure", "Trajet", "Bus", "Ligne"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        incidentTable = new JTable(tableModel);
        incidentTable.setBackground(BG_SECONDAIRE);
        incidentTable.setForeground(TEXT_COLOR);
        incidentTable.setRowHeight(30);
        incidentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        incidentTable.getTableHeader().setBackground(ACCENT_COLOR);
        incidentTable.getTableHeader().setForeground(Color.WHITE);
        incidentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        incidentTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(incidentTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_PRINCIPAL);
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadIncidentData() {
        try {
            List<Incident> incidents = incidentDAO.getAllIncidents();
            updateTable(incidents);
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void filterIncidents() {
        String selectedType = (String) typeFilter.getSelectedItem();
        try {
            List<Incident> incidents = incidentDAO.getAllIncidents();
            if (!selectedType.equals("Tous")) {
                incidents = incidents.stream()
                        .filter(i -> i.getType().equals(selectedType))
                        .collect(java.util.stream.Collectors.toList());
            }
            updateTable(incidents);
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void updateTable(List<Incident> incidents) {
        tableModel.setRowCount(0);
        for (Incident i : incidents) {
            String typeIcon = getTypeIcon(i.getType());
            tableModel.addRow(new Object[]{
                    i.getIdIncident(),
                    typeIcon + " " + i.getType(),
                    i.getDescription(),
                    i.getDateHeure(),
                    i.getTrajetInfo(),
                    i.getBusInfo(),
                    i.getLigneInfo()
            });
        }
    }

    private String getTypeIcon(String type) {
        switch(type) {
            case "Embouteillage": return "🚗";
            case "Technique": return "🔧";
            case "Météo": return "🌧️";
            case "Accident": return "⚠️";
            case "Travaux": return "🚧";
            default: return "📌";
        }
    }

    private void showIncidentDialog(Incident incident) {
        JDialog dialog = new JDialog();
        dialog.setTitle(incident == null ? "Ajouter un Incident" : "Modifier un Incident");
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_SECONDAIRE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Type d'Incident:");
        typeLabel.setForeground(TEXT_COLOR);
        String[] types = {"Embouteillage", "Technique", "Météo", "Accident", "Travaux"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setBackground(BG_SECONDAIRE);
        typeCombo.setForeground(TEXT_COLOR);

        JLabel trajetLabel = new JLabel("Trajet:");
        trajetLabel.setForeground(TEXT_COLOR);
        JComboBox<String> trajetCombo = new JComboBox<>();
        try {
            List<Trajet> trajets = trajetDAO.getAllTrajets();
            for (Trajet t : trajets) {
                trajetCombo.addItem(t.getIdTrajet() + " - " + t.getDate() + " - " + t.getLigneNom());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        trajetCombo.setBackground(BG_SECONDAIRE);
        trajetCombo.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(TEXT_COLOR);
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setBackground(BG_PRINCIPAL);
        descArea.setForeground(TEXT_COLOR);
        descArea.setCaretColor(ACCENT_COLOR);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        if (incident != null) {
            typeCombo.setSelectedItem(incident.getType());
            descArea.setText(incident.getDescription());
            // Sélectionner le trajet dans le combo
            for (int i = 0; i < trajetCombo.getItemCount(); i++) {
                if (trajetCombo.getItemAt(i).startsWith(String.valueOf(incident.getIdTrajet()))) {
                    trajetCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(trajetLabel, gbc);
        gbc.gridx = 1;
        panel.add(trajetCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(descLabel, gbc);
        gbc.gridx = 1;
        panel.add(descScroll, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (descArea.getText().isEmpty()) {
                showWarning("Veuillez entrer une description");
                return;
            }

            try {
                Incident newIncident = new Incident();
                newIncident.setType((String) typeCombo.getSelectedItem());
                newIncident.setDescription(descArea.getText());
                newIncident.setDateHeure(new Timestamp(System.currentTimeMillis()));

                String trajetSelected = (String) trajetCombo.getSelectedItem();
                int idTrajet = Integer.parseInt(trajetSelected.split(" - ")[0]);
                newIncident.setIdTrajet(idTrajet);

                if (incident == null) {
                    incidentDAO.ajouterIncident(newIncident);
                    showSuccess("Incident ajouté avec succès");
                } else {
                    newIncident.setIdIncident(incident.getIdIncident());
                    incidentDAO.modifierIncident(newIncident);
                    showSuccess("Incident modifié avec succès");
                }
                loadIncidentData();
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