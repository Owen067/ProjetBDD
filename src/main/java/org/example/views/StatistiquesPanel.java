package org.example.views;
// ==========================================
// Fichier : views/StatistiquesPanel.java (VERSION CORRIGÉE ET STABLE)
// ==========================================


import org.example.config.models.DatabaseConnection;
import org.example.dao.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class StatistiquesPanel extends JPanel {
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    private JTable resultTable;
    private JPanel queryPanel;
    private CardLayout cardLayout;

    public StatistiquesPanel() {
        setBackground(BG_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tester la connexion au démarrage
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Impossible de se connecter à la base de données MySQL.\n" +
                            "Vérifiez que :\n" +
                            "1. MySQL est démarré (sudo systemctl start mysql)\n" +
                            "2. Le mot de passe dans DatabaseConnection.java est correct\n" +
                            "3. La base de données 'ProjetBDD' existe",
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        }

        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("📊 Statistiques et Rapports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Panel des boutons de requêtes
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buttonPanel.setBackground(BG_PRINCIPAL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btn1 = createQueryButton("1️⃣ Nombre d'étudiants par ligne", new Color(76, 175, 80));
        JButton btn2 = createQueryButton("2️⃣ Taux de remplissage des bus", new Color(33, 150, 243));
        JButton btn3 = createQueryButton("3️⃣ Horaires d'une ligne donnée", new Color(255, 152, 0));
        JButton btn4 = createQueryButton("4️⃣ Étudiants sans abonnement", new Color(156, 39, 176));
        JButton btn5 = createQueryButton("5️⃣ Historique d'un étudiant", new Color(233, 69, 96));
        JButton btn6 = createQueryButton("6️⃣ Bus affectés à une ligne à une date", new Color(0, 188, 212));
        JButton btn7 = createQueryButton("7️⃣ Lignes les plus chargées", new Color(255, 193, 7));
        JButton btn8 = createQueryButton("8️⃣ Liste des trajets avec retard", new Color(244, 67, 54));

        btn1.addActionListener(e -> executeQuery1());
        btn2.addActionListener(e -> executeQuery2());
        btn3.addActionListener(e -> executeQuery3());
        btn4.addActionListener(e -> executeQuery4());
        btn5.addActionListener(e -> executeQuery5());
        btn6.addActionListener(e -> executeQuery6());
        btn7.addActionListener(e -> executeQuery7());
        btn8.addActionListener(e -> executeQuery8());

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);
        buttonPanel.add(btn5);
        buttonPanel.add(btn6);
        buttonPanel.add(btn7);
        buttonPanel.add(btn8);

        // Panel pour les paramètres supplémentaires
        cardLayout = new CardLayout();
        queryPanel = new JPanel(cardLayout);
        queryPanel.setBackground(BG_SECONDAIRE);
        queryPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Panel par défaut (vide)
        JPanel defaultPanel = new JPanel();
        defaultPanel.setBackground(BG_SECONDAIRE);
        queryPanel.add(defaultPanel, "default");

        // Panel pour requête 3 (sélection ligne)
        JPanel panel3 = new JPanel(new FlowLayout());
        panel3.setBackground(BG_SECONDAIRE);
        JLabel label3 = new JLabel("Sélectionner une ligne:");
        label3.setForeground(TEXT_COLOR);
        JComboBox<String> ligneCombo3 = new JComboBox<>();
        loadLignesToCombo(ligneCombo3);
        JButton exec3 = createSmallButton("Afficher");
        exec3.addActionListener(e -> executeQuery3WithLigne(ligneCombo3));
        panel3.add(label3);
        panel3.add(ligneCombo3);
        panel3.add(exec3);
        queryPanel.add(panel3, "query3");

        // Panel pour requête 5 (matricule étudiant)
        JPanel panel5 = new JPanel(new FlowLayout());
        panel5.setBackground(BG_SECONDAIRE);
        JLabel label5 = new JLabel("Matricule étudiant:");
        label5.setForeground(TEXT_COLOR);
        JTextField matriculeField = new JTextField(15);
        matriculeField.setBackground(BG_PRINCIPAL);
        matriculeField.setForeground(TEXT_COLOR);
        matriculeField.setCaretColor(ACCENT_COLOR);
        JButton exec5 = createSmallButton("Afficher");
        exec5.addActionListener(e -> executeQuery5WithMatricule(matriculeField));
        panel5.add(label5);
        panel5.add(matriculeField);
        panel5.add(exec5);
        queryPanel.add(panel5, "query5");

        // Panel pour requête 6 (ligne et date)
        JPanel panel6 = new JPanel(new FlowLayout());
        panel6.setBackground(BG_SECONDAIRE);
        JLabel label6Ligne = new JLabel("Ligne:");
        label6Ligne.setForeground(TEXT_COLOR);
        JComboBox<String> ligneCombo6 = new JComboBox<>();
        loadLignesToCombo(ligneCombo6);
        JLabel label6Date = new JLabel("Date (YYYY-MM-DD):");
        label6Date.setForeground(TEXT_COLOR);
        JTextField dateField6 = new JTextField(12);
        dateField6.setBackground(BG_PRINCIPAL);
        dateField6.setForeground(TEXT_COLOR);
        dateField6.setText(java.time.LocalDate.now().toString());
        JButton exec6 = createSmallButton("Afficher");
        exec6.addActionListener(e -> executeQuery6WithParams(ligneCombo6, dateField6));
        panel6.add(label6Ligne);
        panel6.add(ligneCombo6);
        panel6.add(label6Date);
        panel6.add(dateField6);
        panel6.add(exec6);
        queryPanel.add(panel6, "query6");

        // Table des résultats
        resultTable = new JTable();
        resultTable.setBackground(BG_SECONDAIRE);
        resultTable.setForeground(TEXT_COLOR);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.getTableHeader().setBackground(ACCENT_COLOR);
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBackground(BG_PRINCIPAL);
        scrollPane.getViewport().setBackground(BG_SECONDAIRE);

        add(buttonPanel, BorderLayout.CENTER);
        add(queryPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.SOUTH);

        cardLayout.show(queryPanel, "default");
    }

    private JButton createQueryButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void loadLignesToCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        String sql = "SELECT id_ligne, nom_ligne FROM LIGNE ORDER BY nom_ligne";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                combo.addItem(rs.getInt("id_ligne") + " - " + rs.getString("nom_ligne"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des lignes: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeQuery(String sql, Object[] params, String[] columnNames) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }

                    ResultSet rs = pstmt.executeQuery();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    Vector<String> columns = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(columnNames[i - 1]);
                    }

                    Vector<Vector<Object>> data = new Vector<>();
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            row.add(value != null ? value : "-");
                        }
                        data.add(row);
                    }

                    if (data.isEmpty()) {
                        Vector<Object> emptyRow = new Vector<>();
                        for (int i = 0; i < columnCount; i++) {
                            emptyRow.add("📭 Aucune donnée trouvée");
                        }
                        data.add(emptyRow);
                    }

                    final DefaultTableModel model = new DefaultTableModel(data, columns) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };

                    SwingUtilities.invokeLater(() -> {
                        resultTable.setModel(model);
                        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(JTable table, Object value,
                                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                c.setForeground(TEXT_COLOR);
                                c.setBackground(isSelected ? ACCENT_COLOR : BG_SECONDAIRE);
                                return c;
                            }
                        });
                    });

                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(StatistiquesPanel.this,
                                "Erreur SQL: " + e.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private void executeQuery1() {
        String sql = "SELECT l.nom_ligne, COUNT(a.matricule) AS nb_etudiants " +
                "FROM LIGNE l " +
                "LEFT JOIN ABONNEMENT a ON l.id_ligne = a.id_ligne AND a.actif = 1 " +
                "GROUP BY l.id_ligne, l.nom_ligne " +
                "ORDER BY nb_etudiants DESC";
        String[] columns = {"📌 Ligne", "👨‍🎓 Nombre d'étudiants"};
        executeQuery(sql, new Object[]{}, columns);
        cardLayout.show(queryPanel, "default");
    }

    private void executeQuery2() {
        String sql = "SELECT b.immatriculation, b.modele, b.capacite, " +
                "COALESCE(COUNT(a.matricule), 0) AS nb_etudiants, " +
                "ROUND(COALESCE(COUNT(a.matricule), 0) * 100.0 / NULLIF(b.capacite, 0), 1) AS taux " +
                "FROM BUS b " +
                "LEFT JOIN AFFECTER af ON b.id_bus = af.id_bus AND CURDATE() BETWEEN af.date_debut AND af.date_fin " +
                "LEFT JOIN ABONNEMENT a ON af.id_ligne = a.id_ligne AND a.actif = 1 " +
                "GROUP BY b.id_bus, b.immatriculation, b.modele, b.capacite " +
                "ORDER BY taux DESC";
        String[] columns = {"🚍 Immatriculation", "📦 Modèle", "💺 Capacité", "👨‍🎓 Nb Étudiants", "📊 Taux (%)"};
        executeQuery(sql, new Object[]{}, columns);
        cardLayout.show(queryPanel, "default");
    }

    private void executeQuery3() {
        cardLayout.show(queryPanel, "query3");
        resultTable.setModel(new DefaultTableModel());
    }

    private void executeQuery3WithLigne(JComboBox<String> ligneCombo) {
        if (ligneCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Aucune ligne disponible", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String selected = (String) ligneCombo.getSelectedItem();
        if (selected == null) return;
        int idLigne = Integer.parseInt(selected.split(" - ")[0]);
        String sql = "SELECT h.jour, h.heure_depart, h.heure_retour " +
                "FROM HORAIRE h " +
                "WHERE h.id_ligne = ? " +
                "ORDER BY FIELD(h.jour, 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'), h.heure_depart";
        String[] columns = {"📅 Jour", "⏰ Heure Départ", "⏰ Heure Retour"};
        executeQuery(sql, new Object[]{idLigne}, columns);
    }

    private void executeQuery4() {
        String sql = "SELECT e.matricule, e.nom, e.prenom, e.email, e.telephone " +
                "FROM ETUDIANT e " +
                "WHERE NOT EXISTS (SELECT 1 FROM ABONNEMENT a WHERE a.matricule = e.matricule AND a.actif = 1)";
        String[] columns = {"🆔 Matricule", "👤 Nom", "👤 Prénom", "📧 Email", "📞 Téléphone"};
        executeQuery(sql, new Object[]{}, columns);
        cardLayout.show(queryPanel, "default");
    }

    private void executeQuery5() {
        cardLayout.show(queryPanel, "query5");
        resultTable.setModel(new DefaultTableModel());
    }

    private void executeQuery5WithMatricule(JTextField matriculeField) {
        String matricule = matriculeField.getText().trim().toUpperCase();
        if (matricule.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un matricule", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT l.nom_ligne, a.date_debut, a.date_fin, " +
                "CASE WHEN a.actif = 1 THEN '✅ Actif' ELSE '❌ Inactif' END AS statut " +
                "FROM ABONNEMENT a " +
                "JOIN LIGNE l ON a.id_ligne = l.id_ligne " +
                "WHERE a.matricule = ? " +
                "ORDER BY a.date_debut DESC";
        String[] columns = {"🗺️ Ligne", "📅 Date Début", "📅 Date Fin", "📌 Statut"};
        executeQuery(sql, new Object[]{matricule}, columns);
    }

    private void executeQuery6() {
        cardLayout.show(queryPanel, "query6");
        resultTable.setModel(new DefaultTableModel());
    }

    private void executeQuery6WithParams(JComboBox<String> ligneCombo, JTextField dateField) {
        if (ligneCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Aucune ligne disponible", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String selected = (String) ligneCombo.getSelectedItem();
        if (selected == null) return;
        int idLigne = Integer.parseInt(selected.split(" - ")[0]);
        String date = dateField.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une date", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT b.immatriculation, b.modele, b.capacite, " +
                "af.date_debut, af.date_fin " +
                "FROM BUS b " +
                "JOIN AFFECTER af ON b.id_bus = af.id_bus " +
                "WHERE af.id_ligne = ? " +
                "AND ? BETWEEN af.date_debut AND af.date_fin";
        String[] columns = {"🚍 Immatriculation", "📦 Modèle", "💺 Capacité", "📅 Date Début", "📅 Date Fin"};
        try {
            executeQuery(sql, new Object[]{idLigne, Date.valueOf(date)}, columns);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD\nExemple: 2024-01-15",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeQuery7() {
        String sql = "SELECT l.nom_ligne, " +
                "COUNT(DISTINCT a.matricule) AS nb_etudiants, " +
                "COALESCE(b.capacite, 0) AS capacite, " +
                "ROUND(COUNT(DISTINCT a.matricule) * 100.0 / NULLIF(b.capacite, 0), 1) AS taux_charge " +
                "FROM LIGNE l " +
                "LEFT JOIN ABONNEMENT a ON l.id_ligne = a.id_ligne AND a.actif = 1 " +
                "LEFT JOIN AFFECTER af ON l.id_ligne = af.id_ligne AND CURDATE() BETWEEN af.date_debut AND af.date_fin " +
                "LEFT JOIN BUS b ON af.id_bus = b.id_bus " +
                "GROUP BY l.id_ligne, l.nom_ligne, b.capacite " +
                "ORDER BY nb_etudiants DESC";
        String[] columns = {"🗺️ Ligne", "👨‍🎓 Nb Étudiants", "💺 Capacité Bus", "📊 Taux Charge (%)"};
        executeQuery(sql, new Object[]{}, columns);
        cardLayout.show(queryPanel, "default");
    }

    private void executeQuery8() {
        String sql = "SELECT t.id_trajet, t.date, l.nom_ligne, " +
                "b.immatriculation, h.heure_depart, t.heure_reelle, " +
                "TIME_FORMAT(TIMEDIFF(t.heure_reelle, h.heure_depart), '%H:%i:%s') AS retard " +
                "FROM TRAJET t " +
                "JOIN LIGNE l ON t.id_ligne = l.id_ligne " +
                "JOIN BUS b ON t.id_bus = b.id_bus " +
                "JOIN HORAIRE h ON t.id_horaire = h.id_horaire " +
                "WHERE t.statut = 'retard' " +
                "ORDER BY t.date DESC, retard DESC";
        String[] columns = {"🆔 ID", "📅 Date", "🗺️ Ligne", "🚍 Bus", "⏰ Heure Prévue", "⏰ Heure Réelle", "⚠️ Retard"};
        executeQuery(sql, new Object[]{}, columns);
        cardLayout.show(queryPanel, "default");
    }
}