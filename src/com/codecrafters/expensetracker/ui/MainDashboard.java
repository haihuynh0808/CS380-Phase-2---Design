package com.codecrafters.expensetracker.ui;

import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
 
/**
 * Main dashboard screen for regular users of the Smart Expense Tracker.
 *
 * <p>Displayed after a successful user login. Shows a personalised welcome,
 * the current account balance, and navigation buttons to the core application
 * features:</p>
 * <ul>
 *   <li>Add Transaction</li>
 *   <li>View Transaction History</li>
 *   <li>Category Summary</li>
 * </ul>
 *
 * <p>A logout button ends the session and returns the user to the
 * {@link LoginScreen}.</p>
 *
 * <p><b>Integration note for Kaltoum:</b> The balance display currently shows
 * a placeholder value. Wire it to {@code TransactionManager.calculateBalance()}
 * once that class is available and call {@link #refreshBalance()} to update
 * the label.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     LoginScreen
 */
public class MainDashboard extends JFrame {
 
    // UI constants 
    /** Width of the dashboard window in pixels. */
    private static final int WINDOW_WIDTH  = 480;
 
    /** Height of the dashboard window in pixels. */
    private static final int WINDOW_HEIGHT = 560;
 
    /** Deep blue used for the top navigation bar. */
    private static final Color COLOR_NAVBAR = new Color(31, 78, 121);
 
    /** Accent blue used for primary action buttons. */
    private static final Color COLOR_BTN   = new Color(46, 117, 182);
 
    /** Soft background colour. */
    private static final Color COLOR_BG    = new Color(245, 248, 252);
 
    /** Dark colour for heading text. */
    private static final Color COLOR_TEXT  = new Color(30, 30, 30);
 
    /** Green used for a positive balance. */
    private static final Color COLOR_POSITIVE = new Color(33, 115, 70);
 
    /** Red used for a negative balance. */
    private static final Color COLOR_NEGATIVE = new Color(180, 30, 30);
 
    // Backend 
    /** Shared database access object passed down to child screens. */
    private final DatabaseManager dbManager;
 
    // UI components
    /** Label that displays the user's current balance. Updated by {@link #refreshBalance()}. */
    private JLabel balanceValueLabel;
 
    /**
     * Constructs and displays the main user dashboard.
     *
     * @param dbManager the shared {@link DatabaseManager}; must not be null
     */
    public MainDashboard(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Smart Expense Tracker — Dashboard");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and arranges the dashboard layout:
     * navigation bar at the top, balance card in the centre, and action
     * buttons below.
     */
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildNavBar(),      BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
    }
 
    /**
     * Builds the top navigation bar showing the app name and a logout button.
     *
     * @return a configured navigation {@link JPanel}
     */
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(COLOR_NAVBAR);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));
 
        JLabel appName = new JLabel("Smart Expense Tracker");
        appName.setFont(new Font("Arial", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
 
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(210, 60, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());
 
        nav.add(appName,   BorderLayout.WEST);
        nav.add(logoutBtn, BorderLayout.EAST);
        return nav;
    }
 
    /**
     * Builds the scrollable centre panel containing the welcome message,
     * balance card, and feature navigation buttons.
     *
     * @return a configured centre {@link JPanel}
     */
    private JPanel buildCentrePanel() {
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(COLOR_BG);
        centre.setBorder(new EmptyBorder(28, 40, 28, 40));
 
        // Welcome
        JLabel welcome = new JLabel("Welcome back, " + SessionManager.getCurrentUsername() + "!");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(COLOR_TEXT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(welcome);
 
        centre.add(Box.createVerticalStrut(24));
 
        // Balance card
        centre.add(buildBalanceCard());
 
        centre.add(Box.createVerticalStrut(32));
 
        // Section heading
        JLabel actionsLabel = new JLabel("What would you like to do?");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        actionsLabel.setForeground(new Color(80, 80, 80));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(actionsLabel);
 
        centre.add(Box.createVerticalStrut(14));
 
        // Feature buttons
            centre.add(makeNavButton("\u2795  Add Transaction", COLOR_BTN,
        	    "Opens the Add Transaction form",
        	    e -> new AddTransactionScreen(dbManager, this)));

        	centre.add(Box.createVerticalStrut(12));

        	centre.add(makeNavButton("\uD83D\uDCCB  Transaction History", new Color(60, 100, 160),
        	    "View your full transaction history",
        	    e -> new TransactionHistoryScreen(dbManager, this)));

        	centre.add(Box.createVerticalStrut(12));

        	centre.add(makeNavButton("\uD83D\uDCC8  Category Summary", new Color(38, 109, 80),
        	    "See spending grouped by category",
        	    e -> new CategorySummaryScreen(dbManager)));
 
        return centre;
    }
 
    /**
     * Builds the balance card panel showing the user's current net balance.
     *
     * <p>Call {@link #refreshBalance()} to update the displayed amount after
     * integrating with {@code TransactionManager}.</p>
     *
     * @return a configured balance card {@link JPanel}
     */
    private JPanel buildBalanceCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 218, 235), 1, true),
            new EmptyBorder(20, 30, 20, 30)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel cardTitle = new JLabel("Current Balance");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 13));
        cardTitle.setForeground(new Color(100, 120, 140));
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // TODO (Kaltoum): replace 0.00 with TransactionManager.calculateBalance(userId)
        balanceValueLabel = new JLabel("$0.00");
        balanceValueLabel.setFont(new Font("Arial", Font.BOLD, 34));
        balanceValueLabel.setForeground(COLOR_POSITIVE);
        balanceValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel hint = new JLabel("Income \u2212 Expenses");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(new Color(150, 160, 170));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(balanceValueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(hint);
        return card;
    }
 
    /**
     * Updates the balance label with the provided amount.
     *
     * <p>Displays the value in green if non-negative, red if negative.
     * Call this method after adding, editing, or deleting transactions.</p>
     *
     * @param balance the current net balance to display
     */
    public void refreshBalance(double balance) {
        balanceValueLabel.setText(String.format("$%.2f", balance));
        balanceValueLabel.setForeground(balance >= 0 ? COLOR_POSITIVE : COLOR_NEGATIVE);
    }
    /**
     * Creates a styled navigation button for a dashboard feature.
     *
     * @param text    the button label (may include an emoji prefix)
     * @param color   the background fill colour
     * @param tooltip the tooltip text shown on hover
     * @param action  the action to perform when the button is clicked
     * @return a configured full-width {@link JButton}
     */
    private JButton makeNavButton(String text, Color color, String tooltip,java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.addActionListener(action);
        return btn;
    }
 
    /**
     * Logs the current user out, clears the session, and returns to the
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
