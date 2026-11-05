package org.example.views;

// ==========================================
// Fichier : views/StationPanel.java
// ==========================================


import org.example.dao.StationDAO;
import org.example.models.Station;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class StationPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable stationTable;
    private StationDAO stationDAO;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public StationPanel() {
        stationDAO = new StationDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadStationData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Stations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showStationDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = stationTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idStation = (int) stationTable.getValueAt(selectedRow, 0);
                try {
                    List<Station> stations = stationDAO.getAllStations();
                    Station station = stations.stream().filter(s -> s.getIdStation() == idStation).findFirst().orElse(null);
                    if (station != null) showStationDialog(station);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner une station à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = stationTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette station ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idStation = (int) stationTable.getValueAt(selectedRow, 0);
                    try {
                        stationDAO.supprimerStation(idStation);
                        loadStationData();
                        showSuccess("Station supprimée avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner une station à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadStationData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_PRINCIPAL);
        JLabel searchLabel = new JLabel("Rechercher par Ville:");
        searchLabel.setForeground(TEXT_COLOR);
        searchField = new JTextField(20);
        searchField.setBackground(BG_SECONDAIRE);
        searchField.setForeground(TEXT_COLOR);
        searchField.setCaretColor(ACCENT_COLOR);
        JButton searchButton = createButton("🔍 Rechercher", ACCENT_COLOR);

        searchButton.addActionListener(e -> searchStation());
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchStation();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        String[] columns = {"ID", "Nom Station", "Adresse", "Ville"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stationTable = new JTable(tableModel);
        stationTable.setBackground(BG_SECONDAIRE);
        stationTable.setForeground(TEXT_COLOR);
        stationTable.setRowHeight(30);
        stationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stationTable.getTableHeader().setBackground(ACCENT_COLOR);
        stationTable.getTableHeader().setForeground(Color.WHITE);
        stationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        stationTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(stationTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_PRINCIPAL);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadStationData() {
        try {
            List<Station> stations = stationDAO.getAllStations();
            updateTable(stations);
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void searchStation() {
        String ville = searchField.getText().trim();
        if (ville.isEmpty()) {
            loadStationData();
            return;
        }
        try {
            List<Station> stations = stationDAO.rechercherStationsParVille(ville);
            updateTable(stations);
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void updateTable(List<Station> stations) {
        tableModel.setRowCount(0);
        for (Station s : stations) {
            tableModel.addRow(new Object[]{
                    s.getIdStation(),
                    s.getNomStation(),
                    s.getAdresse(),
                    s.getVille()
            });
        }
    }

    private void showStationDialog(Station station) {
        JDialog dialog = new JDialog();
        dialog.setTitle(station == null ? "Ajouter une Station" : "Modifier une Station");
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

        JLabel nomLabel = new JLabel("Nom Station:");
        nomLabel.setForeground(TEXT_COLOR);
        JTextField nomField = new JTextField(20);
        nomField.setBackground(BG_PRINCIPAL);
        nomField.setForeground(TEXT_COLOR);

        JLabel adresseLabel = new JLabel("Adresse:");
        adresseLabel.setForeground(TEXT_COLOR);
        JTextField adresseField = new JTextField(20);
        adresseField.setBackground(BG_PRINCIPAL);
        adresseField.setForeground(TEXT_COLOR);

        JLabel villeLabel = new JLabel("Ville:");
        villeLabel.setForeground(TEXT_COLOR);
        JTextField villeField = new JTextField(20);
        villeField.setBackground(BG_PRINCIPAL);
        villeField.setForeground(TEXT_COLOR);

        if (station != null) {
            idField.setText(String.valueOf(station.getIdStation()));
            idField.setEditable(false);
            nomField.setText(station.getNomStation());
            adresseField.setText(station.getAdresse());
            villeField.setText(station.getVille());
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
        panel.add(adresseLabel, gbc);
        gbc.gridx = 1;
        panel.add(adresseField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(villeLabel, gbc);
        gbc.gridx = 1;
        panel.add(villeField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            if (idField.getText().isEmpty() || nomField.getText().isEmpty()) {
                showWarning("Veuillez remplir les champs obligatoires (ID et Nom)");
                return;
            }

            try {
                Station newStation = new Station();
                newStation.setIdStation(Integer.parseInt(idField.getText()));
                newStation.setNomStation(nomField.getText());
                newStation.setAdresse(adresseField.getText());
                newStation.setVille(villeField.getText());

                if (station == null) {
                    stationDAO.ajouterStation(newStation);
                    showSuccess("Station ajoutée avec succès");
                } else {
                    stationDAO.modifierStation(newStation);
                    showSuccess("Station modifiée avec succès");
                }
                loadStationData();
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