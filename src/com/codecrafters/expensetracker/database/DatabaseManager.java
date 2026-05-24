package com.codecrafters.expensetracker.database;
 
import com.codecrafters.expensetracker.model.Transaction;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Stub implementation of DatabaseManager used for UI development and testing.
 *
 * <p><b>NOTE TO HAI:</b> Replace this file with the real JDBC implementation.
 * This stub keeps an in-memory list so the full user flow can be tested without
 * a database connection.</p>
 *
 * @author  Hai Huynh  (stub written by Omar Lorenzo Jimenez)
 * @version 1.0-stub
 */
public class DatabaseManager {
 
    /** In-memory transaction store used by the stub. */
    private final List<Transaction> transactionStore = new ArrayList<>();
 
    /** Auto-incrementing ID counter for stub transactions. */
    private int nextId = 1;
 
    /**
     * Validates login credentials against the database.
     *
     * @param username the entered username
     * @param password the entered password
     * @param role     either {@code "USER"} or {@code "ADMIN"}
     * @return {@code true} if credentials match a record with that role
     */
    public boolean validateLogin(String username, String password, String role) {
        if ("USER".equals(role)) {
            return ("Omar123".equals(username) && "Pass123".equals(password))
                || ("Haih".equals(username)    && "Pass456".equals(password));
        }
        if ("ADMIN".equals(role)) {
            return "admin".equals(username) && "AdminPass1".equals(password);
        }
        return false;
    }
 
    /**
     * Returns the database-assigned user ID for the given username.
     *
     * @param username the username to look up
     * @return the integer user ID, or {@code -1} if not found
     */
    public int getUserId(String username) {
        if ("Omar123".equals(username)) return 1;
        if ("Haih".equals(username))    return 2;
        if ("admin".equals(username))   return 99;
        return -1;
    }
 
    /**
     * Inserts a new user record into the users table.
     *
     * @param username the desired username; must be unique
     * @param password the user's password
     * @return {@code true} if the record was created successfully
     */
    public boolean insertUser(String username, String password) {
        return !usernameExists(username);
    }
 
    /**
     * Checks whether a username already exists in the database.
     *
     * @param username the username to check
     * @return {@code true} if the username is already taken
     */
    public boolean usernameExists(String username) {
        return "Omar123".equals(username) || "Haih".equals(username)
            || "admin".equals(username);
    }
 
    /**
     * Inserts a new transaction record into the transactions table.
     *
     * @param userId      the ID of the owning user
     * @param type        {@code "Income"} or {@code "Expense"}
     * @param amount      positive transaction amount
     * @param category    spending or income category label
     * @param date        date the transaction occurred
     * @param description optional note; may be {@code null}
     * @return {@code true} if the record was inserted successfully
     */
    public boolean insertTransaction(int userId, String type, double amount,
                                     String category, LocalDate date,
                                     String description) {
        Transaction t = new Transaction(nextId++, userId, type, amount,
                                        category, date, description);
        transactionStore.add(t);
        return true;
    }
 
    /**
     * Fetches all transactions belonging to the specified user.
     *
     * @param userId the user whose transactions are fetched
     * @return a list of {@link Transaction} objects; empty list if none found
     */
    public List<Transaction> fetchTransactions(int userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactionStore) {
            if (t.getUserId() == userId) {
                result.add(t);
            }
        }
        return result;
    }
 
    /**
     * Updates an existing transaction record in the database.
     *
     * @param id          the ID of the transaction to update
     * @param type        the new type: "Income" or "Expense"
     * @param amount      the new amount
     * @param category    the new category
     * @param date        the new date
     * @param description the new description; may be {@code null}
     * @return {@code true} if the record was updated successfully
     */
    public boolean updateTransaction(int id, String type, double amount,
                                     String category, LocalDate date,
                                     String description) {
        for (Transaction t : transactionStore) {
            if (t.getId() == id) {
                t.setType(type);
                t.setAmount(amount);
                t.setCategory(category);
                t.setDate(date);
                t.setDescription(description);
                return true;
            }
        }
        return false;
    }
 
    /**
     * Deletes a transaction record from the database.
     *
     * @param id the ID of the transaction to delete
     * @return {@code true} if the record was deleted successfully
     */
    public boolean deleteTransaction(int id) {
        return transactionStore.removeIf(t -> t.getId() == id);
    }
}
