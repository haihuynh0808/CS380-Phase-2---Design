package com.codecrafters.expensetracker.ui;
 
import com.codecrafters.expensetracker.database.DatabaseManager;
import com.codecrafters.expensetracker.model.SessionManager;
import com.codecrafters.expensetracker.model.Transaction;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
 
/**
 * Category Summary screen showing the user's spending and income
 * grouped by category.
 *
 * <p>Displays two sections: an Expenses breakdown and an Income breakdown,
 * each showing category totals and a simple visual bar proportional to
 * the amount. A net balance summary is shown at the bottom.</p>
 *
 * @author  Omar Lorenzo Jimenez
 * @see     MainDashboard
 */
public class CategorySummaryScreen extends JFrame {
 
    // UI constants 
    /** Width of the category summary window in pixels. */
    private static final int WINDOW_WIDTH  = 500;
 
    /** Height of the category summary window in pixels. */
    private static final int WINDOW_HEIGHT = 560;
 
    /** Header background colour. */
    private static final Color COLOR_BRAND    = new Color(38, 109, 80);
 
    /** Light background colour. */
    private static final Color COLOR_BG       = new Color(245, 248, 252);
 
    /** Green used for income bars and totals. */
    private static final Color COLOR_INCOME   = new Color(33, 115, 70);
 
    /** Red used for expense bars and totals. */
    private static final Color COLOR_EXPENSE  = new Color(180, 30, 30);
 
    /** Bar fill colour for expense rows. */
    private static final Color COLOR_BAR_EXP  = new Color(240, 180, 180);
 
    /** Bar fill colour for income rows. */
    private static final Color COLOR_BAR_INC  = new Color(180, 230, 195);
 
    // Backend 
    /** Database access object used to fetch transactions. */
    private final DatabaseManager dbManager;
 
    /**
     * Constructs and displays the Category Summary screen.
     *
     * @param dbManager the shared {@link DatabaseManager}; must not be null
     */
    public CategorySummaryScreen(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initWindow();
        buildUI();
        setVisible(true);
    }
 
    /**
     * Configures top-level window properties.
     */
    private void initWindow() {
        setTitle("Category Summary");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
    }
 
    /**
     * Builds and arranges all UI components. Fetches transaction data and
     * computes category totals before rendering.
     */
    private void buildUI() {
        // Compute category totals 
        int userId = SessionManager.getCurrentUserId();
        List<Transaction> transactions = dbManager.fetchTransactions(userId);
 
        Map<String, Double> expenseMap = new LinkedHashMap<>();
        Map<String, Double> incomeMap  = new LinkedHashMap<>();
        double totalIncome   = 0;
        double totalExpenses = 0;
 
        for (Transaction t : transactions) {
            if ("Expense".equals(t.getType())) {
                expenseMap.merge(t.getCategory(), t.getAmount(), Double::sum);
                totalExpenses += t.getAmount();
            } else {
                incomeMap.merge(t.getCategory(), t.getAmount(), Double::sum);
                totalIncome += t.getAmount();
            }
        }
 
        // Layout 
        setLayout(new BorderLayout());
        add(buildHeader(),                                   BorderLayout.NORTH);
        add(buildScrollContent(expenseMap, incomeMap,
                               totalExpenses, totalIncome),  BorderLayout.CENTER);
        add(buildFooter(totalIncome, totalExpenses),         BorderLayout.SOUTH);
    }
 
    /**
     * Builds the branded header panel.
     *
     * @return a configured header {@link JPanel}
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(COLOR_BRAND);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));
 
        JLabel title = new JLabel("Category Summary");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Spending and income broken down by category");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 230, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }
 
    /**
     * Builds the scrollable centre panel containing both the Expenses and
     * Income category breakdowns.
     *
     * @param expenseMap   map of expense category name to total amount
     * @param incomeMap    map of income category name to total amount
     * @param totalExp     total expenses across all categories
     * @param totalInc     total income across all categories
     * @return a configured scrollable centre {@link JPanel}
     */
    private JScrollPane buildScrollContent(Map<String, Double> expenseMap,
                                            Map<String, Double> incomeMap,
                                            double totalExp, double totalInc) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));
 
        // Expenses section
        content.add(makeSectionLabel("Expenses", COLOR_EXPENSE));
        content.add(Box.createVerticalStrut(8));
        if (expenseMap.isEmpty()) {
            content.add(makeEmptyNote("No expense transactions recorded."));
        } else {
            for (Map.Entry<String, Double> entry : expenseMap.entrySet()) {
                content.add(makeCategoryRow(entry.getKey(), entry.getValue(),
                        totalExp, COLOR_BAR_EXP, COLOR_EXPENSE));
                content.add(Box.createVerticalStrut(6));
            }
        }
 
        content.add(Box.createVerticalStrut(20));
 
        // Income section
        content.add(makeSectionLabel("Income", COLOR_INCOME));
        content.add(Box.createVerticalStrut(8));
        if (incomeMap.isEmpty()) {
            content.add(makeEmptyNote("No income transactions recorded."));
        } else {
            for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
                content.add(makeCategoryRow(entry.getKey(), entry.getValue(),
                        totalInc, COLOR_BAR_INC, COLOR_INCOME));
                content.add(Box.createVerticalStrut(6));
            }
        }
 
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }
 
    /**
     * Builds the bottom footer panel showing the balance summary.
     *
     * @param totalIncome   total income amount
     * @param totalExpenses total expenses amount
     * @return a configured footer {@link JPanel}
     */
    private JPanel buildFooter(double totalIncome, double totalExpenses) {
        double balance = totalIncome - totalExpenses;
 
        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setBackground(new Color(230, 236, 244));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 215, 230)),
            new EmptyBorder(12, 20, 12, 20)));
 
        footer.add(makeSummaryBox("Total Income",   String.format("$%.2f", totalIncome),   COLOR_INCOME));
        footer.add(makeSummaryBox("Total Expenses", String.format("$%.2f", totalExpenses), COLOR_EXPENSE));
        footer.add(makeSummaryBox("Net Balance",    String.format("$%.2f", balance),
                balance >= 0 ? COLOR_INCOME : COLOR_EXPENSE));
 
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.add(footer, BorderLayout.CENTER);
 
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(new Color(100, 100, 100));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        closeBtn.addActionListener(e -> dispose());
 
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(COLOR_BG);
        btnRow.add(closeBtn);
        wrapper.add(btnRow, BorderLayout.SOUTH);
        return wrapper;
    }
 
    // Component builders
 
    /**
     * Creates a section heading label (e.g. "Expenses" or "Income").
     *
     * @param text  the heading text
     * @param color the text colour
     * @return a configured {@link JLabel}
     */
    private JLabel makeSectionLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
 
    /**
     * Creates a category row showing the category name, a proportional bar,
     * and the total amount.
     *
     * @param category  the category name
     * @param amount    the total amount for this category
     * @param total     the grand total for this type (used for bar proportion)
     * @param barColor  the bar fill colour
     * @param textColor the amount text colour
     * @return a configured category row {@link JPanel}
     */
    private JPanel makeCategoryRow(String category, double amount,
                                    double total, Color barColor, Color textColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        // Category name
        JLabel nameLabel = new JLabel(category);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setPreferredSize(new Dimension(110, 20));
 
        // Proportional bar
        int barWidth = total > 0 ? (int) ((amount / total) * 160) : 0;
        JPanel barContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barContainer.setBackground(Color.WHITE);
        JPanel bar = new JPanel();
        bar.setBackground(barColor);
        bar.setPreferredSize(new Dimension(Math.max(barWidth, 4), 14));
        barContainer.add(bar);
 
        // Amount
        JLabel amountLabel = new JLabel(String.format("$%.2f", amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        amountLabel.setForeground(textColor);
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        amountLabel.setPreferredSize(new Dimension(90, 20));
 
        row.add(nameLabel,     BorderLayout.WEST);
        row.add(barContainer,  BorderLayout.CENTER);
        row.add(amountLabel,   BorderLayout.EAST);
        return row;
    }
 
    /**
     * Creates a summary box for the footer showing a label and value.
     *
     * @param label     the stat label
     * @param value     the formatted value string
     * @param valueColor the colour for the value text
     * @return a configured summary box {@link JPanel}
     */
    private JPanel makeSummaryBox(String label, String value, Color valueColor) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(230, 236, 244));
 
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.PLAIN, 11));
        labelComp.setForeground(new Color(80, 80, 80));
        labelComp.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.BOLD, 16));
        valueComp.setForeground(valueColor);
        valueComp.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        box.add(labelComp);
        box.add(Box.createVerticalStrut(2));
        box.add(valueComp);
        return box;
    }
 
    /**
     * Creates a placeholder note shown when a category section has no data.
     *
     * @param message the message to display
     * @return a configured {@link JLabel}
     */
    private JLabel makeEmptyNote(String message) {
        JLabel note = new JLabel(message);
        note.setFont(new Font("Arial", Font.ITALIC, 12));
        note.setForeground(new Color(150, 150, 150));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        return note;
    }
}
