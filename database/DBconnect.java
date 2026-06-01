package DBpackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBconnect {

    public static Connection con = null;

    // =========================================================
    //  CONNECTION
    // =========================================================

    /**
     * Establishes a connection to the MySQL database.
     */
    public static void connection() {
        String url      = "jdbc:mysql://localhost:3306/OHKDB";
        String username = "root";
        String pass     = "yourpassword";
        try {
            con = DriverManager.getConnection(url, username, pass);
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // =========================================================
    //  AUTH  (login + register)
    // =========================================================

    /**
     * Validates login credentials and fetches the user role.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return the user's role ("user" or "admin"), or null if login fails
     */
    public static String loginUser(String email, String password) {
        try {
            String sql = "SELECT Role FROM Users WHERE Email = ? AND Password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Role");
            } else {
                return null;       // bad credentials
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the UserID for a given email, or -1 if not found.
     * Call this right after a successful loginUser() to get the session UserID.
     *
     * @param email the user's email address
     * @return UserID, or -1 if not found
     */
    public static int getUserIdByEmail(String email) {
        try {
            String sql = "SELECT UserID FROM Users WHERE Email = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (Exception e) {
            System.out.println("GetUserID error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registers a new user with the default role of "user".
     *
     * @param name     the user's full name
     * @param email    the user's email address
     * @param password the user's password
     * @return true if registration succeeded, false otherwise
     */
    public static boolean registerUser(String name, String email, String password) {
        try {
            String sql = "INSERT INTO Users (Name, Email, Password, Role) VALUES (?, ?, ?, 'user')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            System.out.println("User registered successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  HELPER — role guard
    // =========================================================

    /**
     * Checks whether the given userID has admin privileges.
     * Use this before every admin-only operation.
     *
     * @param userId the UserID to check
     * @return true if role is "admin"
     */
    public static boolean isAdmin(int userId) {
        try {
            String sql = "SELECT Role FROM Users WHERE UserID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "admin".equalsIgnoreCase(rs.getString("Role"));
            }
        } catch (Exception e) {
            System.out.println("Role check error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================
    //  TRANSACTIONS — INSERT
    // =========================================================

    /**
     * Inserts a new transaction for the given user.
     *
     * @param userId          the owner's UserID (from session)
     * @param type            "income" or "expenses"
     * @param amount          transaction amount (must be > 0)
     * @param category        e.g. "Salary", "Food", "Rent"
     * @param transactionDate date in "YYYY-MM-DD" format
     * @param description     optional note (may be null)
     * @return true if insert succeeded
     */
    public static boolean insertTransaction(int userId, String type, double amount,
                                            String category, String transactionDate,
                                            String description) {
        if (amount <= 0) {
            System.out.println("Insert failed: amount must be greater than 0.");
            return false;
        }
        if (type == null || (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expenses"))) {
            System.out.println("Insert failed: type must be 'income' or 'expenses'.");
            return false;
        }

        try {
            String sql = "INSERT INTO TRANSACTIONS (UserID, Type, Amount, Category, TransactionDate, Description) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, category);
            ps.setDate(5, Date.valueOf(transactionDate));
            ps.setString(6, description);
            ps.executeUpdate();
            System.out.println("Transaction inserted successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Insert transaction error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  TRANSACTIONS — UPDATE
    // =========================================================

    /**
     * Updates an existing transaction — but ONLY if it belongs to the requesting user.
     *
     * @param transId          the TransID to update
     * @param requestingUserId the UserID making the request (from session)
     * @param type             new type value
     * @param amount           new amount (must be > 0)
     * @param category         new category
     * @param transactionDate  new date in "YYYY-MM-DD"
     * @param description      new description (may be null)
     * @return true if exactly one row was updated, false if not found or not owner
     */
    public static boolean updateTransaction(int transId, int requestingUserId,
                                            String type, double amount, String category,
                                            String transactionDate, String description) {
        if (amount <= 0) {
            System.out.println("Update failed: amount must be greater than 0.");
            return false;
        }

        try {
            String sql = "UPDATE TRANSACTIONS "
                       + "SET Type=?, Amount=?, Category=?, TransactionDate=?, Description=? "
                       + "WHERE TransID=? AND UserID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, category);
            ps.setDate(4, Date.valueOf(transactionDate));
            ps.setString(5, description);
            ps.setInt(6, transId);
            ps.setInt(7, requestingUserId);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Transaction updated successfully.");
                return true;
            } else {
                System.out.println("Update failed: transaction not found or does not belong to user.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Update transaction error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  TRANSACTIONS — DELETE
    // =========================================================

    /**
     * Deletes a transaction — but ONLY if it belongs to the requesting user.
     *
     * @param transId          the TransID to delete
     * @param requestingUserId the UserID making the request (from session)
     * @return true if the row was deleted, false if not found or not owner
     */
    public static boolean deleteTransaction(int transId, int requestingUserId) {
        try {
            String sql = "DELETE FROM TRANSACTIONS WHERE TransID=? AND UserID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, transId);
            ps.setInt(2, requestingUserId);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Transaction deleted successfully.");
                return true;
            } else {
                System.out.println("Delete failed: transaction not found or does not belong to user.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Delete transaction error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  TRANSACTIONS — FETCH BY USER
    // =========================================================

    /**
     * Fetches all transactions belonging to a specific user.
     * Returns a List of String arrays:
     *   [TransID, Type, Amount, Category, TransactionDate, Description]
     *
     * @param userId the UserID whose transactions to fetch (from session)
     * @return list of transaction rows, empty list if none found
     */
    public static List<String[]> getTransactionsByUser(int userId) {
        List<String[]> results = new ArrayList<>();
        try {
            String sql = "SELECT TransID, Type, Amount, Category, TransactionDate, Description "
                       + "FROM TRANSACTIONS WHERE UserID = ? ORDER BY TransactionDate DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("TransID")),
                    rs.getString("Type"),
                    String.valueOf(rs.getDouble("Amount")),
                    rs.getString("Category"),
                    rs.getString("TransactionDate"),
                    rs.getString("Description") != null ? rs.getString("Description") : ""
                };
                results.add(row);
            }
        } catch (Exception e) {
            System.out.println("Fetch transactions error: " + e.getMessage());
        }
        return results;
    }

    // =========================================================
    //  ADMIN — VIEW ALL TRANSACTIONS
    // =========================================================

    /**
     * [ADMIN] Fetches every transaction across all users, joined with the
     * owning user's name. Non-admins receive an empty list.
     *
     * Columns returned per row:
     *   [TransID, UserName, Type, Amount, Category, TransactionDate, Description]
     *
     * @param requestingUserId must be an admin
     * @return list of all transaction rows, empty if access denied or none found
     */
    public static List<String[]> adminGetAllTransactions(int requestingUserId) {
        if (!isAdmin(requestingUserId)) {
            System.out.println("Access denied: admin only.");
            return new ArrayList<>();
        }
        List<String[]> results = new ArrayList<>();
        try {
            String sql = "SELECT t.TransID, u.Name, t.Type, t.Amount, "
                       + "       t.Category, t.TransactionDate, t.Description "
                       + "FROM TRANSACTIONS t "
                       + "JOIN Users u ON t.UserID = u.UserID "
                       + "ORDER BY t.TransactionDate DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("TransID")),
                    rs.getString("Name"),
                    rs.getString("Type"),
                    String.valueOf(rs.getDouble("Amount")),
                    rs.getString("Category"),
                    rs.getString("TransactionDate"),
                    rs.getString("Description") != null ? rs.getString("Description") : ""
                };
                results.add(row);
            }
        } catch (Exception e) {
            System.out.println("adminGetAllTransactions error: " + e.getMessage());
        }
        return results;
    }

    // =========================================================
    //  ADMIN — UPDATE ANY TRANSACTION
    // =========================================================

    /**
     * [ADMIN] Updates any transaction regardless of which user owns it.
     * Non-admins are rejected immediately.
     *
     * @param requestingUserId must be an admin
     * @param transId          the TransID to update
     * @param type             new type ("income" or "expenses")
     * @param amount           new amount (must be > 0)
     * @param category         new category
     * @param transactionDate  new date in "YYYY-MM-DD"
     * @param description      new description (may be null)
     * @return true if exactly one row was updated
     */
    public static boolean adminUpdateTransaction(int requestingUserId, int transId,
                                                  String type, double amount, String category,
                                                  String transactionDate, String description) {
        if (!isAdmin(requestingUserId)) {
            System.out.println("Access denied: admin only.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Admin update failed: amount must be > 0.");
            return false;
        }
        if (type == null || (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expenses"))) {
            System.out.println("Admin update failed: type must be 'income' or 'expenses'.");
            return false;
        }
        try {
            // No UserID filter — admin can edit any row
            String sql = "UPDATE TRANSACTIONS "
                       + "SET Type=?, Amount=?, Category=?, TransactionDate=?, Description=? "
                       + "WHERE TransID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, category);
            ps.setDate(4, Date.valueOf(transactionDate));
            ps.setString(5, description);
            ps.setInt(6, transId);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Admin updated transaction ID: " + transId);
                return true;
            } else {
                System.out.println("Admin update failed: TransID " + transId + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("adminUpdateTransaction error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  ADMIN — DELETE ANY TRANSACTION
    // =========================================================

    /**
     * [ADMIN] Deletes any transaction by TransID regardless of owner.
     * Non-admins are rejected immediately.
     *
     * @param requestingUserId must be an admin
     * @param transId          the TransID to remove
     * @return true if the row was deleted
     */
    public static boolean adminDeleteTransaction(int requestingUserId, int transId) {
        if (!isAdmin(requestingUserId)) {
            System.out.println("Access denied: admin only.");
            return false;
        }
        try {
            // No UserID filter — admin can delete any row
            String sql = "DELETE FROM TRANSACTIONS WHERE TransID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, transId);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Admin deleted transaction ID: " + transId);
                return true;
            } else {
                System.out.println("Admin delete failed: TransID " + transId + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("adminDeleteTransaction error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    //  ADMIN — LIST ALL USERS
    // =========================================================

    /**
     * [ADMIN] Returns every registered user.
     * Non-admins receive an empty list.
     *
     * Columns returned per row:  [UserID, Name, Email, Role]
     *
     * @param requestingUserId must be an admin
     * @return list of user rows, empty if access denied or none found
     */
    public static List<String[]> adminGetAllUsers(int requestingUserId) {
        if (!isAdmin(requestingUserId)) {
            System.out.println("Access denied: admin only.");
            return new ArrayList<>();
        }
        List<String[]> results = new ArrayList<>();
        try {
            String sql = "SELECT UserID, Name, Email, Role FROM Users ORDER BY UserID ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("UserID")),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Role")
                };
                results.add(row);
            }
        } catch (Exception e) {
            System.out.println("adminGetAllUsers error: " + e.getMessage());
        }
        return results;
    }

    // =========================================================
    //  ADMIN — PROMOTE / DEMOTE USER ROLE
    // =========================================================

    /**
     * [ADMIN] Changes the role of any user.
     * Use this to promote a user to "admin" or demote an admin back to "user".
     * A safeguard prevents an admin from accidentally removing their own admin rights.
     *
     * @param requestingUserId must be an admin
     * @param targetUserId     the UserID whose role to change
     * @param newRole          "admin" or "user"
     * @return true if the role was updated
     */
    public static boolean adminSetUserRole(int requestingUserId, int targetUserId, String newRole) {
        if (!isAdmin(requestingUserId)) {
            System.out.println("Access denied: admin only.");
            return false;
        }
        if (newRole == null || (!newRole.equalsIgnoreCase("admin") && !newRole.equalsIgnoreCase("user"))) {
            System.out.println("adminSetUserRole failed: role must be 'admin' or 'user'.");
            return false;
        }
        if (requestingUserId == targetUserId && newRole.equalsIgnoreCase("user")) {
            System.out.println("adminSetUserRole denied: an admin cannot remove their own admin role.");
            return false;
        }
        try {
            String sql = "UPDATE Users SET Role = ? WHERE UserID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newRole.toLowerCase());
            ps.setInt(2, targetUserId);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Admin set UserID " + targetUserId + " role to '" + newRole + "'.");
                return true;
            } else {
                System.out.println("adminSetUserRole failed: UserID " + targetUserId + " not found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("adminSetUserRole error: " + e.getMessage());
            return false;
        }
    }
}
