package org.example.views;

// ==========================================
// Fichier : views/LoginDialog.java (LAYOUT HORIZONTAL + CHAMPS AGRANDIS)
// ==========================================


import org.example.dao.UtilisateurDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private final Color BG_PRINCIPAL = new Color(18, 18, 24);
    private final Color BG_CARD = new Color(28, 28, 38);
    private final Color ACCENT_COLOR = new Color(99, 102, 241);
    private final Color ACCENT_HOVER = new Color(129, 132, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color TEXT_SECONDARY = new Color(161, 161, 170);
    private final Color BORDER_COLOR = new Color(63, 63, 70);
    private final Color ERROR_COLOR = new Color(239, 68, 68);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean authenticated = false;
    private UtilisateurDAO utilisateurDAO;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginDialog(JFrame parent) {
        super(parent, "", true);
        utilisateurDAO = new UtilisateurDAO();
        setUndecorated(true);
        setSize(900, 550);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        // Main panel with rounded corners
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_PRINCIPAL);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        mainPanel.setOpaque(false);

        // Left panel - Image/Branding
        JPanel leftPanel = createLeftPanel();
        leftPanel.setPreferredSize(new Dimension(400, 550));

        // Right panel - Login form (with horizontal layout for fields)
        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Close button (top right)
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        add(mainPanel);

        // Enable dragging to move window
        addWindowDragListener(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 102, 241),
                        getWidth(), getHeight(), new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel busIcon = new JLabel("🚌");
        busIcon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        busIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("UniTransport");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Gestion du Transport Universitaire");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Version 2.0 | USTHB 2026");
        versionLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(200, 200, 255));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(busIcon);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(versionLabel);
        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));

        // Welcome text
        JLabel welcomeLabel = new JLabel("Connexion");
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instructionLabel = new JLabel("Veuillez saisir vos identifiants pour accéder à l'application");
        instructionLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        instructionLabel.setForeground(TEXT_SECONDARY);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);

        // Username field
        JLabel usernameLabel = new JLabel("NOM D'UTILISATEUR");
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 12));
        usernameLabel.setForeground(TEXT_SECONDARY);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(380, 52));
        usernameField.setMinimumSize(new Dimension(380, 52));
        usernameField.setMaximumSize(new Dimension(380, 52));
        usernameField.setBackground(BG_CARD);
        usernameField.setForeground(TEXT_COLOR);
        usernameField.setCaretColor(ACCENT_COLOR);
        usernameField.setFont(new Font("Inter", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(0, 18, 0, 18)
        ));

        // Password field
        JLabel passwordLabel = new JLabel("MOT DE PASSE");
        passwordLabel.setFont(new Font("Inter", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT_SECONDARY);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(380, 52));
        passwordField.setMinimumSize(new Dimension(380, 52));
        passwordField.setMaximumSize(new Dimension(380, 52));
        passwordField.setBackground(BG_CARD);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setCaretColor(ACCENT_COLOR);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(0, 18, 0, 18)
        ));
        passwordField.setEchoChar('•');

        // Show password checkbox
        JCheckBox showPasswordCheck = new JCheckBox("Afficher le mot de passe");
        showPasswordCheck.setFont(new Font("Inter", Font.PLAIN, 12));
        showPasswordCheck.setForeground(TEXT_SECONDARY);
        showPasswordCheck.setBackground(BG_PRINCIPAL);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_COLOR);

        // Login button
        loginButton = new JButton("SE CONNECTER");
        loginButton.setFont(new Font("Inter", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(380, 52));
        loginButton.setMinimumSize(new Dimension(380, 52));
        loginButton.setMaximumSize(new Dimension(380, 52));

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(ACCENT_COLOR);
            }
        });

        loginButton.addActionListener(e -> attemptLogin());

        // Demo accounts
        JPanel demoPanel = new JPanel();
        demoPanel.setOpaque(false);
        demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel demoLabel = new JLabel("🔐 Comptes de démonstration");
        demoLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        demoLabel.setForeground(TEXT_SECONDARY);
        demoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel demoButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        demoButtons.setOpaque(false);

        JButton adminDemo = createDemoButton("Admin", "admin", "admin123");
        JButton userDemo = createDemoButton("Utilisateur", "user", "user123");

        demoButtons.add(adminDemo);
        demoButtons.add(userDemo);

        demoPanel.add(demoLabel);
        demoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        demoPanel.add(demoButtons);

        // Layout des composants dans GridBagLayout (côte à côte label + field)
        // Username ligne
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(usernameField, gbc);

        // Password ligne
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(showPasswordCheck, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(errorLabel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        formPanel.add(loginButton, gbc);

        // Ajouter tous les composants au rightPanel
        rightPanel.add(welcomeLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        rightPanel.add(instructionLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 32)));
        rightPanel.add(formPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(demoPanel);
        rightPanel.add(Box.createVerticalGlue());

        return rightPanel;
    }

    private JButton createDemoButton(String text, String username, String password) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.PLAIN, 11));
        button.setForeground(ACCENT_COLOR);
        button.setBackground(BG_CARD);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            usernameField.setText(username);
            passwordField.setText(password);
        });
        return button;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        titleLabel.setForeground(ACCENT_COLOR);

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeButton.setForeground(TEXT_SECONDARY);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(ERROR_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(TEXT_SECONDARY);
            }
        });

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(closeButton, BorderLayout.EAST);

        return topBar;
    }

    private void addWindowDragListener(JPanel mainPanel) {
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int dragX, dragY;

            public void mousePressed(MouseEvent e) {
                dragX = e.getX();
                dragY = e.getY();
            }

            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - dragX,
                        getLocation().y + e.getY() - dragY);
            }
        };

        mainPanel.addMouseListener(dragAdapter);
        mainPanel.addMouseMotionListener(dragAdapter);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Reset error
        errorLabel.setText(" ");
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(0, 18, 0, 18)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(0, 18, 0, 18)
        ));

        if (username.isEmpty()) {
            showError("Veuillez saisir votre nom d'utilisateur", usernameField);
            return;
        }

        if (password.isEmpty()) {
            showError("Veuillez saisir votre mot de passe", passwordField);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return utilisateurDAO.authenticate(username, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                loginButton.setEnabled(true);

                try {
                    if (get()) {
                        authenticated = true;
                        dispose();
                    } else {
                        showError("Nom d'utilisateur ou mot de passe incorrect", passwordField);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Erreur: " + e.getMessage(), null);
                }
            }
        };
        worker.execute();
    }

    private void showError(String message, JComponent field) {
        errorLabel.setText(message);
        if (field != null) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(0, 18, 0, 18)
            ));
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}