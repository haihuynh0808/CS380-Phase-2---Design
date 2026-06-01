package final_project;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;

/**
 * Handles database operations for the Smart Expense Tracker application.
 *
 * This class connects to MySQL and performs actions such as:
 * validating login, inserting users, inserting transactions,
 * updating transactions, deleting transactions, and fetching transactions.
 */
public class DatabaseManager {
 
    private String url = "jdbc:mysql://localhost:3306/OHKDB";
    private String username = "root";
    private String password = "cs380";

    /**
     * Connects to the MySQL database.
     *
     * @return a Connection object if successful, otherwise null
     */
    public Connection connect() {
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database.");
            return con;
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks whether a username already exists in the users table.
     *
     * @param username the username to check
     * @return true if the username already exists, otherwise false
     */
    public boolean usernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            rs.close();
            ps.close();
            con.close();

            return exists;
        } catch (Exception e) {
            System.out.println("Username check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether an email already exists in the users table.
     *
     * @param email the email to check
     * @return true if the email already exists, otherwise false
     */
    public boolean emailExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            rs.close();
            ps.close();
            con.close();

            return exists;
        } catch (Exception e) {
            System.out.println("Email check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new user into the users table.
     *
     * @param user the User object to insert
     * @return true if the user was inserted successfully, otherwise false
     */
    public boolean insertUser(User user) {
    	if (user == null) {
    		return false;
    	}
    	
        if (usernameExists(user.getUsername())) {
            System.out.println("Username already exists.");
            return false;
        }

        if (emailExists(user.getEmail())) {
            System.out.println("Email already exists.");
            return false;
        }

        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            return rows > 0;
        } catch (Exception e) {
            System.out.println("Insert user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates a login by checking the username and password in the users table.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return a User object if login is successful, otherwise null
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
                int userId = rs.getInt("userId");
                String email = rs.getString("email");
                String role = rs.getString("role");

                User user = new User(userId, username, email, password, role);

                rs.close();
                ps.close();
                con.close();

                return user;
            }

            rs.close();
            ps.close();
            con.close();

            return null;
        } catch (Exception e) {
            System.out.println("Login validation failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inserts a transaction into the transactions table.
     *
     * @param transaction the Transaction object to insert
     * @return true if inserted successfully, otherwise false
     */
    public boolean insertTransaction(Transaction transaction) {
    	if (transaction == null) {
    		return false;
    	}
    	
        String query = "INSERT INTO transactions (userId, type, amount, category, date, description) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
        	Connection con = connect();
        	PreparedStatement ps = con.prepareStatement(query);

        	ps.setInt(1, transaction.getUserId());
        	ps.setString(2, transaction.getType());
        	ps.setDouble(3, transaction.getAmount());
        	ps.setString(4, transaction.getCategory());
        	ps.setDate(5, Date.valueOf(transaction.getDate()));
        	ps.setString(6, transaction.getDescription());

        	int rows = ps.executeUpdate();

        	ps.close();
        	con.close();

        	return rows > 0;
        } catch (Exception e) {
        	System.out.println("Insert transaction failed: " + e.getMessage());
        	return false;
        }
    }

    /**
     * Updates an existing transaction in the transactions table.
     *
     * @param transaction the updated Transaction object
     * @return true if updated successfully, otherwise false
     */
    public boolean updateTransaction(Transaction transaction) {
    	if (transaction == null) {
    		return false;
    	}
    	
        String query = "UPDATE transactions SET type = ?, amount = ?, category = ?, date = ?, description = ? "
                     + "WHERE id = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, transaction.getType());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getCategory());
            ps.setDate(4, Date.valueOf(transaction.getDate()));
            ps.setString(5, transaction.getDescription());
            ps.setInt(6, transaction.getId());

            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            return rows > 0;
        } catch (Exception e) {
            System.out.println("Update transaction failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a transaction from the transactions table using its ID.
     *
     * @param id the ID of the transaction to delete
     * @return true if deleted successfully, otherwise false
     */
    public boolean deleteTransaction(int id) {
        String query = "DELETE FROM transactions WHERE id = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            return rows > 0;
        } catch (Exception e) {
            System.out.println("Delete transaction failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches all transactions for a specific user.
     *
     * @param userId the ID of the user
     * @return an ArrayList of Transaction objects
     */
    public ArrayList<Transaction> fetchTransactions(int userId) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        String query = "SELECT * FROM transactions WHERE userId = ? ORDER BY date";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                Date sqlDate = rs.getDate("date");
                String description = rs.getString("description");
                
                LocalDate date = null;
                if (sqlDate != null) {
                    date = sqlDate.toLocalDate();
                }

                Transaction transaction = new Transaction(id, userId, type, amount, category, date, description);
                transactions.add(transaction);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Fetch transactions failed: " + e.getMessage());
        }

        return transactions;
    }

    /**
     * Fetches one transaction by its ID.
     *
     * @param id the transaction ID
     * @return the transaction if found, otherwise null
     */
    public Transaction fetchTransactionById(int id) {
        String query = "SELECT * FROM transactions WHERE id = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("userId");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                Date sqlDate = rs.getDate("date");
                String description = rs.getString("description");

                LocalDate date = null;
                if (sqlDate != null) {
                    date = sqlDate.toLocalDate();
                }

                Transaction transaction = new Transaction(id, userId, type, amount, category, date, description);

                rs.close();
                ps.close();
                con.close();

                return transaction;
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Fetch transaction by ID failed: " + e.getMessage());
        }

        return null;
    }
    
    /**
     * Fetches all users in the system.
     * This is mainly for Admin use.
     * 
     * @return an ArrayList of all users
     */
    public ArrayList<User> fetchAllUsers() {
    	ArrayList<User> users = new ArrayList<User>();
    	String query = "SELECT * FROM users ORDER BY userID";
    	
    	try {
    		Connection con = connect();
    		PreparedStatement ps = con.prepareStatement(query);
    		ResultSet rs = ps.executeQuery();
    		
    		while (rs.next()) {
                int userId = rs.getInt("userId");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user = new User(userId, username, email, password, role);
                users.add(user);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Fetch all users failed: " + e.getMessage());
        }

        return users;
    }

    /**
     * Fetches all transactions in the system.
     * This is mainly for Admin use.
     *
     * @return an ArrayList of all transactions
     */
    public ArrayList<Transaction> fetchAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        String query = "SELECT * FROM transactions ORDER BY userId, date";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("userId");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                Date sqlDate = rs.getDate("date");
                String description = rs.getString("description");

                LocalDate date = null;
                if (sqlDate != null) {
                    date = sqlDate.toLocalDate();
                }

                Transaction transaction = new Transaction(id, userId, type, amount, category, date, description);
                transactions.add(transaction);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Fetch all transactions failed: " + e.getMessage());
        }

        return transactions;
    }

    /**
     * Fetches one user by ID.
     *
     * @param userId the user ID
     * @return the user if found, otherwise null
     */
    public User fetchUserById(int userId) {
        String query = "SELECT * FROM users WHERE userId = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user = new User(userId, username, email, password, role);

                rs.close();
                ps.close();
                con.close();

                return user;
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Fetch user by ID failed: " + e.getMessage());
        }

        return null;
    }

    /**
     * Deletes a user from the users table.
     *
     * @param userId the user ID to delete
     * @return true if deleted successfully, otherwise false
     */
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE userId = ?";

        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            return rows > 0;
        } catch (Exception e) {
            System.out.println("Delete user failed: " + e.getMessage());
            return false;
        }
    }
}