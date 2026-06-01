package com.codecrafters.expensetracker.database;

import com.codecrafters.expensetracker.model.Transaction;
import com.codecrafters.expensetracker.model.User;

import java.util.ArrayList;

/**
 * Stub DatabaseManager for testing without a MySQL connection.
 * Replace with the real JDBC version when connecting to Hai's database.
 *
 * @author  Hai Huynh  (stub written by Omar Lorenzo Jimenez)
 * @version 1.0
 */
public class DatabaseManager {

    /** In-memory transaction store. */
    private final ArrayList<Transaction> store = new ArrayList<>();

    /** Auto-increment ID counter. */
    private int nextId = 1;

    /**
     * Validates login credentials against hardcoded test accounts.
     *
     * @param username the entered username
     * @param password the entered password
     * @return a {@link User} if credentials match, otherwise {@code null}
     */
    public User validateLogin(String username, String password) {
        if ("Omar123".equals(username) && "Pass123".equals(password))
            return new User(1, "Omar123", "omar@test.com", password, "user");
        if ("Haih".equals(username) && "Pass456".equals(password))
            return new User(2, "Haih", "hai@test.com", password, "user");
        if ("admin".equals(username) && "AdminPass1".equals(password))
            return new User(99, "admin", "admin@test.com", password, "admin");
        return null;
    }

    /**
     * Checks whether a username already exists.
     *
     * @param username the username to check
     * @return {@code true} if already taken
     */
    public boolean usernameExists(String username) {
        return "Omar123".equals(username) || "Haih".equals(username)
            || "admin".equals(username);
    }

    /**
     * Checks whether an email already exists.
     *
     * @param email the email to check
     * @return {@code true} if already taken
     */
    public boolean emailExists(String email) {
        return email != null && email.contains("@test.com");
    }

    /**
     * Inserts a new user — stub always succeeds.
     *
     * @param user the {@link User} to insert
     * @return {@code true} if inserted successfully
     */
    public boolean insertUser(User user) {
        if (usernameExists(user.getUsername())) return false;
        return true;
    }

    /**
     * Inserts a new transaction into the in-memory store.
     *
     * @param transaction the {@link Transaction} to insert
     */
    public void insertTransaction(Transaction transaction) {
        Transaction t = new Transaction(
            nextId++,
            transaction.getUserId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getCategory(),
            transaction.getDate(),
            transaction.getDescription()
        );
        store.add(t);
    }

    /**
     * Updates an existing transaction in the in-memory store.
     *
     * @param transaction the updated {@link Transaction}
     */
    public void updateTransaction(Transaction transaction) {
        for (Transaction t : store) {
            if (t.getId() == transaction.getId()) {
                t.setType(transaction.getType());
                t.setAmount(transaction.getAmount());
                t.setCategory(transaction.getCategory());
                t.setDate(transaction.getDate());
                t.setDescription(transaction.getDescription());
                return;
            }
        }
    }

    /**
     * Deletes a transaction from the in-memory store by ID.
     *
     * @param id the ID of the transaction to delete
     */
    public void deleteTransaction(int id) {
        store.removeIf(t -> t.getId() == id);
    }

    /**
     * Returns all transactions for the given user from the in-memory store.
     *
     * @param userId the user whose transactions to fetch
     * @return an {@link ArrayList} of matching transactions
     */
    public ArrayList<Transaction> fetchTransactions(int userId) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (Transaction t : store) {
            if (t.getUserId() == userId) result.add(t);
        }
        return result;
    }

    /**
     * Returns all transactions in the in-memory store.
     *
     * @return an {@link ArrayList} of all transactions
     */
    public ArrayList<Transaction> fetchAllTransactions() {
        return new ArrayList<>(store);
    }
    /**
     * Returns all registered users in the system.
     * Stub returns hardcoded test accounts.
     *
     * @return an {@link ArrayList} of all {@link User} objects
     */
    public ArrayList<User> fetchAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1,  "Omar123", "omar@test.com", "Pass123", "user"));
        users.add(new User(2,  "Haih",    "hai@test.com",  "Pass456", "user"));
        users.add(new User(99, "admin",   "admin@test.com","AdminPass1", "admin"));
        return users;
    }

    /**
     * Deletes a user and all their transactions from the system.
     * Stub removes their transactions from the in-memory store.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(int userId) {
        store.removeIf(t -> t.getUserId() == userId);
    }
}