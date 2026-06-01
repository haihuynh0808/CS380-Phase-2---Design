package DBpackage;

import java.util.List;

/**
 * Manual test runner for DBconnect.
 * Run this class directly (no JUnit needed).
 *
 * Covers:
 *   - Auth: login (valid/invalid), register, getUserIdByEmail
 *   - Role restrictions: user blocked from all admin methods
 *   - Admin login and data viewing
 *   - Admin editing entries (adminUpdateTransaction)
 *   - Admin deleting entries (adminDeleteTransaction)
 *   - Admin listing users (adminGetAllUsers)
 *   - Admin role management (adminSetUserRole)
 *   - User CRUD with ownership enforcement
 *   - Edge cases: negative amount, bad type, nonexistent IDs, null fields, empty strings
 */
public class DBconnectTest {

    // Adjust these if your DB seeds different IDs
    static int ADMIN_ID = 1;   // must have Role = 'admin' in your Users table
    static int USER_ID  = -1;  // resolved dynamically after register/login

    // Tracks IDs created during tests so we can clean up reliably
    static int userTransId  = -1;  // transaction inserted by regular user
    static int adminTransId = -1;  // transaction inserted by admin (for admin-edit/delete tests)

    // =========================================================
    public static void main(String[] args) {
        DBconnect.connection();
        if (DBconnect.con == null) {
            System.out.println("ABORT: could not connect to database.");
            return;
        }

        // ── Auth ──────────────────────────────────────────────
        testRegisterUser();
        testRegisterDuplicate();
        testLoginValid();
        testLoginInvalidEmail();
        testLoginInvalidPassword();
        testGetUserIdByEmail();
        testGetUserIdByEmailNotFound();

        // ── Role checks ───────────────────────────────────────
        testRoleCheck();

        // ── User CRUD ─────────────────────────────────────────
        testInsertTransaction();
        testInsertValidationNegativeAmount();
        testInsertValidationZeroAmount();
        testInsertValidationBadType();
        testInsertValidationNullType();
        testFetchTransactions();
        testFetchTransactionsIsolation();
        testUpdateTransaction();
        testUpdateTransactionBadAmount();
        testUpdateWrongUser();
        testDeleteWrongUser();
        testDeleteTransaction();
        testFetchAfterDelete();
        testDeleteAlreadyDeleted();

        // ── Admin — view data ─────────────────────────────────
        testAdminInsertTransactionForTests();   // seeds a row the admin will edit/delete
        testAdminGetAllTransactions();
        testAdminGetAllUsers();

        // ── Admin — edit entries ──────────────────────────────
        testAdminUpdateTransaction();
        testAdminUpdateTransactionBadAmount();
        testAdminUpdateTransactionBadType();
        testAdminUpdateNonexistentId();

        // ── Admin — delete entries ────────────────────────────
        testAdminDeleteTransaction();
        testAdminDeleteNonexistentId();

        // ── Admin — role management ───────────────────────────
        testAdminSetUserRole();
        testAdminCannotDemoteSelf();
        testAdminSetRoleBadValue();

        // ── User blocked from all admin methods ───────────────
        testUserBlockedFromAdminGetAllTransactions();
        testUserBlockedFromAdminUpdate();
        testUserBlockedFromAdminDelete();
        testUserBlockedFromAdminGetAllUsers();
        testUserBlockedFromAdminSetRole();

        // ── Edge cases ────────────────────────────────────────
        testFetchForNonexistentUser();
        testUpdateNonexistentTransId();
        testDeleteNonexistentTransId();

        System.out.println("\n=== All tests complete ===");
    }

    // =========================================================
    //  AUTH TESTS
    // =========================================================

    static void testRegisterUser() {
        header("testRegisterUser");
        boolean ok = DBconnect.registerUser("Test User", "testuser@test.com", "pass123");
        System.out.println("Result: " + ok + "  (true on first run, false on duplicate — both OK)");
    }

    static void testRegisterDuplicate() {
        header("testRegisterDuplicate — same email should fail");
        boolean ok = DBconnect.registerUser("Test User 2", "testuser@test.com", "different");
        System.out.println("Expected: false | Got: " + ok);
        pass(ok == false);
    }

    static void testLoginValid() {
        header("testLoginValid — admin credentials");
        String role = DBconnect.loginUser("maimoussack@cwu.edu", "1234");
        System.out.println("Expected: admin | Got: " + role);
        pass("admin".equals(role));
    }

    static void testLoginInvalidEmail() {
        header("testLoginInvalidEmail");
        String role = DBconnect.loginUser("nobody@nowhere.com", "1234");
        System.out.println("Expected: null  | Got: " + role);
        pass(role == null);
    }

    static void testLoginInvalidPassword() {
        header("testLoginInvalidPassword");
        String role = DBconnect.loginUser("maimoussack@cwu.edu", "wrongpass");
        System.out.println("Expected: null  | Got: " + role);
        pass(role == null);
    }

    static void testGetUserIdByEmail() {
        header("testGetUserIdByEmail");
        USER_ID = DBconnect.getUserIdByEmail("testuser@test.com");
        System.out.println("USER_ID resolved to: " + USER_ID + "  (expected: > 0)");
        pass(USER_ID > 0);
    }

    static void testGetUserIdByEmailNotFound() {
        header("testGetUserIdByEmailNotFound");
        int id = DBconnect.getUserIdByEmail("ghost@nowhere.com");
        System.out.println("Expected: -1 | Got: " + id);
        pass(id == -1);
    }

    // =========================================================
    //  ROLE CHECK TESTS
    // =========================================================

    static void testRoleCheck() {
        header("testRoleCheck");
        boolean adminResult = DBconnect.isAdmin(ADMIN_ID);
        boolean userResult  = DBconnect.isAdmin(USER_ID);
        System.out.println("Admin ID " + ADMIN_ID + " → isAdmin: " + adminResult + "  (expected: true)");
        System.out.println("User  ID " + USER_ID  + " → isAdmin: " + userResult  + "  (expected: false)");
        pass(adminResult && !userResult);
    }

    // =========================================================
    //  USER CRUD TESTS
    // =========================================================

    static void testInsertTransaction() {
        header("testInsertTransaction — valid insert");
        boolean ok = DBconnect.insertTransaction(
            USER_ID, "expenses", 45.99, "Food", "2025-05-20", "Grocery run"
        );
        System.out.println("Expected: true | Got: " + ok);
        pass(ok);
    }

    static void testInsertValidationNegativeAmount() {
        header("testInsertValidationNegativeAmount");
        boolean ok = DBconnect.insertTransaction(USER_ID, "expenses", -10.00, "Food", "2025-05-20", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testInsertValidationZeroAmount() {
        header("testInsertValidationZeroAmount");
        boolean ok = DBconnect.insertTransaction(USER_ID, "income", 0, "Salary", "2025-05-20", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testInsertValidationBadType() {
        header("testInsertValidationBadType — 'shopping' is not valid");
        boolean ok = DBconnect.insertTransaction(USER_ID, "shopping", 50.00, "Misc", "2025-05-20", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testInsertValidationNullType() {
        header("testInsertValidationNullType");
        boolean ok = DBconnect.insertTransaction(USER_ID, null, 50.00, "Misc", "2025-05-20", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testFetchTransactions() {
        header("testFetchTransactions — user sees own rows");
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        System.out.println("Rows returned: " + rows.size() + "  (expected: >= 1)");
        for (String[] r : rows) {
            System.out.println("  TransID=" + r[0] + " | " + r[1] + " | $" + r[2]
                             + " | " + r[3] + " | " + r[4] + " | " + r[5]);
        }
        if (!rows.isEmpty()) {
            userTransId = Integer.parseInt(rows.get(0)[0]);
            System.out.println("Captured userTransId = " + userTransId);
        }
        pass(rows.size() >= 1);
    }

    static void testFetchTransactionsIsolation() {
        header("testFetchTransactionsIsolation — user cannot see admin's rows");
        // getTransactionsByUser filters by UserID in SQL, so each user only sees their own rows.
        // We verify admin's row count is separate from user's row count.
        List<String[]> userRows  = DBconnect.getTransactionsByUser(USER_ID);
        List<String[]> adminRows = DBconnect.getTransactionsByUser(ADMIN_ID);
        boolean noLeak = userRows.stream().noneMatch(r ->
            adminRows.stream().anyMatch(a -> a[0].equals(r[0]))
        );
        System.out.println("User rows:  " + userRows.size());
        System.out.println("Admin rows: " + adminRows.size());
        System.out.println("No overlap (isolation confirmed): " + noLeak + "  (expected: true)");
        pass(noLeak);
    }

    static void testUpdateTransaction() {
        header("testUpdateTransaction — owner updates their own row");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;

        boolean ok = DBconnect.updateTransaction(
            userTransId, USER_ID,
            "expenses", 55.00, "Food", "2025-05-21", "Updated grocery run"
        );
        System.out.println("Update result: " + ok + "  (expected: true)");

        // Verify the values changed in DB
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        for (String[] r : rows) {
            if (r[0].equals(String.valueOf(userTransId))) {
                System.out.println("  Amount in DB:  $" + r[2] + "  (expected: 55.0)");
                System.out.println("  Desc in DB:    " + r[5] + "  (expected: Updated grocery run)");
                pass(ok && "55.0".equals(r[2]) && "Updated grocery run".equals(r[5]));
                return;
            }
        }
        pass(false);  // row not found
    }

    static void testUpdateTransactionBadAmount() {
        header("testUpdateTransactionBadAmount — zero amount should fail");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        boolean ok = DBconnect.updateTransaction(userTransId, USER_ID, "expenses", 0, "Food", "2025-05-21", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testUpdateWrongUser() {
        header("testUpdateWrongUser — admin tries to update USER_ID's row via user method");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        boolean ok = DBconnect.updateTransaction(
            userTransId, ADMIN_ID,
            "income", 9999.00, "Hacked", "2025-01-01", "should not work"
        );
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testDeleteWrongUser() {
        header("testDeleteWrongUser — admin tries to delete USER_ID's row via user method");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        boolean ok = DBconnect.deleteTransaction(userTransId, ADMIN_ID);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testDeleteTransaction() {
        header("testDeleteTransaction — owner deletes their own row");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        boolean ok = DBconnect.deleteTransaction(userTransId, USER_ID);
        System.out.println("Expected: true | Got: " + ok);
        pass(ok);
    }

    static void testFetchAfterDelete() {
        header("testFetchAfterDelete — row must be gone");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
        boolean stillThere = rows.stream().anyMatch(r -> r[0].equals(String.valueOf(userTransId)));
        System.out.println("Row still present? " + stillThere + "  (expected: false)");
        pass(!stillThere);
    }

    static void testDeleteAlreadyDeleted() {
        header("testDeleteAlreadyDeleted — second delete on same ID should fail");
        if (skipIf(userTransId == -1, "no userTransId captured")) return;
        boolean ok = DBconnect.deleteTransaction(userTransId, USER_ID);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    // =========================================================
    //  ADMIN TESTS — VIEW DATA
    // =========================================================

    static void testAdminInsertTransactionForTests() {
        header("testAdminInsertTransactionForTests — seed a row for admin edit/delete tests");
        // We insert a row as USER_ID, then capture its ID for admin to act on
        boolean ok = DBconnect.insertTransaction(
            USER_ID, "income", 500.00, "Salary", "2025-06-01", "Admin test target"
        );
        if (ok) {
            List<String[]> rows = DBconnect.getTransactionsByUser(USER_ID);
            if (!rows.isEmpty()) {
                adminTransId = Integer.parseInt(rows.get(0)[0]);
                System.out.println("Seeded adminTransId = " + adminTransId);
            }
        }
        pass(ok && adminTransId > 0);
    }

    static void testAdminGetAllTransactions() {
        header("testAdminGetAllTransactions — admin sees all users' rows");
        List<String[]> rows = DBconnect.adminGetAllTransactions(ADMIN_ID);
        System.out.println("Total rows across all users: " + rows.size() + "  (expected: >= 1)");
        for (String[] r : rows) {
            System.out.println("  TransID=" + r[0] + " | User=" + r[1] + " | " + r[2]
                             + " | $" + r[3] + " | " + r[4] + " | " + r[5] + " | " + r[6]);
        }
        pass(rows.size() >= 1);
    }

    static void testAdminGetAllUsers() {
        header("testAdminGetAllUsers — admin sees every registered user");
        List<String[]> rows = DBconnect.adminGetAllUsers(ADMIN_ID);
        System.out.println("Users in system: " + rows.size() + "  (expected: >= 2)");
        for (String[] r : rows) {
            System.out.println("  UserID=" + r[0] + " | " + r[1] + " | " + r[2] + " | Role=" + r[3]);
        }
        pass(rows.size() >= 2);
    }

    // =========================================================
    //  ADMIN TESTS — EDIT ENTRIES
    // =========================================================

    static void testAdminUpdateTransaction() {
        header("testAdminUpdateTransaction — admin edits any user's row");
        if (skipIf(adminTransId == -1, "no adminTransId captured")) return;

        boolean ok = DBconnect.adminUpdateTransaction(
            ADMIN_ID, adminTransId,
            "income", 750.00, "Salary", "2025-06-02", "Admin edited"
        );
        System.out.println("Expected: true | Got: " + ok);

        // Verify change is visible
        List<String[]> all = DBconnect.adminGetAllTransactions(ADMIN_ID);
        for (String[] r : all) {
            if (r[0].equals(String.valueOf(adminTransId))) {
                System.out.println("  Amount in DB: $" + r[3] + "  (expected: 750.0)");
                System.out.println("  Desc in DB:   " + r[6] + "  (expected: Admin edited)");
                pass(ok && "750.0".equals(r[3]) && "Admin edited".equals(r[6]));
                return;
            }
        }
        pass(false);
    }

    static void testAdminUpdateTransactionBadAmount() {
        header("testAdminUpdateTransactionBadAmount — negative amount");
        if (skipIf(adminTransId == -1, "no adminTransId captured")) return;
        boolean ok = DBconnect.adminUpdateTransaction(ADMIN_ID, adminTransId, "income", -1, "Salary", "2025-06-02", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testAdminUpdateTransactionBadType() {
        header("testAdminUpdateTransactionBadType — invalid type string");
        if (skipIf(adminTransId == -1, "no adminTransId captured")) return;
        boolean ok = DBconnect.adminUpdateTransaction(ADMIN_ID, adminTransId, "transfer", 100, "Misc", "2025-06-02", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testAdminUpdateNonexistentId() {
        header("testAdminUpdateNonexistentId — ID 999999 should not exist");
        boolean ok = DBconnect.adminUpdateTransaction(ADMIN_ID, 999999, "income", 1.00, "Test", "2025-01-01", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    // =========================================================
    //  ADMIN TESTS — DELETE ENTRIES
    // =========================================================

    static void testAdminDeleteTransaction() {
        header("testAdminDeleteTransaction — admin deletes any user's row");
        if (skipIf(adminTransId == -1, "no adminTransId captured")) return;
        boolean ok = DBconnect.adminDeleteTransaction(ADMIN_ID, adminTransId);
        System.out.println("Expected: true | Got: " + ok);

        // Confirm it's gone
        List<String[]> all = DBconnect.adminGetAllTransactions(ADMIN_ID);
        boolean stillThere = all.stream().anyMatch(r -> r[0].equals(String.valueOf(adminTransId)));
        System.out.println("Row still present after admin delete? " + stillThere + "  (expected: false)");
        pass(ok && !stillThere);
    }

    static void testAdminDeleteNonexistentId() {
        header("testAdminDeleteNonexistentId — ID 999999 should not exist");
        boolean ok = DBconnect.adminDeleteTransaction(ADMIN_ID, 999999);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    // =========================================================
    //  ADMIN TESTS — ROLE MANAGEMENT
    // =========================================================

    static void testAdminSetUserRole() {
        header("testAdminSetUserRole — promote test user to admin, then demote back");
        boolean promoted = DBconnect.adminSetUserRole(ADMIN_ID, USER_ID, "admin");
        System.out.println("Promote to admin: " + promoted + "  (expected: true)");
        System.out.println("  isAdmin after promote: " + DBconnect.isAdmin(USER_ID) + "  (expected: true)");

        boolean demoted = DBconnect.adminSetUserRole(ADMIN_ID, USER_ID, "user");
        System.out.println("Demote back to user: " + demoted + "  (expected: true)");
        System.out.println("  isAdmin after demote: " + DBconnect.isAdmin(USER_ID) + "  (expected: false)");
        pass(promoted && demoted && !DBconnect.isAdmin(USER_ID));
    }

    static void testAdminCannotDemoteSelf() {
        header("testAdminCannotDemoteSelf — admin cannot remove own admin rights");
        boolean ok = DBconnect.adminSetUserRole(ADMIN_ID, ADMIN_ID, "user");
        System.out.println("Expected: false | Got: " + ok);
        System.out.println("Admin still has role: " + DBconnect.isAdmin(ADMIN_ID) + "  (expected: true)");
        pass(!ok && DBconnect.isAdmin(ADMIN_ID));
    }

    static void testAdminSetRoleBadValue() {
        header("testAdminSetRoleBadValue — 'superuser' is not valid");
        boolean ok = DBconnect.adminSetUserRole(ADMIN_ID, USER_ID, "superuser");
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    // =========================================================
    //  ROLE RESTRICTION TESTS — USER BLOCKED FROM ADMIN METHODS
    // =========================================================

    static void testUserBlockedFromAdminGetAllTransactions() {
        header("testUserBlockedFromAdminGetAllTransactions");
        List<String[]> rows = DBconnect.adminGetAllTransactions(USER_ID);
        System.out.println("Expected size: 0 | Got: " + rows.size());
        pass(rows.size() == 0);
    }

    static void testUserBlockedFromAdminUpdate() {
        header("testUserBlockedFromAdminUpdate");
        boolean ok = DBconnect.adminUpdateTransaction(USER_ID, 1, "income", 100, "Test", "2025-01-01", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testUserBlockedFromAdminDelete() {
        header("testUserBlockedFromAdminDelete");
        boolean ok = DBconnect.adminDeleteTransaction(USER_ID, 1);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testUserBlockedFromAdminGetAllUsers() {
        header("testUserBlockedFromAdminGetAllUsers");
        List<String[]> rows = DBconnect.adminGetAllUsers(USER_ID);
        System.out.println("Expected size: 0 | Got: " + rows.size());
        pass(rows.size() == 0);
    }

    static void testUserBlockedFromAdminSetRole() {
        header("testUserBlockedFromAdminSetRole");
        boolean ok = DBconnect.adminSetUserRole(USER_ID, ADMIN_ID, "user");
        System.out.println("Expected: false | Got: " + ok);
        System.out.println("Admin role unchanged: " + DBconnect.isAdmin(ADMIN_ID) + "  (expected: true)");
        pass(!ok && DBconnect.isAdmin(ADMIN_ID));
    }

    // =========================================================
    //  EDGE CASE TESTS
    // =========================================================

    static void testFetchForNonexistentUser() {
        header("testFetchForNonexistentUser — UserID 999999 should have no rows");
        List<String[]> rows = DBconnect.getTransactionsByUser(999999);
        System.out.println("Expected size: 0 | Got: " + rows.size());
        pass(rows.size() == 0);
    }

    static void testUpdateNonexistentTransId() {
        header("testUpdateNonexistentTransId — TransID 999999 does not exist");
        boolean ok = DBconnect.updateTransaction(999999, USER_ID, "expenses", 10.00, "Test", "2025-01-01", null);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    static void testDeleteNonexistentTransId() {
        header("testDeleteNonexistentTransId — TransID 999999 does not exist");
        boolean ok = DBconnect.deleteTransaction(999999, USER_ID);
        System.out.println("Expected: false | Got: " + ok);
        pass(!ok);
    }

    // =========================================================
    //  UTILITIES
    // =========================================================

    static void header(String name) {
        System.out.println("\n-- " + name);
    }

    static void pass(boolean condition) {
        System.out.println(condition ? "  [PASS]" : "  [FAIL] *** CHECK THIS ***");
    }

    /** Prints a skip message and returns true if the condition means this test can't run. */
    static boolean skipIf(boolean condition, String reason) {
        if (condition) {
            System.out.println("  SKIP: " + reason);
            return true;
        }
        return false;
    }
}
