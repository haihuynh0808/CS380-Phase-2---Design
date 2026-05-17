package com.codecrafters.expensetracker.ui;

import com.codecrafters.expensetracker.database.DatabaseManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
 
/**
 * Registration screen for the Smart Expense Tracker application.
 *
 * <p>Allows a new user to create an account by entering a username and
 * confirming their password. Validation is applied before submission:</p>
 * <ul>
 *   <li>No blank fields are accepted.</li>
 *   <li>Password and confirm-password must match.</li>
 *   <li>Username must not already exist in the database.</li>
 *   <li>Password must be at least 6 characters long.</li>
 * </ul>
 *
 * <p>On success, the new account is inserted and the user is returned to
 * the {@link LoginScreen}. A back-link is also provided for users who
 * already have an account.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     LoginScreen
 */
public class RegisterScreen extends JFrame {
 
    // ── UI constants ──────────────────────────────────────────────────────────
    /** Width of the registration window in pixels. */
    private static final int WINDOW_WIDTH  = 420;
 
    /** Height of the registration window in pixels. */
    private static final int WINDOW_HEIGHT = 560;
 
    /** Minimum number of characters required for a valid password. */
    private static final int MIN_PASSWORD_LENGTH = 6;
 
    /** Primary brand colour used for the header panel. */
    private static final Color COLOR_BRAND  = new Color(31, 78, 121);
 
    /** Green colour used for the register button. */
    private static final Color COLOR_GREEN  = new Color(33, 115, 70);
 
    /** Light background colour for the form panel. */
    private static final Color COLOR_BG     = new Color(245, 248, 252);
 
    /** Foreground colour for form labels. */
    private static final Color COLOR_LABEL  = new Color(40, 40, 40);
 
    /** Red used for error and validation messages. */
    private static final Color COLOR_ERROR  = new Color(180, 30, 30);
 
    // Backend
    /** Database access object used to check uniqueness and insert the new user. */
    private final DatabaseManager dbManager;
 
    // UI components
    /** Text field for the desired username. */
    private JTextField usernameField;
 
    /** Password field for the desired password. */
    private JPasswordField passwordField;
 
    /** Password field for confirming the password. */
    private JPasswordField confirmField;
 
    /** Label used to display validation error messages. */
    private JLabel errorLabel;
 
    /**
     * Constructs and displays the registration screen.
     *
     * @param dbManager the shared {@link DatabaseManager} instance; must not be null
     */
    public RegisterScreen(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Smart Expense Tracker — Create Account");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and arranges all UI components using a {@link BorderLayout}.
     */
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(),   BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }
 
    /**
     * Builds the branded header panel.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(COLOR_BRAND);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 20, 28, 20));
 
        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel subtitle = new JLabel("Join Smart Expense Tracker");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 210, 240));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        return header;
    }
 
    /**
     * Builds the form panel with username, password, confirm-password fields,
     * an error label, and the register button.
     *
     * @return a configured form {@link JPanel}
     */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(30, 40, 10, 40));
 
        // Username 
        form.add(makeLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        usernameField = makeTextField();
        form.add(usernameField);
 
        form.add(Box.createVerticalStrut(16));
 
        // Password
        form.add(makeLabel("Password  (min. " + MIN_PASSWORD_LENGTH + " characters)"));
        form.add(Box.createVerticalStrut(6));
        passwordField = makePasswordField();
        form.add(passwordField);
 
        form.add(Box.createVerticalStrut(16));
 
        // Confirm password
        form.add(makeLabel("Confirm Password"));
        form.add(Box.createVerticalStrut(6));
        confirmField = makePasswordField();
        form.add(confirmField);
 
        form.add(Box.createVerticalStrut(20));
 
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(errorLabel);
 
        form.add(Box.createVerticalStrut(10));
 
        // Register button
        JButton registerBtn = makeActionButton("Create Account", COLOR_GREEN);
        registerBtn.addActionListener(this::handleRegister);
        form.add(registerBtn);
 
        return form;
    }
 
    /**
     * Builds the footer panel with a back-link to the login screen.
     *
     * @return a configured footer {@link JPanel}
     */
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));
 
        JLabel prompt = new JLabel("Already have an account?");
        prompt.setFont(new Font("Arial", Font.PLAIN, 12));
        prompt.setForeground(COLOR_LABEL);
 
        JButton backLink = new JButton("Sign in here");
        backLink.setFont(new Font("Arial", Font.BOLD, 12));
        backLink.setForeground(new Color(46, 117, 182));
        backLink.setBorderPainted(false);
        backLink.setContentAreaFilled(false);
        backLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLink.addActionListener(e -> openLoginScreen());
 
        footer.add(prompt);
        footer.add(backLink);
        return footer;
    }
 
    // Event handlers
 
    /**
     * Handles the "Create Account" button action. Validates all fields, checks
     * database uniqueness, inserts the new user, then navigates back to login.
     *
     * @param event the action event fired by the register button (unused)
     */
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());
 
        // Validation
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("All fields are required.");
            return;
        }
 
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
            return;
        }
 
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            confirmField.setText("");
            return;
        }
 
        if (dbManager.usernameExists(username)) {
            showError("Username already taken. Please choose another.");
            usernameField.setText("");
            return;
        }
 
        // Insert new user
        boolean success = dbManager.insertUser(username, password);
 
        if (!success) {
            showError("Registration failed. Please try again.");
            return;
        }
 
        // Success: confirm and return to login
        JOptionPane.showMessageDialog(
            this,
            "Account created successfully!\nYou can now sign in.",
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
        openLoginScreen();
    }
 
    /**
     * Closes the registration window and opens the login screen.
     */
    private void openLoginScreen() {
        dispose();
        new LoginScreen(dbManager);
    }
 
    // UI utilities
 
    /**
     * Displays a validation error message below the form.
     *
     * @param message the error text to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
 
    /**
     * Creates a styled form label.
     *
     * @param text the label text
     * @return a configured {@link JLabel}
     */
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(COLOR_LABEL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
 
    /**
     * Creates a styled single-line text field.
     *
     * @return a configured {@link JTextField}
     */
    private JTextField makeTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return field;
    }
 
    /**
     * Creates a styled password field.
     *
     * @return a configured {@link JPasswordField}
     */
    private JPasswordField makePasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return field;
    }
 
    /**
     * Creates a styled primary action button.
     *
     * @param text  the button label
     * @param color the fill colour
     * @return a configured {@link JButton}
     */
    private JButton makeActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}