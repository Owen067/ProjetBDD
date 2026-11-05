package org.example.views;

// ==========================================
// Fichier : views/DashboardPanel.java
// ==========================================

// ==========================================
// Fichier : views/DashboardPanel.java
// ==========================================

import javax.swing.border.TitledBorder;
import org.example.dao.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import org.example.models.Trajet;
import org.example.models.Incident;

public class DashboardPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color WARNING_COLOR = new Color(255, 152, 0);
    private final Color DANGER_COLOR = new Color(244, 67, 54);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JLabel totalEtudiantsLabel;
    private JLabel totalBusLabel;
    private JLabel totalLignesLabel;
    private JLabel incidentsTodayLabel;
    private JTable trajetsTable;
    private JTable incidentsTable;

    private EtudiantDAO etudiantDAO;
    private BusDAO busDAO;
    private LigneDAO ligneDAO;
    private IncidentDAO incidentDAO;
    private TrajetDAO trajetDAO;

    public DashboardPanel() {
        etudiantDAO = new EtudiantDAO();
        busDAO = new BusDAO();
        ligneDAO = new LigneDAO();
        incidentDAO = new IncidentDAO();
        trajetDAO = new TrajetDAO();

        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        loadData();

        // Rafraîchir toutes les 30 secondes
        new Timer(30000, e -> loadData()).start();
    }

    private void initUI() {
        // Top stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BG_PRINCIPAL);
        statsPanel.setOpaque(true);

        statsPanel.add(createStatCard("👨‍🎓 Total Étudiants", "0", SUCCESS_COLOR));
        statsPanel.add(createStatCard("🚍 Total Bus", "0", ACCENT_COLOR));
        statsPanel.add(createStatCard("🗺️ Total Lignes", "0", WARNING_COLOR));
        statsPanel.add(createStatCard("⚠️ Incidents Aujourd'hui", "0", DANGER_COLOR));

        add(statsPanel, BorderLayout.NORTH);

        // Bottom panels
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(BG_PRINCIPAL);

        // Trajets récents
        JPanel trajetsPanel = createTablePanel("🔄 Trajets Récents");
        trajetsTable = new JTable();
        trajetsTable.setBackground(BG_SECONDAIRE);
        trajetsTable.setForeground(TEXT_COLOR);
        trajetsTable.setRowHeight(30);
        trajetsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trajetsTable.getTableHeader().setBackground(ACCENT_COLOR);
        trajetsTable.getTableHeader().setForeground(Color.WHITE);
        trajetsPanel.add(new JScrollPane(trajetsTable), BorderLayout.CENTER);

        // Incidents récents
        JPanel incidentsPanel = createTablePanel("⚠️ Derniers Incidents");
        incidentsTable = new JTable();
        incidentsTable.setBackground(BG_SECONDAIRE);
        incidentsTable.setForeground(TEXT_COLOR);
        incidentsTable.setRowHeight(30);
        incidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        incidentsTable.getTableHeader().setBackground(ACCENT_COLOR);
        incidentsTable.getTableHeader().setForeground(Color.WHITE);
        incidentsPanel.add(new JScrollPane(incidentsTable), BorderLayout.CENTER);

        bottomPanel.add(trajetsPanel);
        bottomPanel.add(incidentsPanel);

        add(bottomPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(BG_SECONDAIRE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // Stocker la référence pour mise à jour
        if (title.contains("Étudiants")) totalEtudiantsLabel = valueLabel;
        else if (title.contains("Bus")) totalBusLabel = valueLabel;
        else if (title.contains("Lignes")) totalLignesLabel = valueLabel;
        else if (title.contains("Incidents")) incidentsTodayLabel = valueLabel;

        return card;
    }

    private JPanel createTablePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SECONDAIRE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                ACCENT_COLOR
        ));
        return panel;
    }

    private void loadData() {
        try {
            totalEtudiantsLabel.setText(String.valueOf(etudiantDAO.getTotalEtudiants()));
            totalBusLabel.setText(String.valueOf(busDAO.getTotalBus()));
            totalLignesLabel.setText(String.valueOf(ligneDAO.getTotalLignes()));
            incidentsTodayLabel.setText(String.valueOf(incidentDAO.getIncidentsAujourdhui()));

            loadTrajetsRecents();
            loadIncidentsRecents();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void loadTrajetsRecents() throws SQLException {
        List<Trajet> trajets = trajetDAO.getTrajetsRecents(5);
        String[] columns = {"ID", "Date", "Ligne", "Bus", "Statut"};
        Object[][] data = new Object[trajets.size()][5];

        for (int i = 0; i < trajets.size(); i++) {
            Trajet t = trajets.get(i);
            data[i][0] = t.getIdTrajet();
            data[i][1] = t.getDate();
            data[i][2] = t.getLigneNom();
            data[i][3] = t.getBusImmatriculation();
            data[i][4] = t.getStatut();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trajetsTable.setModel(model);

        // Colorer les statuts
        trajetsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) {
                    String statut = value.toString();
                    if (statut.equals("a_lheure")) {
                        c.setForeground(SUCCESS_COLOR);
                        setText("✅ À l'heure");
                    } else if (statut.equals("retard")) {
                        c.setForeground(WARNING_COLOR);
                        setText("⚠️ Retard");
                    } else {
                        c.setForeground(DANGER_COLOR);
                        setText("❌ Annulé");
                    }
                } else {
                    c.setForeground(TEXT_COLOR);
                }
                c.setBackground(isSelected ? ACCENT_COLOR : BG_SECONDAIRE);
                return c;
            }
        });
    }

    private void loadIncidentsRecents() throws SQLException {
        List<Incident> incidents = incidentDAO.getIncidentsRecents(5);
        String[] columns = {"ID", "Type", "Description", "Date/Heure", "Bus"};
        Object[][] data = new Object[incidents.size()][5];

        for (int i = 0; i < incidents.size(); i++) {
            Incident inc = incidents.get(i);
            data[i][0] = inc.getIdIncident();
            data[i][1] = inc.getType();
            data[i][2] = inc.getDescription();
            data[i][3] = inc.getDateHeure();
            data[i][4] = inc.getBusInfo();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incidentsTable.setModel(model);
        incidentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(TEXT_COLOR);
                c.setBackground(isSelected ? ACCENT_COLOR : BG_SECONDAIRE);
                return c;
            }
        });
    }
}