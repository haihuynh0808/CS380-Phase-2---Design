package final_project;

/**
 * Represents one user account in the Smart Expense Tracker application.
 * 
 * A user has an ID, username, password, and role.
 * The role can be "user" or "admin".
 */
public class User {
	
	// The unique ID of the user. 
    private int userId;

    // The username used to log in. 
    private String username;
    
    // The email of the user. 
    private String email;

    // The password used to log in. 
    private String password;

    // The role of the account, such as "user" or "admin". 
    private String role;

    /**
     * Creates a new User object with all fields.
     *
     * @param userId the user's ID
     * @param username the username
     * @param email the email
     * @param password the password
     * @param role the role of the user
     */
    public User(int userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Creates a new User object without a user ID.
     * This is useful when creating a new account before the database assigns an ID.
     *
     * @param username the username
     * @param email the email
     * @param password the password
     * @param role the role
     */
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the user's ID.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user's ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the role of the user.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Checks whether this user is an administrator.
     *
     * @return true if the role is admin, otherwise false
     */
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    /**
     * Returns the user information as a string.
     *
     * @return a string version of the user object
     */
    @Override
    public String toString() {
        return "User ID: " + userId
                + ", Username: " + username
                + ", Email: " + email
                + ", Role: " + role;
    }
}
