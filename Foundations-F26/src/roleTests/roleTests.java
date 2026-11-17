package roleTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;

/*******
 * <p> Title: RoleTests Class. </p>
 *
 * <p> Description: JUnit 5 tests that implement the automatable rows of "TP2 Test Designs" for
 * guiAddRemoveRoles and guiMultipleRoleDispatch.
 *
 * Rows that depend on JavaFX screens (opening a page, the "<Select a User>" and "<Select a Role>"
 * placeholders, logging in and being routed to a home page) are run as manual tests.
 *
 * Automated here:
 *   guiAddRemoveRoles  -> Database.updateUserRole (add/remove roles, invalid role, branch coverage)
 *   guiMultipleRoleDispatch -> Database.getNumberOfRoles (boundary values 1, 2, 3)
 *
 * Database note: these tests use a separate in-memory H2 database (jdbc:h2:mem:roleTestDB).
 * Database.java hard-codes the real URL, so connectToTestDatabase() uses reflection to give the
 * Database object a connection to the test database instead.  Database.java is not modified,
 * the real ~/FoundationDatabase is never touched, and the app can stay open during tests.</p>
 *
 * @author Cardin
 * @version 1.00 2026-11-11 Initial version
 */
public class roleTests {

	private static final String URL = "jdbc:h2:mem:roleTestDB;DB_CLOSE_DELAY=-1";	// separate in-memory test database
	private static final String TEST_USER = "junit_user";
	private static final String TEST_ADMIN_1 = "junit_admin1";
	private static final String TEST_ADMIN_2 = "junit_admin2";

	private Database db = new Database();	// getNumberOfRoles needs no connection
	private Connection raw;		// direct connection used to check results and clean up

	// Called at the start of every database test (replaces @BeforeEach)
	private void setUp() throws SQLException {
		db = new Database();
		connectToTestDatabase(db);
		raw = DriverManager.getConnection(URL, "sa", "");
		deleteTestUsers();
	}

	// Called in a finally block at the end of every database test (replaces @AfterEach)
	private void tearDown() throws SQLException {
		deleteTestUsers();
		raw.close();
		db.closeConnection();
	}

	/*-******************************************************************************************
	 * guiAddRemoveRoles: test table rows
	 */

	// Admin adds the Contributor role to the test user -> Accepted
	@Test
	void addContributorRole() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, false, true);			// starts with Role2 only
			assertTrue(db.updateUserRole(TEST_USER, "Contributor", "true"));
			assertTrue(hasRole(TEST_USER, "contributorRole"));
		} finally {
			tearDown();
		}
	}

	// Admin removes Admin from the only admin -> Rejected (expected to fail: not implemented)
	@Test
	void removeAdminFromOnlyAdmin() throws SQLException {
		setUp();
		try {
			insertUser(TEST_ADMIN_1, true, false, false);
			// Boundary: exactly 1 admin.  Only possible if no other admins exist in the database.
			assumeTrue(countAdmins() == 1,
					"Skipped: the database already has other admins, so 'only admin' cannot be set up");
			db.updateUserRole(TEST_ADMIN_1, "Admin", "false");
			assertTrue(hasRole(TEST_ADMIN_1, "adminRole"), "The last admin's Admin role was removed");
		} finally {
			tearDown();
		}
	}

	// Admin removes Admin from an admin who is not the only admin -> Accepted
	@Test
	void removeAdminWhenAnotherAdminExists() throws SQLException {
		setUp();
		try {
			insertUser(TEST_ADMIN_1, true, false, false);
			insertUser(TEST_ADMIN_2, true, false, false);		// Boundary: 2 admins
			assertTrue(db.updateUserRole(TEST_ADMIN_2, "Admin", "false"));
			assertFalse(hasRole(TEST_ADMIN_2, "adminRole"));
			assertTrue(countAdmins() >= 1, "At least one admin must remain");
		} finally {
			tearDown();
		}
	}

	// Admin removes Contributor, the user's only role -> Rejected (expected to fail: not implemented)
	@Test
	void removeOnlyRole() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, true, false);			// Boundary: exactly 1 role
			db.updateUserRole(TEST_USER, "Contributor", "false");
			assertTrue(hasRole(TEST_USER, "contributorRole"), "User was left with zero roles");
		} finally {
			tearDown();
		}
	}

	// Admin removes Contributor, one of the user's multiple roles -> Accepted
	@Test
	void removeOneOfMultipleRoles() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, true, true);			// 2 roles
			assertTrue(db.updateUserRole(TEST_USER, "Contributor", "false"));
			assertFalse(hasRole(TEST_USER, "contributorRole"));
			assertTrue(hasRole(TEST_USER, "newRole2"), "Other roles must be unchanged");
		} finally {
			tearDown();
		}
	}

	// updateUserRole(testUser, "Blabla invalid role", "true") -> Rejected
	@Test
	void invalidRoleRejected() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, false, false);
			assertFalse(db.updateUserRole(TEST_USER, "Blabla invalid role", "true"));
			assertFalse(hasRole(TEST_USER, "adminRole"));
			assertFalse(hasRole(TEST_USER, "contributorRole"));
			assertFalse(hasRole(TEST_USER, "newRole2"));
		} finally {
			tearDown();
		}
	}

	/*-******************************************************************************************
	 * guiAddRemoveRoles: "Role name and value Strings match"
	 */

	// Role names must match exactly (case, spaces, empty)
	@Test
	void roleNamesMustMatchExactly() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, false, false);
			assertFalse(db.updateUserRole(TEST_USER, "admin", "true"));
			assertFalse(db.updateUserRole(TEST_USER, "Admin ", "true"));
			assertFalse(db.updateUserRole(TEST_USER, "", "true"));
			assertFalse(hasRole(TEST_USER, "adminRole"));
		} finally {
			tearDown();
		}
	}

	/*-******************************************************************************************
	 * guiAddRemoveRoles: coverage of Database.updateUserRole (every branch)
	 */

	// Coverage: Admin branch, true and false
	@Test
	void coverAdminBranch() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, false, false);
			assertTrue(db.updateUserRole(TEST_USER, "Admin", "true"));
			assertTrue(hasRole(TEST_USER, "adminRole"));
			assertTrue(db.updateUserRole(TEST_USER, "Admin", "false"));
			assertFalse(hasRole(TEST_USER, "adminRole"));
		} finally {
			tearDown();
		}
	}

	// Coverage: Role2 branch, true and false
	@Test
	void coverRole2Branch() throws SQLException {
		setUp();
		try {
			insertUser(TEST_USER, false, false, false);
			assertTrue(db.updateUserRole(TEST_USER, "Role2", "true"));
			assertTrue(hasRole(TEST_USER, "newRole2"));
			assertTrue(db.updateUserRole(TEST_USER, "Role2", "false"));
			assertFalse(hasRole(TEST_USER, "newRole2"));
		} finally {
			tearDown();
		}
	}

	// Coverage: database error branches return false
	@Test
	void coverSQLExceptionBranches() throws SQLException {
		Database closedDb = new Database();
		connectToTestDatabase(closedDb);
		closedDb.closeConnection();
		assertFalse(closedDb.updateUserRole(TEST_USER, "Admin", "true"));
		assertFalse(closedDb.updateUserRole(TEST_USER, "Contributor", "true"));
		assertFalse(closedDb.updateUserRole(TEST_USER, "Role2", "true"));
	}

	/*-******************************************************************************************
	 * guiMultipleRoleDispatch: Database.getNumberOfRoles boundary values
	 */

	// 1 role (minimum): user goes straight to that role's home screen
	@Test
	void oneRole() {
		assertEquals(1, db.getNumberOfRoles(user(true, false, false)));
		assertEquals(1, db.getNumberOfRoles(user(false, true, false)));
		assertEquals(1, db.getNumberOfRoles(user(false, false, true)));
	}

	// Admin + Contributor: db.getNumberOfRoles(user) returns 2 (dispatch is shown)
	@Test
	void adminPlusContributorReturnsTwo() {
		assertEquals(2, db.getNumberOfRoles(user(true, true, false)));
	}

	// 3 roles (maximum): user must select a role first
	@Test
	void threeRoles() {
		assertEquals(3, db.getNumberOfRoles(user(true, true, true)));
	}

	/*-******************************************************************************************
	 * MANUAL TESTS
	 *
	 * These rows depend on JavaFX screens, so JUnit cannot run them.  Each is listed here as a
	 * @Disabled test so it appears as "skipped" in the JUnit report, with its steps documented.
	 * Run them by hand in the application and record Actual Result and Pass/Fail.
	 */

	/**
	 * <p> Manual M1: Contributor opens guiAddRemoveRoles page -> Rejected, stays on homepage </p>
	 * Steps:
	 *   1. Log in as a user with only the Contributor role.
	 *   2. On the Contributor home page, look for any button or menu leading to Add/Remove Roles.
	 * Expected: The Contributor home page opens and no option leads to Add/Remove Roles.
	 */
	@Test
	@Disabled("Manual test M1 (JavaFX): Contributor cannot open guiAddRemoveRoles")
	void manual_contributorCannotOpenAddRemoveRoles() { }

	/**
	 * <p> Manual M2: Admin opens guiAddRemoveRoles page -> Accepted, page opens </p>
	 * Steps:
	 *   1. Log in as an Admin.
	 *   2. On Admin home, click the Add/Remove Roles button.
	 * Expected: The Add/Remove Roles page opens.
	 */
	@Test
	@Disabled("Manual test M2 (JavaFX): Admin opens guiAddRemoveRoles")
	void manual_adminOpensAddRemoveRoles() { }

	/**
	 * <p> Manual M3: Index 0 placeholder "&lt;Select a User&gt;" -> no effect </p>
	 * Steps:
	 *   1. Log in as an Admin and open Add/Remove Roles.
	 *   2. Leave the user dropdown on "&lt;Select a User&gt;".
	 * Expected: No roles are shown, and the add/remove role controls are not displayed
	 *           (ControllerAddRemoveRoles.repaintTheWindow hides them).
	 */
	@Test
	@Disabled("Manual test M3 (JavaFX): <Select a User> placeholder has no effect")
	void manual_selectUserPlaceholder() { }

	/**
	 * <p> Manual M4: Admin selects a user -> roles shown, add/remove available </p>
	 * Steps:
	 *   1. Log in as an Admin and open Add/Remove Roles.
	 *   2. Select a user who has the Admin and Contributor roles.
	 * Expected: The current roles label lists the user's roles, and the add/remove dropdowns and
	 *           buttons appear.  Note: check whether the label shows "Role1" instead of
	 *           "Contributor" (setupSelectedUser builds the label with "Role1").
	 */
	@Test
	@Disabled("Manual test M4 (JavaFX): selecting a user shows roles and add/remove controls")
	void manual_M4_selectUserShowsRoles() { }

	/**
	 * <p> Manual M5: Admin logs in with 1 role -> goes straight to guiAdminHome </p>
	 * Steps:
	 *   1. Log in as a user with only the Admin role.
	 * Expected: Admin home opens directly; the dispatch page is not shown.
	 */
	@Test
	@Disabled("Manual test M5 (JavaFX): 1-role Admin skips dispatch")
	void manual_adminOneRoleSkipsDispatch() { }

	/**
	 * <p> Manual M6: Contributor logs in with 1 role -> goes straight to guiContributorHome </p>
	 * Steps:
	 *   1. Log in as a user with only the Contributor role.
	 * Expected: Contributor home opens directly; the dispatch page is not shown.
	 */
	@Test
	@Disabled("Manual test M6 (JavaFX): 1-role Contributor skips dispatch")
	void manual_contributorOneRoleSkipsDispatch() { }

	/**
	 * <p> Manual M7: Admin + Contributor logs in with 2 roles -> dispatch page shown </p>
	 * Steps:
	 *   1. Log in as a user with the Admin and Contributor roles.
	 *   2. Select Admin and continue.  Confirm Admin home opens.  Log out.
	 *   3. Log in again, select Contributor and continue.  Confirm Contributor home opens.
	 * Expected: The dispatch page lists only Admin and Contributor, and each choice opens the
	 *           matching home page.
	 */
	@Test
	@Disabled("Manual test M7 (JavaFX): 2-role user sees dispatch and is routed correctly")
	void manual_twoRolesShowsDispatch() { }

	/**
	 * <p> Manual M8: Admin + Contributor selects the placeholder -> Rejected, blocked with message </p>
	 * Steps:
	 *   1. Log in as a user with the Admin and Contributor roles.
	 *   2. Leave the role dropdown on the placeholder (or no selection) and continue.
	 * Expected: The user stays on the dispatch page and sees a message.
	 * Likely to FAIL: ControllerMultipleRoleDispatch.performRole has no placeholder check; an
	 *           unknown value calls System.exit(0) and a null value throws NullPointerException.
	 */
	@Test
	@Disabled("Manual test M8 (JavaFX): <Select a Role> placeholder is blocked")
	void manual_M8_dispatchPlaceholderBlocked() { }

	/*-******************************************************************************************
	 * Helpers
	 */

	// Connects a Database object to the in-memory test database instead of ~/FoundationDatabase.
	// Database has no way to change its URL, so this sets its private connection and statement
	// fields directly and runs its private createTables() method.
	private void connectToTestDatabase(Database target) throws SQLException {
		try {
			Class.forName("org.h2.Driver");
			Connection testConnection = DriverManager.getConnection(URL, "sa", "");

			Field connectionField = Database.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			connectionField.set(target, testConnection);

			Field statementField = Database.class.getDeclaredField("statement");
			statementField.setAccessible(true);
			statementField.set(target, testConnection.createStatement());

			Method createTables = Database.class.getDeclaredMethod("createTables");
			createTables.setAccessible(true);
			createTables.invoke(target);
		} catch (ReflectiveOperationException e) {
			throw new SQLException("Could not connect Database to the test database", e);
		}
	}

	// In-memory User with the given flags: Admin, Contributor (Role1), Role2
	private User user(boolean admin, boolean contributor, boolean role2) {
		return new User("junit_x", "Password1!", "First", "", "Last", "First",
				"junit@test.com", admin, contributor, role2);
	}

	private void insertUser(String name, boolean admin, boolean contributor, boolean role2)
			throws SQLException {
		String sql = "INSERT INTO userDB (userName, password, firstName, middleName, lastName, "
				+ "preferredFirstName, emailAddress, adminRole, contributorRole, newRole2) "
				+ "VALUES (?, 'Password1!', 'First', '', 'Last', 'First', 'junit@test.com', ?, ?, ?)";
		try (PreparedStatement ps = raw.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setBoolean(2, admin);
			ps.setBoolean(3, contributor);
			ps.setBoolean(4, role2);
			ps.executeUpdate();
		}
	}

	// Reads one role column directly from userDB
	private boolean hasRole(String name, String column) throws SQLException {
		String sql = "SELECT " + column + " FROM userDB WHERE userName = ?";
		try (PreparedStatement ps = raw.prepareStatement(sql)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			assertTrue(rs.next(), "Test user " + name + " not found");
			return rs.getBoolean(1);
		}
	}

	private int countAdmins() throws SQLException {
		try (PreparedStatement ps =
				raw.prepareStatement("SELECT COUNT(*) FROM userDB WHERE adminRole = TRUE")) {
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}
	}

	private void deleteTestUsers() throws SQLException {
		try (PreparedStatement ps =
				raw.prepareStatement("DELETE FROM userDB WHERE userName LIKE 'junit_%'")) {
			ps.executeUpdate();
		}
	}
}