package hw2;

import java.sql.SQLException;

import database.Database;
import entityClasses.LessonLearned;

/*******
 * <p> Title: HW2Tests Class </p>
 *
 * <p> Description: Runs the test cases for the Lessons Learned CRUD and input
 *  validation. Each test prints [PASS] or [FAIL] and the name of the test, and a
 *  count is printed at the end. The validation tests check that bad input is
 *  caught and that good input is accepted. The CRUD tests check that create, read,
 *  update, search, and delete work against the database. </p>
 *
 * <p> Copyright: Sean Anderson © 2026 </p>
 *
 * @author Sean Anderson
 */
public class HW2Tests {

	private static int passed = 0;
	private static int failed = 0;

	/*****
	 * <p> Method: void check(String name, boolean ok) </p>
	 *
	 * <p> Description: Prints [PASS] or [FAIL] for one test and counts the result. </p>
	 *
	 * @param name the name of the test
	 *
	 * @param ok true if the test passed, false if it failed
	 */
	private static void check(String name, boolean ok) {
		if (ok) {
			passed++;
			System.out.println("[PASS] " + name);
		} else {
			failed++;
			System.out.println("[FAIL] " + name);
		}
	}

	/*****
	 * <p> Method: void clear(Database db) </p>
	 *
	 * <p> Description: Deletes every lesson so each test run starts clean. </p>
	 *
	 * @param db the database to clear
	 */
	private static void clear(Database db) {
		for (LessonLearned lesson : db.getAllLessons()) {
			db.deleteLesson(lesson.getId());
		}
	}

	/*****
	 * <p> Method: void main(String[] args) </p>
	 *
	 * <p> Description: Runs every test case from top to bottom. </p>
	 *
	 * @param args not used
	 *
	 * @throws SQLException if the database cannot be reached
	 */
	public static void main(String[] args) throws SQLException {
		Database db = new Database();
		db.connectToDatabase();
		clear(db);

		System.out.println("=== Input validation tests (negative + positive) ===");

		check("A valid lesson passes validation",
				new LessonLearned(0, "Good title", "Good body text.", "Sean", "Database").validate().isEmpty());

		check("An empty title is rejected",
				!new LessonLearned(0, "", "A good body.", "Sean", "Database").validate().isEmpty());

		check("A too-short title is rejected",
				!new LessonLearned(0, "ab", "A good body.", "Sean", "Database").validate().isEmpty());

		check("A too-long title is rejected",
				!new LessonLearned(0, "x".repeat(101), "A good body.", "Sean", "Database").validate().isEmpty());

		check("A title with an invalid character is rejected",
				!new LessonLearned(0, "Bad\u0001title", "A good body.", "Sean", "Database").validate().isEmpty());

		check("An empty body is rejected",
				!new LessonLearned(0, "Good title", "", "Sean", "Database").validate().isEmpty());

		check("An empty author is rejected",
				!new LessonLearned(0, "Good title", "A good body.", "", "Database").validate().isEmpty());

		check("An empty category is rejected",
				!new LessonLearned(0, "Good title", "A good body.", "Sean", "").validate().isEmpty());

		check("A negative id is rejected",
				!new LessonLearned(-1, "Good title", "A good body.", "Sean", "Database").validate().isEmpty());

		System.out.println("\n=== CRUD tests (positive) ===");

		// CREATE
		db.createLesson(new LessonLearned(0, "Keep it DRY", "Do not repeat yourself.", "Sean", "Design"));
		check("Create adds exactly one lesson", db.getAllLessons().size() == 1);

		// READ ONE
		int id = db.getAllLessons().get(0).getId();
		LessonLearned read = db.getLesson(id);
		check("Read returns the lesson that was saved",
				read != null && read.getTitle().equals("Keep it DRY"));

		// READ a missing id
		check("Read of a missing id returns null", db.getLesson(999999) == null);

		// UPDATE
		read.setTitle("Keep code DRY");
		db.updateLesson(read);
		check("Update changes the stored title",
				db.getLesson(id).getTitle().equals("Keep code DRY"));

		// SEARCH that matches (returns a subset)
		db.createLesson(new LessonLearned(0, "Test early", "Write tests first.", "Sean", "Testing"));
		check("Search returns a non-empty subset when there is a match",
				db.searchLessons("tests").size() >= 1);

		// SEARCH that matches nothing (empty subset)
		check("Search returns an empty subset when there is no match",
				db.searchLessons("zzznotfound").isEmpty());

		// DELETE
		db.deleteLesson(id);
		check("Delete removes the lesson", db.getLesson(id) == null);

		System.out.println("\n" + passed + " passed, " + failed + " failed.");
		db.closeConnection();
	}
}
