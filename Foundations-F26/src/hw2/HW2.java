package hw2;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.LessonLearned;

/*******
 * <p> Title: HW2 Class </p>
 *
 * <p> Description: A small console app that shows the Lessons Learned CRUD working
 *  on top of the Foundations Database package. It creates lessons (checking input
 *  first), reads them back, reads one, updates one, searches for a subset, and
 *  deletes one. Everything is saved in the H2 database, so the data is still there
 *  the next time this runs. </p>
 *
 * <p> Copyright: Sean Anderson © 2026 </p>
 *
 * @author Sean Anderson
 */
public class HW2 {

	/*****
	 * <p> Method: void saveLesson(Database db, LessonLearned lesson) </p>
	 *
	 * <p> Description: Tries to save a lesson. First it checks the lesson with
	 *  validate(). If the lesson is okay it is created in the database. If not, the
	 *  helpful error message is printed and nothing is saved. </p>
	 *
	 * @param db the database to save into
	 *
	 * @param lesson the lesson to check and save
	 *
	 * @throws SQLException if the create fails
	 */
	private static void saveLesson(Database db, LessonLearned lesson) throws SQLException {
		String error = lesson.validate();
		if (error.isEmpty()) {
			db.createLesson(lesson);
			System.out.println("Saved: " + lesson.getTitle());
		} else {
			System.out.println("Rejected: " + error);
		}
	}

	/*****
	 * <p> Method: void printAll(Database db) </p>
	 *
	 * <p> Description: Prints every lesson in the database as "id: title". </p>
	 *
	 * @param db the database to read from
	 */
	private static void printAll(Database db) {
		for (LessonLearned lesson : db.getAllLessons()) {
			System.out.println("  " + lesson.getId() + ": " + lesson.getTitle());
		}
	}

	/*****
	 * <p> Method: void main(String[] args) </p>
	 *
	 * <p> Description: Runs the full CRUD demo from start to finish. </p>
	 *
	 * @param args not used
	 *
	 * @throws SQLException if the database cannot be reached
	 */
	public static void main(String[] args) throws SQLException {
		Database db = new Database();
		db.connectToDatabase();

		// Start fresh so the demo looks clean each time it runs.
		for (LessonLearned lesson : db.getAllLessons()) {
			db.deleteLesson(lesson.getId());
		}

		// CREATE: two good lessons, then one bad lesson to show input validation.
		System.out.println("CREATE:");
		saveLesson(db, new LessonLearned(0, "Use PreparedStatement",
				"It keeps user input from breaking the SQL.", "Sean", "Database"));
		saveLesson(db, new LessonLearned(0, "Refresh Eclipse",
				"Press F5 so new files show up.", "Sean", "Tools"));
		saveLesson(db, new LessonLearned(0, "",
				"This one has no title.", "Sean", "Oops"));

		// READ ALL
		System.out.println("\nREAD ALL:");
		printAll(db);

		// Grab the ids just made so the rest does not depend on fixed numbers.
		List<LessonLearned> all = db.getAllLessons();
		int firstId = all.get(0).getId();
		int lastId  = all.get(all.size() - 1).getId();

		// READ ONE
		System.out.println("\nREAD ONE (id " + firstId + "):");
		LessonLearned one = db.getLesson(firstId);
		System.out.println("  title: " + one.getTitle());
		System.out.println("  body:  " + one.getBody());

		// UPDATE
		System.out.println("\nUPDATE (id " + firstId + "):");
		one.setTitle("Always use PreparedStatement");
		db.updateLesson(one);
		System.out.println("  new title: " + db.getLesson(firstId).getTitle());

		// SEARCH (returns a subset)
		System.out.println("\nSEARCH for 'Eclipse':");
		for (LessonLearned lesson : db.searchLessons("Eclipse")) {
			System.out.println("  " + lesson.getId() + ": " + lesson.getTitle());
		}

		// DELETE
		System.out.println("\nDELETE (id " + lastId + "):");
		db.deleteLesson(lastId);
		printAll(db);

		db.closeConnection();
	}
}
