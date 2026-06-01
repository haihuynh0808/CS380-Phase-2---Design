package final_project;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for AdminManager.
 *
 * These tests focus on the business logic layer, especially
 * permission checks for admin actions.
 */
public class AdminManagerTest {

    /** The admin manager being tested. */
    private AdminManager adminManager;

    /** A fake database manager used for testing. */
    private FakeDatabaseManager fakeDb;

    /** A sample admin user. */
    private User adminUser;

    /** A sample normal user. */
    private User normalUser;

    /**
     * Sets up test objects before each test.
     */
    @BeforeEach
    public void setUp() {
        fakeDb = new FakeDatabaseManager();
        adminManager = new AdminManager(fakeDb);

        adminUser = new User(1, "admin1", "admin@email.com", "pass", "admin");
        normalUser = new User(2, "user1", "user@email.com", "pass", "user");
    }

    /**
     * Tests that an admin user is authorized.
     */
    @Test
    public void testIsAdminAuthorizedWithAdmin() {
        assertTrue(adminManager.isAdminAuthorized(adminUser));
    }

    /**
     * Tests that a normal user is not authorized.
     */
    @Test
    public void testIsAdminAuthorizedWithNormalUser() {
        assertFalse(adminManager.isAdminAuthorized(normalUser));
    }

    /**
     * Tests that a null user is not authorized.
     */
    @Test
    public void testIsAdminAuthorizedWithNullUser() {
        assertFalse(adminManager.isAdminAuthorized(null));
    }

    /**
     * Tests that an admin can view all users.
     */
    @Test
    public void testGetAllUsersForAdminWithAdmin() {
        ArrayList<User> users = adminManager.getAllUsersForAdmin(adminUser);
        assertEquals(2, users.size());
    }

    /**
     * Tests that a normal user cannot view all users.
     */
    @Test
    public void testGetAllUsersForAdminWithNormalUser() {
        ArrayList<User> users = adminManager.getAllUsersForAdmin(normalUser);
        assertEquals(0, users.size());
    }

    /**
     * Tests that an admin can view all transactions.
     */
    @Test
    public void testGetAllTransactionsForAdminWithAdmin() {
        ArrayList<Transaction> transactions = adminManager.getAllTransactionsForAdmin(adminUser);
        assertEquals(2, transactions.size());
    }

    /**
     * Tests that a normal user cannot view all transactions.
     */
    @Test
    public void testGetAllTransactionsForAdminWithNormalUser() {
        ArrayList<Transaction> transactions = adminManager.getAllTransactionsForAdmin(normalUser);
        assertEquals(0, transactions.size());
    }

    /**
     * Tests that an admin can delete a transaction.
     */
    @Test
    public void testDeleteTransactionAsAdminWithAdmin() {
        assertTrue(adminManager.deleteTransactionAsAdmin(adminUser, 1));
    }

    /**
     * Tests that a normal user cannot delete a transaction as admin.
     */
    @Test
    public void testDeleteTransactionAsAdminWithNormalUser() {
        assertFalse(adminManager.deleteTransactionAsAdmin(normalUser, 1));
    }

    /**
     * Tests that invalid transaction ID fails.
     */
    @Test
    public void testDeleteTransactionAsAdminWithInvalidId() {
        assertFalse(adminManager.deleteTransactionAsAdmin(adminUser, 0));
    }

    /**
     * Tests that an admin can update a transaction.
     */
    @Test
    public void testUpdateTransactionAsAdminWithAdmin() {
        Transaction t = new Transaction(1, 2, "Expense", 20.0, "Food", LocalDate.now(), "Lunch");
        assertTrue(adminManager.updateTransactionAsAdmin(adminUser, t));
    }

    /**
     * Tests that a normal user cannot update a transaction as admin.
     */
    @Test
    public void testUpdateTransactionAsAdminWithNormalUser() {
        Transaction t = new Transaction(1, 2, "Expense", 20.0, "Food", LocalDate.now(), "Lunch");
        assertFalse(adminManager.updateTransactionAsAdmin(normalUser, t));
    }

    /**
     * Tests that null transaction fails update.
     */
    @Test
    public void testUpdateTransactionAsAdminWithNullTransaction() {
        assertFalse(adminManager.updateTransactionAsAdmin(adminUser, null));
    }

    /**
     * Tests that an admin can delete a user.
     */
    @Test
    public void testDeleteUserAsAdminWithAdmin() {
        assertTrue(adminManager.deleteUserAsAdmin(adminUser, 2));
    }

    /**
     * Tests that a normal user cannot delete a user as admin.
     */
    @Test
    public void testDeleteUserAsAdminWithNormalUser() {
        assertFalse(adminManager.deleteUserAsAdmin(normalUser, 2));
    }

    /**
     * Tests that invalid user ID fails delete.
     */
    @Test
    public void testDeleteUserAsAdminWithInvalidId() {
        assertFalse(adminManager.deleteUserAsAdmin(adminUser, -1));
    }

    /**
     * Tests that an admin can fetch a user by ID.
     */
    @Test
    public void testGetUserByIdForAdminWithAdmin() {
        User user = adminManager.getUserByIdForAdmin(adminUser, 2);
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
    }

    /**
     * Tests that a normal user cannot fetch a user by ID through admin logic.
     */
    @Test
    public void testGetUserByIdForAdminWithNormalUser() {
        User user = adminManager.getUserByIdForAdmin(normalUser, 2);
        assertNull(user);
    }

    /**
     * Tests that an admin can fetch a transaction by ID.
     */
    @Test
    public void testGetTransactionByIdForAdminWithAdmin() {
        Transaction transaction = adminManager.getTransactionByIdForAdmin(adminUser, 1);
        assertNotNull(transaction);
        assertEquals("Expense", transaction.getType());
    }

    /**
     * Tests that a normal user cannot fetch a transaction by ID through admin logic.
     */
    @Test
    public void testGetTransactionByIdForAdminWithNormalUser() {
        Transaction transaction = adminManager.getTransactionByIdForAdmin(normalUser, 1);
        assertNull(transaction);
    }

    /**
     * A fake database manager used only for testing AdminManager logic.
     */
    private static class FakeDatabaseManager extends DatabaseManager {

        @Override
        public ArrayList<User> fetchAllUsers() {
            ArrayList<User> users = new ArrayList<User>();
            users.add(new User(1, "admin1", "admin@email.com", "pass", "admin"));
            users.add(new User(2, "user1", "user@email.com", "pass", "user"));
            return users;
        }

        @Override
        public ArrayList<Transaction> fetchAllTransactions() {
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            transactions.add(new Transaction(1, 2, "Expense", 20.0, "Food", LocalDate.now(), "Lunch"));
            transactions.add(new Transaction(2, 2, "Income", 100.0, "Salary", LocalDate.now(), "Paycheck"));
            return transactions;
        }

        @Override
        public boolean updateTransaction(Transaction transaction) {
            return transaction != null && transaction.getId() > 0;
        }

        @Override
        public boolean deleteTransaction(int id) {
            return id > 0;
        }

        @Override
        public boolean deleteUser(int userId) {
            return userId > 0;
        }

        @Override
        public User fetchUserById(int userId) {
            if (userId == 2) {
                return new User(2, "user1", "user@email.com", "pass", "user");
            }
            return null;
        }

        @Override
        public Transaction fetchTransactionById(int id) {
            if (id == 1) {
                return new Transaction(1, 2, "Expense", 20.0, "Food", LocalDate.now(), "Lunch");
            }
            return null;
        }
    }
}
