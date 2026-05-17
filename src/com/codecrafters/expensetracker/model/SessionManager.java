package com.codecrafters.expensetracker.model;

/**
 * Manages the currently logged-in user's session state.
 *
 * <p>This is a singleton utility class used throughout the UI layer to
 * determine who is logged in and what role they hold. It is set after a
 * successful login and cleared on logout.</p>
 *
 * <p>Roles are defined by the constants {@link #ROLE_USER} and
 * {@link #ROLE_ADMIN}.</p>
 *
 * @author  Omar Lorenzo Jimenez
 */
public class SessionManager {
 
    /** Role constant for a regular user account. */
    public static final String ROLE_USER  = "USER";
 
    /** Role constant for an administrator account. */
    public static final String ROLE_ADMIN = "ADMIN";
 
    /** The username of the currently authenticated user, or {@code null} if not logged in. */
    private static String currentUsername = null;
 
    /** The role of the currently authenticated user, or {@code null} if not logged in. */
    private static String currentRole = null;
 
    /** The database-assigned ID of the current user, or {@code -1} if not logged in. */
    private static int currentUserId = -1;
 
    /**
     * Private constructor — this class should not be instantiated.
     */
    private SessionManager() {}
 
    /**
     * Records a successful login by storing the user's identity and role.
     *
     * @param userId   the database-assigned user ID
     * @param username the authenticated username
     * @param role     either {@link #ROLE_USER} or {@link #ROLE_ADMIN}
     */
    public static void login(int userId, String username, String role) {
        currentUserId   = userId;
        currentUsername = username;
        currentRole     = role;
    }
 
    /**
     * Clears all session data, effectively logging the user out.
     */
    public static void logout() {
        currentUserId   = -1;
        currentUsername = null;
        currentRole     = null;
    }
 
    /**
     * Returns the username of the currently logged-in user.
     *
     * @return the current username, or {@code null} if no user is logged in
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
 
    /**
     * Returns the role of the currently logged-in user.
     *
     * @return {@link #ROLE_USER}, {@link #ROLE_ADMIN}, or {@code null}
     */
    public static String getCurrentRole() {
        return currentRole;
    }
 
    /**
     * Returns the database ID of the currently logged-in user.
     *
     * @return the user ID, or {@code -1} if no user is logged in
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }
 
    /**
     * Returns {@code true} if a user is currently authenticated.
     *
     * @return {@code true} if logged in, {@code false} otherwise
     */
    public static boolean isLoggedIn() {
        return currentUsername != null;
    }
 
    /**
     * Returns {@code true} if the current user has the administrator role.
     *
     * @return {@code true} if the current role is {@link #ROLE_ADMIN}
     */
    public static boolean isAdmin() {
        return ROLE_ADMIN.equals(currentRole);
    }
}
