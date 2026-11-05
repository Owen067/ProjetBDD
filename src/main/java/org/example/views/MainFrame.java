package org.example.views;

// ==========================================
// Fichier : views/MainFrame.java
// ==========================================


// ==========================================
// Fichier : views/MainFrame.java (CORRIGÉ)
// ==========================================


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton[] navButtons;
    private JLabel statusLabel;

    // Couleurs
    private final Color BG_PRINCIPAL = new Color(26, 26, 46);
    private final Color BG_SECONDAIRE = new Color(22, 30, 62);
    private final Color SIDEBAR_COLOR = new Color(15, 52, 96);
    private final Color ACCENT_COLOR = new Color(233, 69, 96);
    private final Color TEXT_COLOR = new Color(234, 234, 234);

    public MainFrame() {
        setTitle("🚌 UniTransport USTHB - Système de Gestion du Transport Universitaire");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_PRINCIPAL);

        // Ajouter tous les panels avec des noms SIMPLES et UNIQUES
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new EtudiantPanel(), "ETUDIANTS");
        contentPanel.add(new BusPanel(), "BUS");
        contentPanel.add(new LignePanel(), "LIGNES");
        contentPanel.add(new StationPanel(), "STATIONS");
        contentPanel.add(new HorairePanel(), "HORAIRES");
        contentPanel.add(new AbonnementPanel(), "ABONNEMENTS");
        contentPanel.add(new TrajetPanel(), "TRAJETS");
        contentPanel.add(new IncidentPanel(), "INCIDENTS");
        contentPanel.add(new StatistiquesPanel(), "STATISTIQUES");

        add(contentPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // Dashboard par défaut
        cardLayout.show(contentPanel, "DASHBOARD");
        highlightButton(0);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setLayout(new BorderLayout());

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(SIDEBAR_COLOR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        JLabel logoLabel = new JLabel("🚌 UniTransport", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(ACCENT_COLOR);
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setBackground(SIDEBAR_COLOR);
        navPanel.setLayout(new GridLayout(10, 1, 5, 5));

        // Les noms affichés et les identifiants CardLayout
        String[] buttonLabels = {
                "🏠 Dashboard", "👨‍🎓 Etudiants", "🚍 Bus", "🗺️ Lignes",
                "📍 Stations", "⏰ Horaires", "📋 Abonnements",
                "🔄 Trajets", "⚠️ Incidents", "📊 Statistiques"
        };

        String[] panelNames = {
                "DASHBOARD", "ETUDIANTS", "BUS", "LIGNES",
                "STATIONS", "HORAIRES", "ABONNEMENTS",
                "TRAJETS", "INCIDENTS", "STATISTIQUES"
        };

        navButtons = new JButton[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            final int index = i;
            final String panelName = panelNames[i];

            JButton btn = new JButton(buttonLabels[i]);
            btn.setBackground(SIDEBAR_COLOR);
            btn.setForeground(TEXT_COLOR);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                // Afficher le panel correspondant
                cardLayout.show(contentPanel, panelName);
                highlightButton(index);
            });

            navPanel.add(btn);
            navButtons[i] = btn;
        }

        sidebar.add(navPanel, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setBackground(BG_SECONDAIRE);
        statusBar.setPreferredSize(new Dimension(getWidth(), 30));
        statusBar.setLayout(new BorderLayout());

        statusLabel = new JLabel("✅ Connecté à ProjetBDD | MySQL");
        statusLabel.setForeground(new Color(76, 175, 80));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusBar.add(statusLabel, BorderLayout.WEST);

        JLabel timeLabel = new JLabel(new java.util.Date().toString());
        timeLabel.setForeground(TEXT_COLOR);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.add(timeLabel, BorderLayout.EAST);

        // Mettre à jour l'heure chaque seconde
        new Timer(1000, e -> {
            timeLabel.setText(new java.util.Date().toString());
        }).start();

        return statusBar;
    }

    private void highlightButton(int index) {
        for (int i = 0; i < navButtons.length; i++) {
            if (i == index) {
                navButtons[i].setBackground(ACCENT_COLOR);
                navButtons[i].setForeground(Color.WHITE);
            } else {
                navButtons[i].setBackground(SIDEBAR_COLOR);
                navButtons[i].setForeground(TEXT_COLOR);
            }
        }
    }
}