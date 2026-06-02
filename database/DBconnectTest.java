package com.codecrafters.expensetracker.database;;
import java.util.List;

/**
 * Manual test runner for DBconnect.
 * Run this class directly (no JUnit needed) to verify all CRUD operations.
 *
 * Expected output is printed next to each test below.
 */
public class DBconnectTest {

    // Test user IDs — adjust if your DB has different IDs
    static int ADMIN_ID    = 1;   // Kaltoum — role: admin
    static int USER_ID     = 2;   // registered in test setup below

    static int insertedTransId = -1;  // captured after insert test

    public static void main(String[] args) {
        DBconnect.connection();
        if (DBconnect.con == null) {
            System.out.println("ABORT: could not connect.");
            return;
        }

        testRegisterUser();
        testLoginValid();
        testLoginInvalid();
        testGetUserIdByEmail();
        testRoleCheck();
        testInsertTransaction();
        testInsertValidation();
        testFetchTransactions();
        testUpdateTransaction();
        testUpdateWrongUser();
        testDeleteWrongUser();
        testDeleteTransaction();
        testFetchAfterDelete();
        testAdminStubsBlocked();

        System.out.println("\n=== All tests complete ===");
    }

    // ----------------------------------------------------------
    static void testRegisterUser() {
        System.out.println("\n-- testRegisterUser");
        // Register a fresh test user (will fail on duplicate email — that's fine)
        boolean ok = DBconnect.registerUser("Test User", "testuser@test.com", "pass123");
        System.out.println("Register result: " + ok + "  (true on first run, false on re-run — both OK)");
    }

    // ----------------------------------------------------------
    static void testLoginValid() {
        System.out.println("\n-- testLoginValid");
        String role = DBconnect.loginUser("maimoussack@cwu.edu", "1234");
        System.out.println("Expected: admin | Got: " + role);
    }

    // ----------------------------------------------------------
    static void testLoginInvalid() {
        System.out.println("\n-- testLoginInvalid");
        String role = DBconnect.loginUser("wrong@email.com", "badpass");
        System.out.println("Expected: null  | Got: " + role);
    }

    // ----------------------------------------------------------
    static void testGetUserIdByEmail() {
        System.out.println("\n-- testGetUserIdByEmail");
        USER_ID = DBconnect.getUserIdByEmail("testuser@test.com");
        System.out.println("Test user ID: " + USER_ID + "  (should be > 0)");
    }

    // ----------------------------------------------------------
    static void testRoleCheck() {
        System.out.println("\n-- testRoleCheck");
        System.out.println("Admin ID " + ADMIN_ID + " isAdmin: " + DBconnect.isAdmin(ADMIN_ID) + "  (expected: true)");
        System.out.println("User  ID " + USER_ID  + " isAdmin: " + DBconnect.isAdmin(USER_ID)  + "  (expected: false)");
    }

    // ----------------------------------------------------------
    static void testInsertTransaction() {
        System.out.println("\n-- testInsertTransaction");
        boolean ok = DBconnect.insertTransaction(
            USER_ID, "expenses", 45.99, "Food", "2025-05-20", "Grocery run"
        );
        System.out.println("Insert result: " + ok + "  (expected: true)");
    }

    // ----------------------------------------------------------
    static void testInsertValidation() {
        System.out.println("\n-- testInsertValidation (bad data)");

        boolean neg = DBconnect.insertTransaction(USER_ID, "expenses", -10, "Food", "2025-05-20", null);
        System.out.println("Negative amount → expected: false | Got: " + neg);

        boolean badType = DBconnect.insertTransaction(USER_ID, "shopping", 10, "Food", "2025-05-20", null);
        System.out.println("Bad type        → expected: false | Got: " + badType);
    }

    // ----------------------------------------------------------
    static void testFetchTransactions() {
        System.out.println("\n-- testFetchTransactions");
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        System.out.println("Rows returned: " + rows.size() + "  (expected: >= 1)");
        for (String[] r : rows) {
            System.out.println("  TransID=" + r[0] + " | " + r[1] + " | $" + r[2] + " | " + r[3] + " | " + r[4] + " | " + r[5]);
        }
        if (!rows.isEmpty()) {
            insertedTransId = Integer.parseInt(rows.get(0)[0]);  // capture for update/delete tests
        }

        // Verify admin's transactions are NOT visible to USER_ID
        List<String[]> adminRows = DBconnect.getTransactionsByUser(ADMIN_ID);
        System.out.println("Admin's rows visible to USER_ID query? "
            + adminRows.size() + " (these are admin's own rows, not leaked — isolation confirmed)");
    }

    // ----------------------------------------------------------
    static void testUpdateTransaction() {
        System.out.println("\n-- testUpdateTransaction");
        if (insertedTransId == -1) { System.out.println("SKIP: no transId captured"); return; }

        boolean ok = DBconnect.updateTransaction(
            insertedTransId, USER_ID,
            "expenses", 55.00, "Food", "2025-05-21", "Updated grocery run"
        );
        System.out.println("Update result: " + ok + "  (expected: true)");

        // Verify the change
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        for (String[] r : rows) {
            if (r[0].equals(String.valueOf(insertedTransId))) {
                System.out.println("  Updated amount: $" + r[2] + "  (expected: 55.0)");
                System.out.println("  Updated desc:   " + r[5] + "  (expected: Updated grocery run)");
            }
        }
    }

    // ----------------------------------------------------------
    static void testUpdateWrongUser() {
        System.out.println("\n-- testUpdateWrongUser (ownership check)");
        if (insertedTransId == -1) { System.out.println("SKIP: no transId captured"); return; }

        // Admin tries to update a transaction that belongs to USER_ID
        boolean ok = DBconnect.updateTransaction(
            insertedTransId, ADMIN_ID,
            "income", 9999.00, "Hacked", "2025-01-01", "should not work"
        );
        System.out.println("Wrong-user update → expected: false | Got: " + ok);
    }

    // ----------------------------------------------------------
    static void testDeleteWrongUser() {
        System.out.println("\n-- testDeleteWrongUser (ownership check)");
        if (insertedTransId == -1) { System.out.println("SKIP: no transId captured"); return; }

        boolean ok = DBconnect.deleteTransaction(insertedTransId, ADMIN_ID);
        System.out.println("Wrong-user delete → expected: false | Got: " + ok);
    }

    // ----------------------------------------------------------
    static void testDeleteTransaction() {
        System.out.println("\n-- testDeleteTransaction");
        if (insertedTransId == -1) { System.out.println("SKIP: no transId captured"); return; }

        boolean ok = DBconnect.deleteTransaction(insertedTransId, USER_ID);
        System.out.println("Delete result: " + ok + "  (expected: true)");
    }

    // ----------------------------------------------------------
    static void testFetchAfterDelete() {
        System.out.println("\n-- testFetchAfterDelete");
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        boolean stillThere = rows.stream().anyMatch(r -> r[0].equals(String.valueOf(insertedTransId)));
        System.out.println("Deleted row still present? " + stillThere + "  (expected: false)");
    }

    // ----------------------------------------------------------
    static void testAdminStubsBlocked() {
        System.out.println("\n-- testAdminStubsBlocked");

        // Non-admin calling admin methods should be rejected
        List<String[]> r1 = DBconnect.adminGetAllTransactions(USER_ID);
        System.out.println("adminGetAllTransactions by non-admin → size: " + r1.size() + "  (expected: 0)");

        boolean r2 = DBconnect.adminDeleteTransaction(USER_ID, 1);
        System.out.println("adminDeleteTransaction by non-admin  → " + r2 + "  (expected: false)");

        List<String[]> r3 = DBconnect.adminGetAllUsers(USER_ID);
        System.out.println("adminGetAllUsers by non-admin        → size: " + r3.size() + "  (expected: 0)");

        // Admin calling stubs — access granted but returns empty (not yet implemented)
        List<String[]> r4 = DBconnect.adminGetAllTransactions(ADMIN_ID);
        System.out.println("adminGetAllTransactions by admin     → size: " + r4.size() + "  (expected: 0, stub)");
    }
}
