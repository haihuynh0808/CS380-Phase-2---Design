package com.codecrafters.expensetracker.ui;

import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
 
/**
 * Form screen for adding a new financial transaction.
 *
 * <p>Allows the logged-in user to record a new income or expense by filling
 * in the following fields:</p>
 * <ul>
 *   <li>Type — Income or Expense (dropdown)</li>
 *   <li>Amount — positive number</li>
 *   <li>Category — e.g. Salary, Food, Rent</li>
 *   <li>Date — in YYYY-MM-DD format, defaults to today</li>
 *   <li>Description — optional free-text note</li>
 * </ul>
 *
 * <p>All fields are validated before the transaction is saved. On success the
 * window closes and the parent {@link MainDashboard} is refreshed so the new
 * balance is shown immediately.</p>
 *
 * <p><b>Integration note for Kaltoum:</b> The save button currently calls
 * {@code dbManager.insertTransaction()} directly via the stub. Once
 * {@code TransactionManager} is ready, replace that call with
 * {@code transactionManager.addTransaction(...)} and pass in the
 * {@code TransactionManager} instance from {@link MainDashboard}.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     MainDashboard
 */
public class AddTransactionScreen extends JFrame {
 
    // UI constants 
    /** Width of the Add Transaction window in pixels. */
    private static final int WINDOW_WIDTH  = 440;
 
    /** Height of the Add Transaction window in pixels. */
    private static final int WINDOW_HEIGHT = 580;
 
    /** Expected date input format. */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
 
    /** Primary brand colour used for the header. */
    private static final Color COLOR_BRAND   = new Color(31, 78, 121);
 
    /** Accent blue for the Save button. */
    private static final Color COLOR_SAVE    = new Color(33, 115, 70);
 
    /** Light background for the form. */
    private static final Color COLOR_BG      = new Color(245, 248, 252);
 
    /** Label text colour. */
    private static final Color COLOR_LABEL   = new Color(40, 40, 40);
 
    /** Red used for validation error messages. */
    private static final Color COLOR_ERROR   = new Color(180, 30, 30);
 
    /** Background tint applied when Income is selected. */
    private static final Color COLOR_INCOME_TINT  = new Color(235, 248, 240);
 
    /** Background tint applied when Expense is selected. */
    private static final Color COLOR_EXPENSE_TINT = new Color(252, 240, 240);
 
    /** Preset category options shown in the category dropdown. */
    private static final String[] CATEGORIES = {
        "Salary", "Food", "Rent", "Transportation",
        "Utilities", "Entertainment", "Healthcare", "Education", "Other"
    };
 
    // Backend 
    /** Shared database access object used to persist the new transaction. */
    private final DatabaseManager dbManager;
 
    /** Reference to the parent dashboard, refreshed after a successful save. */
    private final MainDashboard parentDashboard;
 
    // UI components 
    /** Dropdown for selecting Income or Expense. */
    private JComboBox<String> typeCombo;
 
    /** Text field for entering the transaction amount. */
    private JTextField amountField;
 
    /** Dropdown for selecting a spending/income category. */
    private JComboBox<String> categoryCombo;
 
    /** Text field for entering the transaction date (YYYY-MM-DD). */
    private JTextField dateField;
 
    /** Text area for entering an optional description. */
    private JTextArea descriptionArea;
 
    /** Label used to display validation error messages. */
    private JLabel errorLabel;
 
    /** The centre form panel — background changes with the selected type. */
    private JPanel formPanel;
 
    /**
     * Constructs and displays the Add Transaction screen.
     *
     * @param dbManager       the shared {@link DatabaseManager}; must not be null
     * @param parentDashboard the {@link MainDashboard} to refresh on success;
     *                        must not be null
     */
    public AddTransactionScreen(DatabaseManager dbManager, MainDashboard parentDashboard) {
        this.dbManager       = dbManager;
        this.parentDashboard = parentDashboard;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Add Transaction");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and arranges all UI components.
     */
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(),   BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
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
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        JLabel title = new JLabel("Add Transaction");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Record a new income or expense");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 210, 240));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }
 
    /**
     * Builds the centre form panel containing all input fields.
     * The panel's background tints green for Income and red for Expense
     * based on the current dropdown selection.
     *
     * @return a configured form {@link JPanel}
     */
    private JPanel buildFormPanel() {
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BG);
        formPanel.setBorder(new EmptyBorder(20, 36, 10, 36));
 
        // Type dropdown 
        formPanel.add(makeLabel("Transaction Type"));
        formPanel.add(Box.createVerticalStrut(6));
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        typeCombo.setBackground(Color.WHITE);
        typeCombo.addActionListener(e -> updateTypeTint());
        formPanel.add(typeCombo);
 
        formPanel.add(Box.createVerticalStrut(14));
 
        // Amount 
        formPanel.add(makeLabel("Amount ($)"));
        formPanel.add(Box.createVerticalStrut(6));
        amountField = makeTextField("e.g. 250.00");
        formPanel.add(amountField);
 
        formPanel.add(Box.createVerticalStrut(14));
 
        // Category 
        formPanel.add(makeLabel("Category"));
        formPanel.add(Box.createVerticalStrut(6));
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        categoryCombo.setBackground(Color.WHITE);
        formPanel.add(categoryCombo);
 
        formPanel.add(Box.createVerticalStrut(14));
 
        // Date 
        formPanel.add(makeLabel("Date (YYYY-MM-DD)"));
        formPanel.add(Box.createVerticalStrut(6));
        dateField = makeTextField(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        formPanel.add(dateField);
 
        formPanel.add(Box.createVerticalStrut(14));
 
        // Description 
        formPanel.add(makeLabel("Description (optional)"));
        formPanel.add(Box.createVerticalStrut(6));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        formPanel.add(descScroll);
 
        formPanel.add(Box.createVerticalStrut(12));
 
        // Error label 
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(errorLabel);
 
        return formPanel;
    }
 
    /**
     * Builds the bottom button panel containing Save and Cancel buttons.
     *
     * @return a configured button {@link JPanel}
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(0, 36, 20, 36));
 
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(180, 180, 180));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());
 
        JButton saveBtn = new JButton("Save Transaction");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setBackground(COLOR_SAVE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(this::handleSave);
 
        panel.add(cancelBtn);
        panel.add(saveBtn);
        return panel;
    }
 
    // Event handlers 
 
    /**
     * Handles the Save button action. Reads and validates all form fields,
     * then inserts the transaction into the database if valid.
     *
     * <p>Validation rules:</p>
     * <ul>
     *   <li>Amount must not be blank and must be a positive number.</li>
     *   <li>Date must be in YYYY-MM-DD format.</li>
     * </ul>
     *
     * <p>On success, the parent dashboard balance is refreshed and this
     * window is closed.</p>
     *
     * @param event the action event fired by the Save button (unused)
     */
    private void handleSave(ActionEvent event) {
        // Read fields 
        String type        = (String) typeCombo.getSelectedItem();
        String amountText  = amountField.getText().trim();
        String category    = (String) categoryCombo.getSelectedItem();
        String dateText    = dateField.getText().trim();
        String description = descriptionArea.getText().trim();
 
        // Validate amount 
        if (amountText.isEmpty()) {
            showError("Amount is required.");
            return;
        }
 
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showError("Amount must be a number (e.g. 250.00).");
            return;
        }
 
        if (amount <= 0) {
            showError("Amount must be greater than zero.");
            return;
        }
 
        // Validate date
        LocalDate date;
        try {
            date = LocalDate.parse(dateText,
                    DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            showError("Date must be in YYYY-MM-DD format (e.g. 2026-05-17).");
            return;
        }
 
        //  Save to database 
        int userId = SessionManager.getCurrentUserId();
 
        // TODO (Kaltoum): replace with transactionManager.addTransaction(...)
        // boolean success = transactionManager.addTransaction(
        //     userId, type, amount, category, date, description);
        boolean success = dbManager.insertTransaction(
                userId, type, amount, category, date,
                description.isEmpty() ? null : description);
 
        if (!success) {
            showError("Failed to save transaction. Please try again.");
            return;
        }
 
        // Success
        JOptionPane.showMessageDialog(
            this,
            type + " of $" + String.format("%.2f", amount)
                + " saved successfully!",
            "Transaction Saved",
            JOptionPane.INFORMATION_MESSAGE
        );
 
        // TODO (Kaltoum): replace 0.00 with real balance from TransactionManager
        // parentDashboard.refreshBalance(transactionManager.calculateBalance(userId));
        parentDashboard.refreshBalance(0.00);
 
        dispose();
    }
 
    /**
     * Updates the form panel background tint to reflect the selected
     * transaction type — green for Income, red for Expense.
     */
    private void updateTypeTint() {
        String selected = (String) typeCombo.getSelectedItem();
        if ("Income".equals(selected)) {
            formPanel.setBackground(COLOR_INCOME_TINT);
        } else {
            formPanel.setBackground(COLOR_EXPENSE_TINT);
        }
    }
 
    /**
     * Displays a validation error message on the form.
     *
     * @param message the error text to show
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
 
    //  UI utilities 
 
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
     * @param placeholder hint text shown in the field
     * @return a configured {@link JTextField}
     */
    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return field;
    }
}