package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
import com.codecrafters.expensetracker.model.Transaction;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
 
/**
 * Transaction History screen showing all transactions for the logged-in user.
 *
 * <p>Displays transactions in a sortable table with columns for Date, Type,
 * Category, Amount, and Description. The user can select any row and use the
 * Edit or Delete buttons to modify or remove it.</p>
 *
 * <p>The table refreshes automatically when this screen is opened and after
 * any edit or delete operation.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @version 1.0
 * @see     MainDashboard
 * @see     EditTransactionScreen
 */
public class TransactionHistoryScreen extends JFrame {
 
    // UI constants 
    /** Width of the transaction history window in pixels. */
    private static final int WINDOW_WIDTH  = 680;
 
    /** Height of the transaction history window in pixels. */
    private static final int WINDOW_HEIGHT = 520;
 
    /** Header colour matching the main brand. */
    private static final Color COLOR_BRAND    = new Color(31, 78, 121);
 
    /** Background colour for the screen body. */
    private static final Color COLOR_BG       = new Color(245, 248, 252);
 
    /** Green used for income rows in the table. */
    private static final Color COLOR_INCOME   = new Color(230, 247, 236);
 
    /** Red used for expense rows in the table. */
    private static final Color COLOR_EXPENSE  = new Color(253, 235, 235);
 
    /** Column names for the transactions table. */
    private static final String[] COLUMNS = { "#", "Date", "Type", "Category", "Amount", "Description" };
 
    // Backend 
    /** Database access object used to fetch, update, and delete transactions. */
    private final DatabaseManager dbManager;
 
    /** Reference to the parent dashboard, refreshed after any change. */
    private final MainDashboard parentDashboard;
 
    // UI components 
    /** Table model backing the transactions table. */
    private DefaultTableModel tableModel;
 
    /** The transactions table component. */
    private JTable table;
 
    /** Label showing the count of displayed transactions. */
    private JLabel countLabel;
 
    /**
     * Constructs and displays the Transaction History screen.
     *
     * @param dbManager       the shared {@link DatabaseManager}; must not be null
     * @param parentDashboard the {@link MainDashboard} to refresh after changes
     */
    public TransactionHistoryScreen(DatabaseManager dbManager, MainDashboard parentDashboard) {
        this.dbManager       = dbManager;
        this.parentDashboard = parentDashboard;
        initWindow();
        buildUI();
        loadTransactions();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Transaction History");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Builds and arranges all UI components.
     */
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }
 
    /**
     * Builds the branded header panel with the screen title and transaction count.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BRAND);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
 
        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
 
        countLabel = new JLabel("Loading...");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(180, 210, 240));
 
        header.add(title,      BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }
 
    /**
     * Builds the centre panel containing the scrollable transactions table.
     * Rows are colour-coded green for Income and red for Expense.
     *
     * @return a configured table {@link JPanel}
     */
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 16, 0, 16));
 
        // Non-editable table model
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
 
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(31, 78, 121));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 230, 240));
 
        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(40);   // #
        table.getColumnModel().getColumn(1).setPreferredWidth(90);  // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(110); // Category
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Amount
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Description
 
        // Colour-code rows by transaction type
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String type = (String) tableModel.getValueAt(row, 2);
                    c.setBackground("Income".equals(type) ? COLOR_INCOME : COLOR_EXPENSE);
                }
                return c;
            }
        });
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 230)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
 
    /**
     * Builds the bottom button panel with Edit, Delete, and Close buttons.
     *
     * @return a configured button {@link JPanel}
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(0, 16, 4, 16));
 
        JButton editBtn   = makeButton("Edit",   new Color(46, 117, 182));
        JButton deleteBtn = makeButton("Delete", new Color(180, 30, 30));
        JButton closeBtn  = makeButton("Close",  new Color(120, 120, 120));
 
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        closeBtn.addActionListener(e  -> dispose());
 
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(closeBtn);
        return panel;
    }
 
    // Data loading 
 
    /**
     * Fetches all transactions for the current user from the database and
     * populates the table. Also updates the transaction count label and
     * refreshes the balance on the parent dashboard.
     */
    public void loadTransactions() {
        tableModel.setRowCount(0);
        int userId = SessionManager.getCurrentUserId();
        List<Transaction> transactions = dbManager.fetchTransactions(userId);
 
        double balance = 0;
        for (Transaction t : transactions) {
            String amount = String.format("$%.2f", t.getAmount());
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getDate().toString(),
                t.getType(),
                t.getCategory(),
                amount,
                t.getDescription() != null ? t.getDescription() : ""
            });
            if ("Income".equals(t.getType()))  balance += t.getAmount();
            if ("Expense".equals(t.getType())) balance -= t.getAmount();
        }
 
        countLabel.setText(transactions.size() + " transaction(s)");
        parentDashboard.refreshBalance(balance);
    }
 
    // Event handlers 
 
    /**
     * Opens the {@link EditTransactionScreen} for the currently selected row.
     * Shows a warning if no row is selected.
     */
    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a transaction to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Transaction> all = dbManager.fetchTransactions(SessionManager.getCurrentUserId());
        Transaction selected = null;
        for (Transaction t : all) {
            if (t.getId() == transactionId) { selected = t; break; }
        }
 
        if (selected != null) {
            new EditTransactionScreen(dbManager, selected, this);
        }
    }
 
    /**
     * Prompts the user for confirmation then deletes the selected transaction.
     * Refreshes the table and balance after deletion.
     */
    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a transaction to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this transaction?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean success = dbManager.deleteTransaction(transactionId);
            if (success) {
                loadTransactions();
                JOptionPane.showMessageDialog(this,
                    "Transaction deleted successfully.",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete transaction.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    // UI utility 
 
    /**
     * Creates a styled button for the bottom action panel.
     *
     * @param text  the button label
     * @param color the background fill colour
     * @return a configured {@link JButton}
     */
    private JButton makeButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
