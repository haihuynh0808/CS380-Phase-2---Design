package final_project;

import java.time.LocalDate;
import java.util.*;

/**
 * Simple driver class for testing Week 2 backend logic.
 */
public class Driver {

    /**
     * Main method for testing.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        TransactionManager transactionManager = new TransactionManager(db);
        SessionManager sessionManager = new SessionManager(db);

        // Example login test
        boolean loggedIn = sessionManager.login("Haih", "Pass456");
        System.out.println("Login success: " + loggedIn);

        if (!loggedIn) {
            System.out.println("Stop testing because login failed.");
            return;
        }

        User currentUser = sessionManager.getCurrentUser();
        System.out.println("Logged in as: " + currentUser);
        System.out.println("Landing route: " + sessionManager.getLandingRoute());

        // Add transaction test
        Transaction newTransaction = new Transaction(
                currentUser.getUserId(),
                "Expense",
                25.50,
                "Food",
                LocalDate.now(),
                "Lunch"
        );

        boolean addSuccess = transactionManager.addTransaction(newTransaction);
        System.out.println("Add transaction success: " + addSuccess);

        // View transactions
        ArrayList<Transaction> transactions = transactionManager.getAllTransactions(currentUser.getUserId());
        System.out.println("\nAll Transactions:");
        for (int i = 0; i < transactions.size(); i++) {
            System.out.println(transactions.get(i));
        }

        // Balance
        double balance = transactionManager.calculateBalance(currentUser.getUserId());
        System.out.println("\nBalance: $" + balance);

        // Category summary
        Map<String, Double> summary = transactionManager.getCategorySummary(currentUser.getUserId());
        System.out.println("\nCategory Summary:");
        ArrayList<String> categories = new ArrayList<String>(summary.keySet());
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            System.out.println(category + ": $" + summary.get(category));
        }

        // Logout
        sessionManager.logout();
        System.out.println("\nLogged out. Route now: " + sessionManager.getLandingRoute());
    }
}