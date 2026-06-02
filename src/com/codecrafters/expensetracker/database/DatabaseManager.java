package com.codecrafters.expensetracker.database;

import com.codecrafters.expensetracker.model.Transaction;
import com.codecrafters.expensetracker.model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * Handles all database operations for the Smart Expense Tracker.
 * Connects to MySQL via JDBC.
 *
 * @author  Hai Huynh
 * @version 1.0
 */
public class DatabaseManager {

    /** JDBC connection URL. */
    private String url      = "jdbc:mysql://localhost:3306/AppDB";

    /** MySQL username. */
    private String username = "root";

    /** MySQL password — update to match your local MySQL installation. */
    private String password = "Odiseo@2026#";

    /**
     * Connects to the MySQL database.
     *
     * @return a {@link Connection} if successful, otherwise {@code null}
     */
    public Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates login credentials against the users table.
     *
     * @param username the entered username
     * @param password the entered password
     * @return a {@link User} object if valid, otherwise {@code null}
     */
    public User validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("userId"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                rs.close(); ps.close(); con.close();
                return user;
            }
            rs.close(); ps.close(); con.close();
            return null;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks whether a username already exists in the database.
     *
     * @param username the username to check
     * @return {@code true} if already taken
     */
    public boolean usernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close(); ps.close(); con.close();
            return exists;
        } catch (Exception e) {
            System.out.println("Username check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether an email already exists in the database.
     *
     * @param email the email to check
     * @return {@code true} if already taken
     */
    public boolean emailExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close(); ps.close(); con.close();
            return exists;
        } catch (Exception e) {
            System.out.println("Email check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new user into the users table.
     *
     * @param user the {@link User} to insert
     * @return {@code true} if inserted successfully
     */
    public boolean insertUser(User user) {
        if (usernameExists(user.getUsername())) return false;
        if (emailExists(user.getEmail()))       return false;
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
            ps.close(); con.close();
            return true;
        } catch (Exception e) {
            System.out.println("Insert user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new transaction into the transactions table.
     *
     * @param transaction the {@link Transaction} to insert
     */
    public void insertTransaction(Transaction transaction) {
        String query = "INSERT INTO transactions (userId, type, amount, category, date, description) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,    transaction.getUserId());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getCategory());
            ps.setString(5, transaction.getDate());
            ps.setString(6, transaction.getDescription());
            ps.executeUpdate();
            ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Insert transaction failed: " + e.getMessage());
        }
    }

    /**
     * Updates an existing transaction in the transactions table.
     *
     * @param transaction the updated {@link Transaction}
     */
    public void updateTransaction(Transaction transaction) {
        String query = "UPDATE transactions SET type=?, amount=?, category=?, date=?, description=? WHERE id=?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transaction.getType());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getCategory());
            ps.setString(4, transaction.getDate());
            ps.setString(5, transaction.getDescription());
            ps.setInt(6,    transaction.getId());
            ps.executeUpdate();
            ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Update transaction failed: " + e.getMessage());
        }
    }

    /**
     * Deletes a transaction from the transactions table by ID.
     *
     * @param id the ID of the transaction to delete
     */
    public void deleteTransaction(int id) {
        String query = "DELETE FROM transactions WHERE id=?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Delete transaction failed: " + e.getMessage());
        }
    }

    /**
     * Fetches all transactions for a specific user ordered by date.
     *
     * @param userId the ID of the user
     * @return an {@link ArrayList} of {@link Transaction} objects
     */
    public ArrayList<Transaction> fetchTransactions(int userId) {
        ArrayList<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE userId=? ORDER BY date";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("id"), userId,
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("date"),
                    rs.getString("description")
                ));
            }
            rs.close(); ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Fetch transactions failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fetches all transactions in the database across all users.
     *
     * @return an {@link ArrayList} of all {@link Transaction} objects
     */
    public ArrayList<Transaction> fetchAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY userId, date";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("id"),
                    rs.getInt("userId"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("date"),
                    rs.getString("description")
                ));
            }
            rs.close(); ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Fetch all transactions failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fetches all registered users from the database.
     *
     * @return an {@link ArrayList} of all {@link User} objects
     */
    public ArrayList<User> fetchAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        String query = "SELECT * FROM users";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("userId"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
            rs.close(); ps.close(); con.close();
        } catch (Exception e) {
            System.out.println("Fetch all users failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Deletes a user and all their transactions from the database.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(int userId) {
        try {
            Connection con = connect();
            PreparedStatement ps1 = con.prepareStatement(
                "DELETE FROM transactions WHERE userId=?");
            ps1.setInt(1, userId);
            ps1.executeUpdate();
            ps1.close();
            PreparedStatement ps2 = con.prepareStatement(
                "DELETE FROM users WHERE userId=?");
            ps2.setInt(1, userId);
            ps2.executeUpdate();
            ps2.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Delete user failed: " + e.getMessage());
        }
    }
}