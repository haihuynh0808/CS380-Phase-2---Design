package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.Transaction;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
 
/**
 * Edit Transaction screen that pre-fills a form with an existing transaction's
 * data and allows the user to modify and save it.
 *
 * <p>All fields are pre-populated from the selected {@link Transaction} object.
 * Validation rules are identical to {@link AddTransactionScreen}. On success,
 * the parent {@link TransactionHistoryScreen} is refreshed to show the updated
 * data immediately.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     TransactionHistoryScreen
 */
public class EditTransactionScreen extends JFrame {
 
    // UI constants 
    /** Width of the edit transaction window in pixels. */
    private static final int WINDOW_WIDTH  = 440;
 
    /** Height of the edit transaction window in pixels. */
    private static final int WINDOW_HEIGHT = 560;
 
    /** Expected date input format. */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
 
    /** Dark orange header colour distinguishing this from the Add screen. */
    private static final Color COLOR_BRAND  = new Color(130, 70, 10);
 
    /** Green save button colour. */
    private static final Color COLOR_SAVE   = new Color(33, 115, 70);
 
    /** Light background. */
    private static final Color COLOR_BG     = new Color(245, 248, 252);
 
    /** Label text colour. */
    private static final Color COLOR_LABEL  = new Color(40, 40, 40);
 
    /** Red for validation errors. */
    private static final Color COLOR_ERROR  = new Color(180, 30, 30);
 
    /** Preset category options matching those in AddTransactionScreen. */
    private static final String[] CATEGORIES = {
        "Salary", "Food", "Rent", "Transportation",
        "Utilities", "Entertainment", "Healthcare", "Education", "Other"
    };
 
    // Backend 
    /** Database access object used to persist the updated transaction. */
    private final DatabaseManager dbManager;
 
    /** The transaction being edited. */
    private final Transaction transaction;
 
    /** Reference to the history screen, refreshed after a successful save. */
    private final TransactionHistoryScreen parentHistory;
 
    // UI components 
    /** Dropdown for selecting Income or Expense. */
    private JComboBox<String> typeCombo;
 
    /** Text field for the transaction amount. */
    private JTextField amountField;
 
    /** Dropdown for the transaction category. */
    private JComboBox<String> categoryCombo;
 
    /** Text field for the transaction date. */
    private JTextField dateField;
 
    /** Text area for the optional description. */
    private JTextArea descriptionArea;
 
    /** Label used to display validation error messages. */
    private JLabel errorLabel;
 
    /**
     * Constructs and displays the Edit Transaction screen pre-filled with the
     * given transaction's data.
     *
     * @param dbManager     the shared {@link DatabaseManager}; must not be null
     * @param transaction   the {@link Transaction} to edit; must not be null
     * @param parentHistory the {@link TransactionHistoryScreen} to refresh on success
     */
    public EditTransactionScreen(DatabaseManager dbManager, Transaction transaction,
                                  TransactionHistoryScreen parentHistory) {
        this.dbManager     = dbManager;
        this.transaction   = transaction;
        this.parentHistory = parentHistory;
        initWindow();
        buildUI();
        prefillFields();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Edit Transaction #" + transaction.getId());
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
     * Builds the header panel with a distinct orange colour to signal editing mode.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(COLOR_BRAND);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        JLabel title = new JLabel("Edit Transaction");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Update the details below and save");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 220, 180));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }
 
    /**
     * Builds the form panel with all input fields.
     *
     * @return a configured form {@link JPanel}
     */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_BG);
        form.setBorder(new EmptyBorder(20, 36, 10, 36));
 
        // Type 
        form.add(makeLabel("Transaction Type"));
        form.add(Box.createVerticalStrut(6));
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        typeCombo.setBackground(Color.WHITE);
        form.add(typeCombo);
 
        form.add(Box.createVerticalStrut(14));
 
        // Amount 
        form.add(makeLabel("Amount ($)"));
        form.add(Box.createVerticalStrut(6));
        amountField = makeTextField();
        form.add(amountField);
 
        form.add(Box.createVerticalStrut(14));
 
        // Category
        form.add(makeLabel("Category"));
        form.add(Box.createVerticalStrut(6));
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        categoryCombo.setBackground(Color.WHITE);
        form.add(categoryCombo);
 
        form.add(Box.createVerticalStrut(14));
 
        // Date 
        form.add(makeLabel("Date (YYYY-MM-DD)"));
        form.add(Box.createVerticalStrut(6));
        dateField = makeTextField();
        form.add(dateField);
 
        form.add(Box.createVerticalStrut(14));
 
        // Description 
        form.add(makeLabel("Description (optional)"));
        form.add(Box.createVerticalStrut(6));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        form.add(scroll);
 
        form.add(Box.createVerticalStrut(12));
 
        // Error label 
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(errorLabel);
 
        return form;
    }
 
    /**
     * Builds the bottom button panel with Save and Cancel buttons.
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
 
        JButton saveBtn = new JButton("Save Changes");
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
 
    // Pre-fill
 
    /**
     * Pre-fills all form fields with the existing transaction's data.
     * Called once after the UI is built.
     */
    private void prefillFields() {
        typeCombo.setSelectedItem(transaction.getType());
        amountField.setText(String.valueOf(transaction.getAmount()));
        categoryCombo.setSelectedItem(transaction.getCategory());
        dateField.setText(transaction.getDate().format(
                DateTimeFormatter.ofPattern(DATE_FORMAT)));
        if (transaction.getDescription() != null) {
            descriptionArea.setText(transaction.getDescription());
        }
    }
 
    // Event handlers 
 
    /**
     * Handles the Save Changes button. Validates all fields and updates the
     * transaction in the database if valid. Refreshes the history screen on success.
     *
     * @param event the action event fired by the Save button (unused)
     */
    private void handleSave(ActionEvent event) {
        String type        = (String) typeCombo.getSelectedItem();
        String amountText  = amountField.getText().trim();
        String category    = (String) categoryCombo.getSelectedItem();
        String dateText    = dateField.getText().trim();
        String description = descriptionArea.getText().trim();
 
        // Validate amount 
        if (amountText.isEmpty()) {
            errorLabel.setText("Amount is required.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a number (e.g. 250.00).");
            return;
        }
        if (amount <= 0) {
            errorLabel.setText("Amount must be greater than zero.");
            return;
        }
 
        // Validate date 
        LocalDate date;
        try {
            date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            errorLabel.setText("Date must be YYYY-MM-DD format.");
            return;
        }
 
        // Update in database 
        boolean success = dbManager.updateTransaction(
                transaction.getId(), type, amount, category, date,
                description.isEmpty() ? null : description);
 
        if (!success) {
            errorLabel.setText("Failed to update transaction. Please try again.");
            return;
        }
 
        JOptionPane.showMessageDialog(this,
            "Transaction updated successfully!",
            "Saved", JOptionPane.INFORMATION_MESSAGE);
 
        parentHistory.loadTransactions();
        dispose();
    }
 
    // UI utilities 
 
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
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return field;
    }
}
