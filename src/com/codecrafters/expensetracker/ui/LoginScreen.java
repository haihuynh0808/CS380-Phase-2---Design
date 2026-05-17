package com.codecrafters.expensetracker.ui;

import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
 
/**
 * Login screen for the Smart Expense Tracker application.
 *
 * <p>Allows both regular users and administrators to authenticate.
 * A radio-button toggle lets the user select their role before submitting.
 * On success, the appropriate screen is opened:</p>
 * <ul>
 *   <li>Regular users are routed to the {@link MainDashboard}.</li>
 *   <li>Administrators are routed to the {@link AdminDashboard}.</li>
 * </ul>
 *
 * <p>Navigation links to the {@link RegisterScreen} are provided for new users.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     RegisterScreen
 * @see     MainDashboard
 * @see     AdminDashboard
 */
public class LoginScreen extends JFrame {
 
    // UI constants 
    /** Width of the login window in pixels. */
    private static final int WINDOW_WIDTH  = 420;
 
    /** Height of the login window in pixels. */
    private static final int WINDOW_HEIGHT = 520;
 
    /** Primary brand colour used for the header panel. */
    private static final Color COLOR_BRAND    = new Color(31, 78, 121);
 
    /** Accent colour used for the login button. */
    private static final Color COLOR_ACCENT   = new Color(46, 117, 182);
 
    /** Light background colour for the form panel. */
    private static final Color COLOR_BG       = new Color(245, 248, 252);
 
    /** Foreground colour for labels and text. */
    private static final Color COLOR_LABEL    = new Color(40, 40, 40);
 
    /** Red used for error messages. */
    private static final Color COLOR_ERROR    = new Color(180, 30, 30);
 
    // Backend
    /** Database access object used to validate credentials. */
    private final DatabaseManager dbManager;
 
    // UI components
    /** Text field for the username input. */
    private JTextField usernameField;
 
    /** Password field for the password input. */
    private JPasswordField passwordField;
 
    /** Radio button that selects the "User" role. */
    private JRadioButton userRoleButton;
 
    /** Radio button that selects the "Admin" role. */
    private JRadioButton adminRoleButton;
 
    /** Label used to display authentication error messages. */
    private JLabel errorLabel;
 
    /**
     * Constructs and displays the login screen.
     *
     * @param dbManager the shared {@link DatabaseManager} instance; must not be null
     */
    public LoginScreen(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties (size, title, close behaviour).
     */
    private void initWindow() {
        setTitle("Smart Expense Tracker — Login");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);          // centre on screen
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and lays out all UI components using a {@link BorderLayout}.
     * The screen is divided into a branded header, a form panel, and a footer
     * with a registration link.
     */
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(),   BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }
 
    /**
     * Builds the branded header panel containing the application title and subtitle.
     *
     * @return a configured {@link JPanel} for the top of the window
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(COLOR_BRAND);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 20, 28, 20));
 
        JLabel title = new JLabel("Smart Expense Tracker");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 210, 240));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        return header;
    }
 
    /**
     * Builds the centre form panel containing username, password, role selection,
     * an error label, and the login button.
     *
     * @return a configured {@link JPanel} containing all form elements
     */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(30, 40, 10, 40));
 
        // Username 
        form.add(makeLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        usernameField = makeTextField("Enter your username");
        form.add(usernameField);
 
        form.add(Box.createVerticalStrut(16));
 
        // Password 
        form.add(makeLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        form.add(passwordField);
 
        form.add(Box.createVerticalStrut(20));
 
        // Role selection
        form.add(makeLabel("Login as"));
        form.add(Box.createVerticalStrut(8));
        form.add(buildRolePanel());
 
        form.add(Box.createVerticalStrut(16));
 
        // Error label 
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(errorLabel);
 
        form.add(Box.createVerticalStrut(10));
 
        //  Login button
        JButton loginBtn = makeActionButton("Login", COLOR_ACCENT);
        loginBtn.addActionListener(this::handleLogin);
        form.add(loginBtn);
 
        return form;
    }
 
    /**
     * Builds the role-selection panel containing two radio buttons grouped together.
     *
     * @return a {@link JPanel} with a "User" and an "Admin" radio button
     */
    private JPanel buildRolePanel() {
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setBackground(COLOR_BG);
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
 
        userRoleButton  = new JRadioButton("User",  true);
        adminRoleButton = new JRadioButton("Admin", false);
 
        styleRadioButton(userRoleButton);
        styleRadioButton(adminRoleButton);
 
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(userRoleButton);
        roleGroup.add(adminRoleButton);
 
        rolePanel.add(userRoleButton);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(adminRoleButton);
        return rolePanel;
    }
 
    /**
     * Builds the footer panel containing a link to the registration screen.
     *
     * @return a configured {@link JPanel} for the bottom of the window
     */
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));
 
        JLabel prompt = new JLabel("Don't have an account?");
        prompt.setFont(new Font("Arial", Font.PLAIN, 12));
        prompt.setForeground(COLOR_LABEL);
 
        JButton registerLink = new JButton("Register here");
        registerLink.setFont(new Font("Arial", Font.BOLD, 12));
        registerLink.setForeground(COLOR_ACCENT);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> openRegisterScreen());
 
        footer.add(prompt);
        footer.add(registerLink);
        return footer;
    }
 
    // Event handlers 
 
    /**
     * Handles the login button action. Reads credentials and role from the form,
     * validates them against the database, and navigates to the appropriate screen
     * on success.
     *
     * <p>On failure, an error message is shown without closing the window.</p>
     *
     * @param event the action event fired by the login button (unused)
     */
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = adminRoleButton.isSelected()
                          ? SessionManager.ROLE_ADMIN
                          : SessionManager.ROLE_USER;
 
        // Input validation 
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
 
        // Authenticate
        boolean valid = dbManager.validateLogin(username, password, role);
 
        if (!valid) {
            showError("Invalid username or password.");
            passwordField.setText("");
            return;
        }
 
        // Start session and navigate
        int userId = dbManager.getUserId(username);
        SessionManager.login(userId, username, role);
        clearError();
        navigateAfterLogin(role);
    }
 
    /**
     * Routes the authenticated user to the correct screen based on their role.
     *
     * @param role the authenticated role: {@link SessionManager#ROLE_USER} or
     *             {@link SessionManager#ROLE_ADMIN}
     */
    private void navigateAfterLogin(String role) {
        dispose();  // close login window
        if (SessionManager.ROLE_ADMIN.equals(role)) {
            new AdminDashboard(dbManager);
        } else {
            new MainDashboard(dbManager);
        }
    }
 
    /**
     * Closes the login window and opens the registration screen.
     */
    private void openRegisterScreen() {
        dispose();
        new RegisterScreen(dbManager);
    }
 
    // UI utilities
 
    /**
     * Displays an error message below the form fields.
     *
     * @param message the error text to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
 
    /**
     * Clears any displayed error message.
     */
    private void clearError() {
        errorLabel.setText(" ");
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
     * Creates a styled single-line text field with placeholder text.
     *
     * @param placeholder the greyed-out hint text shown when the field is empty
     * @return a configured {@link JTextField}
     */
    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField();
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
     * @param text            the button label
     * @param backgroundColor the fill colour for the button
     * @return a configured {@link JButton}
     */
    private JButton makeActionButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    /**
     * Applies consistent styling to a role-selection radio button.
     *
     * @param rb the {@link JRadioButton} to style
     */
    private void styleRadioButton(JRadioButton rb) {
        rb.setFont(new Font("Arial", Font.PLAIN, 13));
        rb.setBackground(COLOR_BG);
        rb.setForeground(COLOR_LABEL);
    }
 
    // Entry point
 
    /**
     * Application entry point. Creates the DatabaseManager and opens the login screen
     * on the Swing Event Dispatch Thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager db = new DatabaseManager();
            new LoginScreen(db);
        });
    }
}
