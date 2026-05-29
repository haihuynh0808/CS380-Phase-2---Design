package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.manager.TransactionManager;
import com.codecrafters.expensetracker.model.Transaction;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
 
/**
 * Admin Edit Transaction screen — allows an administrator to modify
 * any transaction in the system regardless of which user owns it.
 *
 * <p>Pre-fills all fields from the selected {@link Transaction}. Calls
 * {@link TransactionManager#editTransaction(Transaction)} on save and
 * refreshes the parent {@link AdminAllTransactionsScreen}.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @version 1.0
 * @see     AdminAllTransactionsScreen
 */
public class AdminEditTransactionScreen extends JFrame {
 
    // ── UI constants ──────────────────────────────────────────────────────────
    /** Width of the window in pixels. */
    private static final int WINDOW_WIDTH  = 440;
 
    /** Height of the window in pixels. */
    private static final int WINDOW_HEIGHT = 520;
 
    /** Dark teal admin header colour. */
    private static final Color COLOR_BRAND = new Color(20, 60, 80);
 
    /** Green save button. */
    private static final Color COLOR_SAVE  = new Color(33, 115, 70);
 
    /** Light background. */
    private static final Color COLOR_BG    = new Color(240, 246, 248);
 
    /** Label text colour. */
    private static final Color COLOR_LABEL = new Color(30, 30, 30);
 
    /** Error message colour. */
    private static final Color COLOR_ERROR = new Color(180, 30, 30);
 
    /** Available category options. */
    private static final String[] CATEGORIES = {
        "Salary", "Food", "Rent", "Transportation",
        "Utilities", "Entertainment", "Healthcare", "Education", "Other"
    };
 
    // ── Backend ───────────────────────────────────────────────────────────────
    /** Database access object. */
    private final DatabaseManager dbManager;
 
    /** Transaction manager used to update the transaction. */
    private final TransactionManager transactionManager;
 
    /** The transaction being edited. */
    private final Transaction transaction;
 
    /** Parent screen refreshed on success. */
    private final AdminAllTransactionsScreen parentScreen;
 
    // ── UI components ─────────────────────────────────────────────────────────
    /** Type dropdown. */
    private JComboBox<String> typeCombo;
 
    /** Amount field. */
    private JTextField amountField;
 
    /** Category dropdown. */
    private JComboBox<String> categoryCombo;
 
    /** Date field. */
    private JTextField dateField;
 
    /** Description text area. */
    private JTextArea descriptionArea;
 
    /** Validation error label. */
    private JLabel errorLabel;
 
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Constructs and displays the Admin Edit Transaction screen.
     *
     * @param dbManager          the shared {@link DatabaseManager}
     * @param transactionManager the shared {@link TransactionManager}
     * @param transaction        the {@link Transaction} to edit
     * @param parentScreen       the {@link AdminAllTransactionsScreen} to refresh
     */
    public AdminEditTransactionScreen(DatabaseManager dbManager,
                                       TransactionManager transactionManager,
                                       Transaction transaction,
                                       AdminAllTransactionsScreen parentScreen) {
        this.dbManager          = dbManager;
        this.transactionManager = transactionManager;
        this.transaction        = transaction;
        this.parentScreen       = parentScreen;
        initWindow();
        buildUI();
        prefillFields();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Admin — Edit Transaction #" + transaction.getId()
                + "  (User " + transaction.getUserId() + ")");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
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
     * Builds the dark teal header panel.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(COLOR_BRAND);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));
 
        JLabel title = new JLabel("Edit Transaction  [Admin]");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Transaction #" + transaction.getId()
                + "  —  User ID: " + transaction.getUserId());
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(160, 210, 220));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }
 
    /**
     * Builds the form panel with all editable fields.
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
 
        form.add(Box.createVerticalStrut(10));
 
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(errorLabel);
 
        return form;
    }
 
    /**
     * Builds the bottom button panel with Save Changes and Cancel buttons.
     *
     * @return a configured button {@link JPanel}
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(0, 36, 20, 36));
 
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(160, 160, 160));
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
 
    // ── Pre-fill ──────────────────────────────────────────────────────────────
 
    /**
     * Pre-fills all form fields with the existing transaction's data.
     */
    private void prefillFields() {
        typeCombo.setSelectedItem(transaction.getType());
        amountField.setText(String.valueOf(transaction.getAmount()));
        categoryCombo.setSelectedItem(transaction.getCategory());
        dateField.setText(transaction.getDate());
        if (transaction.getDescription() != null) {
            descriptionArea.setText(transaction.getDescription());
        }
    }
 
    // ── Event handlers ────────────────────────────────────────────────────────
 
    /**
     * Validates all fields then calls
     * {@link TransactionManager#editTransaction(Transaction)} to save.
     * Refreshes the parent screen on success.
     *
     * @param event the action event (unused)
     */
    private void handleSave(ActionEvent event) {
        String type        = (String) typeCombo.getSelectedItem();
        String amountText  = amountField.getText().trim();
        String category    = (String) categoryCombo.getSelectedItem();
        String date        = dateField.getText().trim();
        String description = descriptionArea.getText().trim();
 
        if (amountText.isEmpty()) {
            errorLabel.setText("Amount is required.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a number.");
            return;
        }
        if (amount <= 0) {
            errorLabel.setText("Amount must be greater than zero.");
            return;
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            errorLabel.setText("Date must be YYYY-MM-DD format.");
            return;
        }
 
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDate(date);
        transaction.setDescription(description.isEmpty() ? null : description);
 
        transactionManager.editTransaction(transaction);
 
        JOptionPane.showMessageDialog(this,
            "Transaction updated successfully!",
            "Saved", JOptionPane.INFORMATION_MESSAGE);
 
        parentScreen.loadTransactions();
        dispose();
    }
 
    // ── UI utilities ──────────────────────────────────────────────────────────
 
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