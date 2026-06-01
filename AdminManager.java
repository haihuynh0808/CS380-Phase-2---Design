package final_project;

import java.util.ArrayList;

/**
 * Handles the Admin business logic for the Smart Expense Tracker application.
 *
 * This class does not write SQL directly.
 * Instead, it checks permissions and then calls DatabaseManager methods.
 */
public class AdminManager {

    /** The database manager used to access stored data. */
    private DatabaseManager db;

    /**
     * Creates an AdminManager object.
     *
     * @param db the database manager
     */
    public AdminManager(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Checks whether the given user is allowed to use Admin features.
     *
     * @param currentUser the currently logged-in user
     * @return true if the user is an admin, otherwise false
     */
    public boolean isAdminAuthorized(User currentUser) {
        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns all users if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @return a list of all users, or an empty list if not authorized
     */
    public ArrayList<User> getAllUsersForAdmin(User currentUser) {
        if (!isAdminAuthorized(currentUser)) {
            return new ArrayList<User>();
        }

        return db.fetchAllUsers();
    }

    /**
     * Returns all transactions if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @return a list of all transactions, or an empty list if not authorized
     */
    public ArrayList<Transaction> getAllTransactionsForAdmin(User currentUser) {
        if (!isAdminAuthorized(currentUser)) {
            return new ArrayList<Transaction>();
        }

        return db.fetchAllTransactions();
    }

    /**
     * Updates any selected transaction if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @param transaction the transaction to update
     * @return true if updated successfully, otherwise false
     */
    public boolean updateTransactionAsAdmin(User currentUser, Transaction transaction) {
        if (!isAdminAuthorized(currentUser)) {
            return false;
        }

        if (transaction == null) {
            return false;
        }

        if (transaction.getId() <= 0) {
            return false;
        }

        return db.updateTransaction(transaction);
    }

    /**
     * Deletes any selected transaction if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @param transactionId the ID of the transaction to delete
     * @return true if deleted successfully, otherwise false
     */
    public boolean deleteTransactionAsAdmin(User currentUser, int transactionId) {
        if (!isAdminAuthorized(currentUser)) {
            return false;
        }

        if (transactionId <= 0) {
            return false;
        }

        return db.deleteTransaction(transactionId);
    }

    /**
     * Deletes a selected user if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @param userId the ID of the user to delete
     * @return true if deleted successfully, otherwise false
     */
    public boolean deleteUserAsAdmin(User currentUser, int userId) {
        if (!isAdminAuthorized(currentUser)) {
            return false;
        }

        if (userId <= 0) {
            return false;
        }

        return db.deleteUser(userId);
    }

    /**
     * Returns one user by ID if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @param userId the user ID to search for
     * @return the user if found and authorized, otherwise null
     */
    public User getUserByIdForAdmin(User currentUser, int userId) {
        if (!isAdminAuthorized(currentUser)) {
            return null;
        }

        if (userId <= 0) {
            return null;
        }

        return db.fetchUserById(userId);
    }

    /**
     * Returns one transaction by ID if the current user is an admin.
     *
     * @param currentUser the currently logged-in user
     * @param transactionId the transaction ID to search for
     * @return the transaction if found and authorized, otherwise null
     */
    public Transaction getTransactionByIdForAdmin(User currentUser, int transactionId) {
        if (!isAdminAuthorized(currentUser)) {
            return null;
        }

        if (transactionId <= 0) {
            return null;
        }

        return db.fetchTransactionById(transactionId);
    }
}