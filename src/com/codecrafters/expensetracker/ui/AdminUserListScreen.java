package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.manager.TransactionManager;
import com.codecrafters.expensetracker.model.User;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
 
/**
 * Admin screen displaying all registered users in the system.
 *
 * <p>Allows the administrator to view every user account and delete
 * selected entries for testing and maintenance purposes. Calls
 * {@link DatabaseManager#fetchAllUsers()} to load the data.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @version 1.0
 * @see     AdminDashboard
 */
public class AdminUserListScreen extends JFrame {
 
    // ── UI constants ──────────────────────────────────────────────────────────
    /** Width of the window in pixels. */
    private static final int WINDOW_WIDTH  = 620;
 
    /** Height of the window in pixels. */
    private static final int WINDOW_HEIGHT = 480;
 
    /** Admin teal header colour. */
    private static final Color COLOR_NAVBAR = new Color(20, 60, 80);
 
    /** Light background. */
    private static final Color COLOR_BG     = new Color(240, 246, 248);
 
    /** Column names for the users table. */
    private static final String[] COLUMNS = { "ID", "Username", "Email", "Role" };
 
    // ── Backend ───────────────────────────────────────────────────────────────
    /** Database access object used to fetch and delete users. */
    private final DatabaseManager dbManager;
 
    /** Transaction manager passed back to parent dashboard. */
    private final TransactionManager transactionManager;
 
    // ── UI components ─────────────────────────────────────────────────────────
    /** Table model backing the users table. */
    private DefaultTableModel tableModel;
 
    /** The users table. */
    private JTable table;
 
    /** Label showing user count. */
    private JLabel countLabel;
 
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Constructs and displays the Admin User List screen.
     *
     * @param dbManager          the shared {@link DatabaseManager}
     * @param transactionManager the shared {@link TransactionManager}
     */
    public AdminUserListScreen(DatabaseManager dbManager,
                                TransactionManager transactionManager) {
        this.dbManager          = dbManager;
        this.transactionManager = transactionManager;
        initWindow();
        buildUI();
        loadUsers();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Admin — User Management");
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
     * Builds the header panel with title and user count.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_NAVBAR);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
 
        JLabel title = new JLabel("User Management");
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
     * Builds the centre panel with the scrollable users table.
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
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(190, 210, 220)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
 
    /**
     * Builds the bottom button panel with Delete and Close buttons.
     *
     * @return a configured button {@link JPanel}
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(0, 16, 4, 16));
 
        JButton deleteBtn = makeButton("Delete User", new Color(160, 30, 30));
        JButton closeBtn  = makeButton("Close",       new Color(100, 100, 100));
 
        deleteBtn.addActionListener(e -> handleDelete());
        closeBtn.addActionListener(e  -> dispose());
 
        panel.add(deleteBtn);
        panel.add(closeBtn);
        return panel;
    }
 
    // ── Data loading ──────────────────────────────────────────────────────────
 
    /**
     * Fetches all users from the database and populates the table.
     */
    public void loadUsers() {
        tableModel.setRowCount(0);
        ArrayList<User> users = dbManager.fetchAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole()
            });
        }
        countLabel.setText(users.size() + " user(s)");
    }
 
    // ── Event handlers ────────────────────────────────────────────────────────
 
    /**
     * Prompts for confirmation then deletes the selected user via
     * {@link DatabaseManager#deleteUser(int)}.
     */
    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role     = (String) tableModel.getValueAt(selectedRow, 3);
 
        if ("admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete an admin account.",
                "Not Allowed", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + username + "\" and all their transactions?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            dbManager.deleteUser(userId);
            loadUsers();
            JOptionPane.showMessageDialog(this,
                "User deleted successfully.",
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
        btn.setPreferredSize(new Dimension(120, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}