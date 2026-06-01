package final_project;

/**
 * Handles login state, logout, and role-based routing.
 *
 * This class is useful for Week 2 because it supports:
 * login, logout, and deciding whether the user goes to the
 * normal dashboard or the admin dashboard.
 */
public class SessionManager {

    // The database manager used for login validation. 
    private DatabaseManager db;

    // The currently logged-in user. 
    private User currentUser;

    /**
     * Creates a SessionManager object.
     *
     * @param db the database manager
     */
    public SessionManager(DatabaseManager db) {
        this.db = db;
        this.currentUser = null;
    }

    /**
     * Attempts to log in a user.
     *
     * @param username the username
     * @param password the password
     * @return true if login succeeds, otherwise false
     */
    public boolean login(String username, String password) {
        User user = db.validateLogin(username, password);

        if (user != null) {
            currentUser = user;
            return true;
        }

        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current user, or null if nobody is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns whether someone is currently logged in.
     *
     * @return true if logged in, otherwise false
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Returns whether the current user is an admin.
     *
     * @return true if the current user is an admin, otherwise false
     */
    public boolean isAdminLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Returns the landing page route after login.
     *
     * Student B can use this to decide which screen to open next.
     *
     * @return "ADMIN_DASHBOARD", "USER_DASHBOARD", or "LOGIN"
     */
    public String getLandingRoute() {
        if (currentUser == null) {
            return "LOGIN";
        }

        if (currentUser.isAdmin()) {
            return "ADMIN_DASHBOARD";
        }

        return "USER_DASHBOARD";
    }
}