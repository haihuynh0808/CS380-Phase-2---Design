package final_project;

import java.util.*;

/**
 * Handles the main transaction logic for the Smart Expense Tracker application.
 * 
 * This class works with DatabaseManager to add, edit, delete, retrieve,
 * and summarize transactions.
 */
public class TransactionManager {

    // The database manager used to access stored transaction data. 
    private DatabaseManager db;

    /**
     * Creates a TransactionManager object.
     *
     * @param db the DatabaseManager object used for database operations
     */
    public TransactionManager(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Adds a transaction to the database.
     *
     * @param transaction the transaction to add
     * @return true if successful, otherwise false
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        if (transaction.getAmount() < 0) {
            return false;
        }

        if (!transaction.getType().equalsIgnoreCase("Income")
                && !transaction.getType().equalsIgnoreCase("Expense")) {
            return false;
        }

        return db.insertTransaction(transaction);
    }

    /**
     * Updates an existing transaction in the database.
     *
     * @param transaction the updated transaction
     * @return true if successful, otherwise false
     */
    public boolean editTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        if (transaction.getId() <= 0) {
            return false;
        }

        if (transaction.getAmount() < 0) {
            return false;
        }

        return db.updateTransaction(transaction);
    }
    
    /**
     * Deletes a transaction from the database by its ID.
     *
     * @param id the ID of the transaction to delete
     * @return true if successful, otherwise false
     */
    public boolean deleteTransaction(int id) {
        if (id <= 0) {
            return false;
        }

        return db.deleteTransaction(id);
    }

    /**
     * Returns all transactions for a given user.
     *
     * @param userId the ID of the user
     * @return a list of transactions for that user
     */
    public ArrayList<Transaction> getAllTransactions(int userId) {
        return db.fetchTransactions(userId);
    }

    /**
     * Calculates the current balance for a user.
     * 
     * Income amounts are added to the balance.
     * Expense amounts are subtracted from the balance.
     *
     * @param userId the ID of the user
     * @return the calculated balance
     */
    public double calculateBalance(int userId) {
        ArrayList<Transaction> transactions = db.fetchTransactions(userId);
        double balance = 0.0;

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            
            if (transaction.getType().equalsIgnoreCase("Income")) {
                balance = balance + transaction.getAmount();
            } else if (transaction.getType().equalsIgnoreCase("Expense")) {
                balance = balance - transaction.getAmount();
            }
        }

        return balance;
    }

    /**
     * Calculates the total spending amount for each expense category.
     * 
     * Only transactions with type "Expense" are included in the summary.
     *
     * @param userId the ID of the user
     * @return a map where the key is the category and the value is the total spent
     */
    public Map<String, Double> getCategorySummary(int userId) {
        ArrayList<Transaction> transactions = db.fetchTransactions(userId);
        Map<String, Double> summary = new HashMap<String, Double>();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            
            if (transaction.getType().equalsIgnoreCase("Expense")) {
                String category = transaction.getCategory();
                double amount = transaction.getAmount();

                if (summary.containsKey(category)) {
                    double oldAmount = summary.get(category);
                    summary.put(category, oldAmount + amount);
                } else {
                    summary.put(category, amount);
                }
            }
        }

        return summary;
    }
    
    /**
     * Returns one transaction by ID.
     *
     * @param id the transaction ID
     * @return the transaction if found, otherwise null
     */
    public Transaction getTransactionById(int id) {
        return db.fetchTransactionById(id);
    }

    /**
     * Prints all transactions for a given user to the console.
     *
     * @param userId the ID of the user
     */
    public void printAllTransactions(int userId) {
        ArrayList<Transaction> transactions = db.fetchTransactions(userId);

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            
            System.out.println(transaction);
        }
    }

    /**
     * Prints the category summary for a given user to the console.
     *
     * @param userId the ID of the user
     */
    public void printCategorySummary(int userId) {
        Map<String, Double> summary = getCategorySummary(userId);
        
        ArrayList<String> categories = new ArrayList<String>(summary.keySet());

        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            System.out.println(category + ": $" + summary.get(category));
        }
    }
}