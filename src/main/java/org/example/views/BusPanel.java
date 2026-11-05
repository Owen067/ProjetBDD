package org.example.views;

// ==========================================
// Fichier : views/BusPanel.java
// ==========================================


import org.example.dao.BusDAO;
import org.example.models.Bus;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class BusPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable busTable;
    private BusDAO busDAO;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public BusPanel() {
        busDAO = new BusDAO();
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadBusData();
    }

    private void initUI() {
        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PRINCIPAL);

        JLabel titleLabel = new JLabel("Gestion des Bus");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_PRINCIPAL);

        JButton addButton = createButton("➕ Ajouter", SUCCESS_COLOR);
        JButton editButton = createButton("✏️ Modifier", new Color(255, 152, 0));
        JButton deleteButton = createButton("🗑️ Supprimer", new Color(244, 67, 54));
        JButton refreshButton = createButton("🔄 Rafraîchir", ACCENT_COLOR);

        addButton.addActionListener(e -> showBusDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = busTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idBus = (int) busTable.getValueAt(selectedRow, 0);
                try {
                    List<Bus> busList = busDAO.getAllBus();
                    Bus bus = busList.stream().filter(b -> b.getIdBus() == idBus).findFirst().orElse(null);
                    if (bus != null) showBusDialog(bus);
                } catch (SQLException ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showWarning("Veuillez sélectionner un bus à modifier");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = busTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce bus ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int idBus = (int) busTable.getValueAt(selectedRow, 0);
                    try {
                        busDAO.supprimerBus(idBus);
                        loadBusData();
                        showSuccess("Bus supprimé avec succès");
                    } catch (SQLException ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Veuillez sélectionner un bus à supprimer");
            }
        });

        refreshButton.addActionListener(e -> loadBusData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_PRINCIPAL);
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setForeground(TEXT_COLOR);
        searchField = new JTextField(20);
        searchField.setBackground(BG_SECONDAIRE);
        searchField.setForeground(TEXT_COLOR);
        searchField.setCaretColor(ACCENT_COLOR);
        JButton searchButton = createButton("🔍 Rechercher", ACCENT_COLOR);

        searchButton.addActionListener(e -> searchBus());
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchBus();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table
        String[] columns = {"ID", "Immatriculation", "Capacité", "Modèle", "Ligne Affectée"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        busTable = new JTable(tableModel);
        busTable.setBackground(BG_SECONDAIRE);
        busTable.setForeground(TEXT_COLOR);
        busTable.setRowHeight(30);
        busTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        busTable.getTableHeader().setBackground(ACCENT_COLOR);
        busTable.getTableHeader().setForeground(Color.WHITE);
        busTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        busTable.setSelectionBackground(ACCENT_COLOR);

        JScrollPane scrollPane = new JScrollPane(busTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        add(topPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadBusData() {
        try {
            List<Bus> busList = busDAO.getAllBus();
            tableModel.setRowCount(0);
            for (Bus bus : busList) {
                tableModel.addRow(new Object[]{
                        bus.getIdBus(),
                        bus.getImmatriculation(),
                        bus.getCapacite(),
                        bus.getModele(),
                        bus.getLigneAffectee() != null ? bus.getLigneAffectee() : "Non affecté"
                });
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void searchBus() {
        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            loadBusData();
            return;
        }
        try {
            List<Bus> busList = busDAO.rechercherBus(search);
            tableModel.setRowCount(0);
            for (Bus bus : busList) {
                tableModel.addRow(new Object[]{
                        bus.getIdBus(),
                        bus.getImmatriculation(),
                        bus.getCapacite(),
                        bus.getModele(),
                        bus.getLigneAffectee() != null ? bus.getLigneAffectee() : "Non affecté"
                });
            }
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void showBusDialog(Bus bus) {
        JDialog dialog = new JDialog();
        dialog.setTitle(bus == null ? "Ajouter un Bus" : "Modifier un Bus");
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_SECONDAIRE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("ID:");
        idLabel.setForeground(TEXT_COLOR);
        JTextField idField = new JTextField(15);
        idField.setBackground(BG_PRINCIPAL);
        idField.setForeground(TEXT_COLOR);
        idField.setCaretColor(ACCENT_COLOR);

        JLabel immatLabel = new JLabel("Immatriculation:");
        immatLabel.setForeground(TEXT_COLOR);
        JTextField immatField = new JTextField(15);
        immatField.setBackground(BG_PRINCIPAL);
        immatField.setForeground(TEXT_COLOR);

        JLabel capaciteLabel = new JLabel("Capacité:");
        capaciteLabel.setForeground(TEXT_COLOR);
        JTextField capaciteField = new JTextField(15);
        capaciteField.setBackground(BG_PRINCIPAL);
        capaciteField.setForeground(TEXT_COLOR);

        JLabel modeleLabel = new JLabel("Modèle:");
        modeleLabel.setForeground(TEXT_COLOR);
        JTextField modeleField = new JTextField(15);
        modeleField.setBackground(BG_PRINCIPAL);
        modeleField.setForeground(TEXT_COLOR);

        if (bus != null) {
            idField.setText(String.valueOf(bus.getIdBus()));
            idField.setEditable(false);
            immatField.setText(bus.getImmatriculation());
            capaciteField.setText(String.valueOf(bus.getCapacite()));
            modeleField.setText(bus.getModele());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(immatLabel, gbc);
        gbc.gridx = 1;
        panel.add(immatField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(capaciteLabel, gbc);
        gbc.gridx = 1;
        panel.add(capaciteField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(modeleLabel, gbc);
        gbc.gridx = 1;
        panel.add(modeleField, gbc);

        JButton saveButton = createButton("💾 Enregistrer", SUCCESS_COLOR);
        JButton cancelButton = createButton("❌ Annuler", new Color(244, 67, 54));

        saveButton.addActionListener(e -> {
            try {
                Bus newBus = new Bus();
                newBus.setIdBus(Integer.parseInt(idField.getText()));
                newBus.setImmatriculation(immatField.getText());
                newBus.setCapacite(Integer.parseInt(capaciteField.getText()));
                newBus.setModele(modeleField.getText());

                if (bus == null) {
                    busDAO.ajouterBus(newBus);
                    showSuccess("Bus ajouté avec succès");
                } else {
                    busDAO.modifierBus(newBus);
                    showSuccess("Bus modifié avec succès");
                }
                loadBusData();
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