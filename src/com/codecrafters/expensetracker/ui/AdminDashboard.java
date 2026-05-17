package com.codecrafters.expensetracker.ui;

import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
 
/**
 * Administrator dashboard screen for the Smart Expense Tracker.
 *
 * <p>Displayed after a successful admin login. Provides system-wide statistics
 * and navigation to all administrator-only features:</p>
 * <ul>
 *   <li>User Management — view, activate/deactivate, and delete user accounts</li>
 *   <li>All Transactions — browse and edit any transaction in the system</li>
 *   <li>Insert Test Data — quickly seed the database for testing</li>
 * </ul>
 *
 * <p>The dashboard uses a teal/dark colour scheme to visually distinguish it
 * from the user-facing {@link MainDashboard}.</p>
 *
 * <p><b>Integration note for Hai:</b> The stat cards (Total Users, Total Transactions,
 * Income, Expenses) currently show placeholder values. Wire them to
 * {@code AdminManager.getSystemSummary()} once available and call
 * {@link #refreshStats(int, int, double, double)} to update them.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     LoginScreen
 * @see     MainDashboard
 */
public class AdminDashboard extends JFrame {
 
    // UI constants
    /** Width of the admin dashboard window in pixels. */
    private static final int WINDOW_WIDTH  = 520;
 
    /** Height of the admin dashboard window in pixels. */
    private static final int WINDOW_HEIGHT = 620;
 
    /** Dark teal used for the admin navigation bar. */
    private static final Color COLOR_NAVBAR  = new Color(20, 60, 80);
 
    /** Teal accent colour for admin action buttons. */
    private static final Color COLOR_TEAL    = new Color(30, 110, 120);
 
    /** Secondary teal for alternate buttons. */
    private static final Color COLOR_TEAL2   = new Color(20, 85, 95);
 
    /** Dark red used for destructive action buttons. */
    private static final Color COLOR_DANGER  = new Color(160, 30, 30);
 
    /** Light background for the dashboard body. */
    private static final Color COLOR_BG      = new Color(240, 246, 248);
 
    /** Dark colour for heading text. */
    private static final Color COLOR_TEXT    = new Color(20, 40, 50);
 
    // Backend 
    /** Shared database access object passed to child admin screens. */
    private final DatabaseManager dbManager;
 
    // Stat labels — updated by refreshStats() 
    /** Displays total number of registered users. */
    private JLabel totalUsersLabel;
 
    /** Displays total number of transactions across all users. */
    private JLabel totalTransactionsLabel;
 
    /** Displays total income across all user accounts. */
    private JLabel totalIncomeLabel;
 
    /** Displays total expenses across all user accounts. */
    private JLabel totalExpensesLabel;
    
    /**
     * Constructs and displays the admin dashboard.
     *
     * @param dbManager the shared {@link DatabaseManager}; must not be null
     */
    public AdminDashboard(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Smart Expense Tracker — Admin Panel");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and arranges the admin dashboard layout.
     */
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildNavBar(),      BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
    }
 
    /**
     * Builds the top navigation bar for the admin panel.
     * Displays the admin username and a logout button.
     *
     * @return a configured navigation {@link JPanel}
     */
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(COLOR_NAVBAR);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));
 
        // Left: app + role badge
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftGroup.setBackground(COLOR_NAVBAR);
 
        JLabel appName = new JLabel("Smart Expense Tracker");
        appName.setFont(new Font("Arial", Font.BOLD, 15));
        appName.setForeground(Color.WHITE);
 
        JLabel badge = new JLabel("ADMIN");
        badge.setFont(new Font("Arial", Font.BOLD, 10));
        badge.setForeground(new Color(20, 60, 80));
        badge.setBackground(new Color(80, 200, 180));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));
 
        leftGroup.add(appName);
        leftGroup.add(badge);
 
        // Right: who + logout
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightGroup.setBackground(COLOR_NAVBAR);
 
        JLabel whoLabel = new JLabel(SessionManager.getCurrentUsername());
        whoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        whoLabel.setForeground(new Color(160, 210, 220));
 
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(190, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());
 
        rightGroup.add(whoLabel);
        rightGroup.add(logoutBtn);
 
        nav.add(leftGroup,  BorderLayout.WEST);
        nav.add(rightGroup, BorderLayout.EAST);
        return nav;
    }
 
    /**
     * Builds the scrollable centre panel containing stat cards and admin action buttons.
     *
     * @return a configured centre {@link JPanel}
     */
    private JPanel buildCentrePanel() {
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(COLOR_BG);
        centre.setBorder(new EmptyBorder(24, 36, 24, 36));
 
        // Page heading
        JLabel heading = new JLabel("Admin Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(COLOR_TEXT);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(heading);
 
        JLabel subheading = new JLabel("System Overview");
        subheading.setFont(new Font("Arial", Font.PLAIN, 13));
        subheading.setForeground(new Color(80, 110, 120));
        subheading.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(subheading);
 
        centre.add(Box.createVerticalStrut(22));
 
        // Stats grid
        centre.add(buildStatsGrid());
 
        centre.add(Box.createVerticalStrut(28));
 
        // Section heading
        JLabel actionsLabel = new JLabel("Admin Actions");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        actionsLabel.setForeground(new Color(60, 90, 100));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(actionsLabel);
 
        centre.add(Box.createVerticalStrut(14));
 
        // Admin action buttons
        centre.add(makeAdminButton(
            "\uD83D\uDC65  Manage Users",
            COLOR_TEAL,
            "View, activate, deactivate, or delete user accounts"));
        centre.add(Box.createVerticalStrut(12));
        centre.add(makeAdminButton(
            "\uD83D\uDCB3  All Transactions",
            COLOR_TEAL2,
            "Browse and edit every transaction in the system"));
        centre.add(Box.createVerticalStrut(12));
        centre.add(makeAdminButton(
            "\uD83E\uDDEA  Insert Test Data",
            new Color(90, 60, 130),
            "Quickly seed the database with sample users and transactions"));
 
        return centre;
    }
 
    /**
     * Builds a 2×2 grid of system stat cards.
     *
     * <p>The four cards show: Total Users, Total Transactions, Total Income,
     * Total Expenses. Labels are saved as fields so {@link #refreshStats} can
     * update them without rebuilding the layout.</p>
     *
     * @return a {@link JPanel} containing four stat cards in a grid layout
     */
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setBackground(COLOR_BG);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // Card labels are stored as fields so refreshStats() can update them
        totalUsersLabel        = new JLabel("—");
        totalTransactionsLabel = new JLabel("—");
        totalIncomeLabel       = new JLabel("—");
        totalExpensesLabel     = new JLabel("—");
 
        grid.add(buildStatCard("Total Users",        totalUsersLabel,        new Color(30, 90, 130)));
        grid.add(buildStatCard("Total Transactions", totalTransactionsLabel, new Color(30, 100, 80)));
        grid.add(buildStatCard("Total Income",        totalIncomeLabel,       new Color(33, 115, 70)));
        grid.add(buildStatCard("Total Expenses",      totalExpensesLabel,     new Color(160, 50, 50)));
 
        return grid;
    }
 
    /**
     * Builds a single stat card with a coloured header and a large value label.
     *
     * @param title      the name of the statistic
     * @param valueLabel the pre-created {@link JLabel} that will show the value
     * @param headerColor the background colour for the card's title bar
     * @return a configured stat card {@link JPanel}
     */
    private JPanel buildStatCard(String title, JLabel valueLabel, Color headerColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 218, 228), 1, true));
 
        // Title bar
        JPanel titleBar = new JPanel();
        titleBar.setBackground(headerColor);
        titleBar.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel);
 
        // Value
        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));
        valueLabel.setForeground(COLOR_TEXT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        card.add(titleBar,    BorderLayout.NORTH);
        card.add(valueLabel,  BorderLayout.CENTER);
        return card;
    }
 
    /**
     * Updates the four system stat cards with live data.
     *
     * <p>Call this after the dashboard loads or after any admin action that
     * modifies user or transaction counts.</p>
     *
     * @param totalUsers        number of registered users
     * @param totalTransactions number of transactions in the system
     * @param totalIncome       sum of all income transactions
     * @param totalExpenses     sum of all expense transactions
     */
    public void refreshStats(int totalUsers, int totalTransactions,
                             double totalIncome, double totalExpenses) {
        totalUsersLabel.setText(String.valueOf(totalUsers));
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));
        totalIncomeLabel.setText(String.format("$%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));
    }
 
    /**
     * Creates a styled full-width admin action button.
     *
     * @param text    the button label (may include an emoji prefix)
     * @param color   the background fill colour
     * @param tooltip the tooltip shown on hover
     * @return a configured {@link JButton}
     */
    private JButton makeAdminButton(String text, Color color, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
 
        // TODO: add ActionListeners when admin sub-screens are ready
        return btn;
    }
 
    /**
     * Logs the administrator out, clears the session, and returns to the
     * {@link LoginScreen}.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            dispose();
            new LoginScreen(dbManager);
        }
    }
}
