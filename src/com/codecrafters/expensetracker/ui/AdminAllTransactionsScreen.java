package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.manager.TransactionManager;
import com.codecrafters.expensetracker.model.Transaction;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
 
/**
 * Admin screen displaying all transactions from every user in the system.
 *
 * <p>Allows the administrator to view, edit, and delete any transaction
 * for testing and maintenance purposes. Calls
 * {@link DatabaseManager#fetchAllTransactions()} to load all records.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @version 1.0
 * @see     AdminDashboard
 * @see     AdminEditTransactionScreen
 */
public class AdminAllTransactionsScreen extends JFrame {
 
    // ── UI constants ──────────────────────────────────────────────────────────
    /** Width of the window in pixels. */
    private static final int WINDOW_WIDTH  = 760;
 
    /** Height of the window in pixels. */
    private static final int WINDOW_HEIGHT = 520;
 
    /** Admin teal header colour. */
    private static final Color COLOR_NAVBAR  = new Color(20, 60, 80);
 
    /** Light background. */
    private static final Color COLOR_BG      = new Color(240, 246, 248);
 
    /** Green for income rows. */
    private static final Color COLOR_INCOME  = new Color(230, 247, 236);
 
    /** Red for expense rows. */
    private static final Color COLOR_EXPENSE = new Color(253, 235, 235);
 
    /** Column headers for the transactions table. */
    private static final String[] COLUMNS =
            { "ID", "User ID", "Date", "Type", "Category", "Amount", "Description" };
 
    // ── Backend ───────────────────────────────────────────────────────────────
    /** Database access object used to fetch and delete transactions. */
    private final DatabaseManager dbManager;
 
    /** Transaction manager passed to edit screen. */
    private final TransactionManager transactionManager;
 
    // ── UI components ─────────────────────────────────────────────────────────
    /** Table model backing the transactions table. */
    private DefaultTableModel tableModel;
 
    /** The transactions table. */
    private JTable table;
 
    /** Label showing transaction count. */
    private JLabel countLabel;
 
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Constructs and displays the Admin All Transactions screen.
     *
     * @param dbManager          the shared {@link DatabaseManager}
     * @param transactionManager the shared {@link TransactionManager}
     */
    public AdminAllTransactionsScreen(DatabaseManager dbManager,
                                       TransactionManager transactionManager) {
        this.dbManager          = dbManager;
        this.transactionManager = transactionManager;
        initWindow();
        buildUI();
        loadTransactions();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Admin — All Transactions");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Constructs and arranges all UI components.
     */
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),      BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }
 
    /**
     * Builds the header panel with title and count label.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_NAVBAR);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
 
        JLabel title = new JLabel("All Transactions");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
 
        countLabel = new JLabel("Loading...");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(160, 210, 220));
 
        header.add(title,      BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }
 
    /**
     * Builds the centre panel with the scrollable colour-coded transactions table.
     *
     * @return a configured table {@link JPanel}
     */
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 16, 0, 16));
 
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
 
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(COLOR_NAVBAR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(210, 225, 230));
 
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
 
        // Colour-code by type
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String type = (String) tableModel.getValueAt(row, 3);
                    c.setBackground("Income".equals(type)
                            ? COLOR_INCOME : COLOR_EXPENSE);
                }
                return c;
            }
        });
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(190, 210, 220)));
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
        JButton deleteBtn = makeButton("Delete", new Color(160, 30, 30));
        JButton closeBtn  = makeButton("Close",  new Color(100, 100, 100));
 
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        closeBtn.addActionListener(e  -> dispose());
 
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(closeBtn);
        return panel;
    }
 
    // ── Data loading ──────────────────────────────────────────────────────────
 
    /**
     * Fetches all transactions from the database via
     * {@link DatabaseManager#fetchAllTransactions()} and populates the table.
     */
    public void loadTransactions() {
        tableModel.setRowCount(0);
        ArrayList<Transaction> transactions = dbManager.fetchAllTransactions();
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getUserId(),
                t.getDate(),
                t.getType(),
                t.getCategory(),
                String.format("$%.2f", t.getAmount()),
                t.getDescription() != null ? t.getDescription() : ""
            });
        }
        countLabel.setText(transactions.size() + " transaction(s)");
    }
 
    // ── Event handlers ────────────────────────────────────────────────────────
 
    /**
     * Opens the {@link AdminEditTransactionScreen} for the selected row.
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
        ArrayList<Transaction> all = dbManager.fetchAllTransactions();
 
        Transaction selected = null;
        for (Transaction t : all) {
            if (t.getId() == transactionId) { selected = t; break; }
        }
 
        if (selected != null) {
            new AdminEditTransactionScreen(dbManager, transactionManager,
                    selected, this);
        }
    }
 
    /**
     * Prompts for confirmation then deletes the selected transaction via
     * {@link TransactionManager#deleteTransaction(int)}.
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
            "Delete this transaction? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            transactionManager.deleteTransaction(transactionId);
            loadTransactions();
            JOptionPane.showMessageDialog(this,
                "Transaction deleted.",
                "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }
 
    // ── UI utility ────────────────────────────────────────────────────────────
 
    /**
     * Creates a styled action button.
     *
     * @param text  the button label
     * @param color the background colour
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
