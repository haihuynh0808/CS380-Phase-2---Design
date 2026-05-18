package com.codecrafters.expensetracker.database;

/**
 * Stub implementation of DatabaseManager used for UI development and testing.
 *
 * <p><b>NOTE TO HAI:</b> Replace this file with the real DatabaseManager once
 * the MySQL/JDBC implementation is complete. This stub returns hard-coded values
 * so the UI layer can be built and tested independently.</p>
 *
 * <p>Seeded test accounts:</p>
 * <ul>
 *   <li>User  — username: {@code Omar123}, password: {@code Pass123}</li>
 *   <li>User  — username: {@code Haih},    password: {@code Pass456}</li>
 *   <li>Admin — username: {@code admin},   password: {@code AdminPass1}</li>
 * </ul>
 *
 * @author  Hai Huynh  (stub written by Omar Lorenzo Jimenez)
 * @version 1.0-stub
 */
public class DatabaseManager {
 
    /**
     * Validates login credentials against the database.
     *
     * @param username the entered username
     * @param password the entered password
     * @param role     either {@code "USER"} or {@code "ADMIN"}
     * @return {@code true} if credentials match a record with that role
     */
    public boolean validateLogin(String username, String password, String role) {
        // Stub
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
        // Stub
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
        // Stub: always succeeds unless username is "taken"
        return !"taken".equalsIgnoreCase(username);
    }
 
    /**
     * Checks whether a username already exists in the database.
     *
     * @param username the username to check
     * @return {@code true} if the username is already taken
     */
    public boolean usernameExists(String username) {
        // Stub
        return "Omar123".equals(username) || "Haih".equals(username)
            || "admin".equals(username);
    }
}
