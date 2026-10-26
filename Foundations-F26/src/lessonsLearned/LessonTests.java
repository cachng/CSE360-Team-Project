package lessonsLearned;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.User;

/*******
 * <p> Title: LessonTests Class. </p>
 * 
 * <p> Description: Automated tests for the lesson requirements.  Every test matches one row of
 * Test Cases.pdf and prints PASS or FAIL followed by the name of that row.  The first group
 * does not use the database, so a failure there points at the lesson classes.  The second group
 * uses the database.  The lessons created by the tests are deleted at the end so the tests can
 * be run again.</p>
 * 
 * <p> Copyright: Cardin Nguyen © 2026 </p>
 * 
 * @author Cardin Nguyen
 * 
 * @version 1.00		2026-10-07 Initial version
 *  
 */

public class LessonTests {

	/*-********************************************************************************************

	The attributes used by the tests
	
	*/
	
	private static Database db = new Database();
	private static int passed = 0;
	private static int failed = 0;
	private static List<Integer> createdIds = new ArrayList<Integer>();
	
	private static final String ADMIN = "Admin";
	private static final String CONTRIBUTOR = "Role1";
	
	private static final String ADMIN_USER = "hw2Admin";
	private static final String CONTRIB_ONE = "hw2Contrib1";
	private static final String CONTRIB_TWO = "hw2Contrib2";
	
	private static final String GOOD_DESCRIPTION =
			"This is a valid description with enough characters.";

	/*-********************************************************************************************

	Helper methods
	
	*/
	
	/**********
	 * <p> Method: void report(String name, boolean ok) </p>
	 * 
	 * <p> Description: Prints PASS or FAIL for one test and keeps the totals.</p>
	 * 
	 */
	private static void report(String name, boolean ok) {
		if (ok) {
			passed++;
			System.out.println("PASS: " + name);
		} else {
			failed++;
			System.out.println("FAIL: " + name);
		}
	}
	
	
	/**********
	 * <p> Method: void makeUser(String name, boolean admin, boolean role1) </p>
	 * 
	 * <p> Description: Registers a test user if that user is not in the database yet.</p>
	 * 
	 */
	private static void makeUser(String name, boolean admin, boolean role1) throws Exception {
		if (!db.doesUserExist(name)) {
			User user = new User(name, "Pass1234!", "Test", "", "User", "Test",
					name + "@example.com", admin, role1, false);
			db.register(user);
		}
	}
	
	
	/**********
	 * <p> Method: LessonsLearned build(String title, String author) </p>
	 * 
	 * <p> Description: Builds a valid lesson in memory.  It is not saved.</p>
	 * 
	 */
	private static LessonsLearned build(String title, String author) {
		LessonsLearned lesson = new LessonsLearned();
		lesson.setTitle(title);
		lesson.setDescription(GOOD_DESCRIPTION);
		lesson.setAuthor(author);
		lesson.setModule(2);
		lesson.setCategory(LessonsLearned.Category.HOMEWORK);
		return lesson;
	}
	
	
	/**********
	 * <p> Method: LessonsLearned create(String title, String author) </p>
	 * 
	 * <p> Description: Builds a valid lesson and saves it.  The id is remembered so the lesson
	 * can be deleted when the tests finish.</p>
	 * 
	 */
	private static LessonsLearned create(String title, String author) {
		LessonsLearned lesson = build(title, author);
		if (db.createLesson(lesson).isEmpty()) createdIds.add(lesson.getId());
		return lesson;
	}
	
	
	// Each of these returns true when the bad value was rejected.
	
	private static boolean titleRejected(String title) {
		try {
			new LessonsLearned().setTitle(title);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean descriptionRejected(String description) {
		try {
			new LessonsLearned().setDescription(description);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean moduleRejected(int module) {
		try {
			new LessonsLearned().setModule(module);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean categoryRejected(LessonsLearned.Category category) {
		try {
			new LessonsLearned().setCategory(category);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean authorRejected(String author) {
		try {
			new LessonsLearned().setAuthor(author);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean lockRejected(LessonsLearned lesson, String fields) {
		try {
			lesson.setLockedFields(fields);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static boolean nullAddRejected(LessonCollection collection) {
		try {
			collection.add(null);
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}
	
	private static String repeat(String text, int times) {
		String result = "";
		for (int i = 0; i < times; i++) {
			result = result + text;
		}
		return result;
	}

	/*-********************************************************************************************

	The main method runs every group of tests
	
	*/
	
	public static void main(String[] args) throws Exception {
		System.out.println("===== TESTS THAT DO NOT USE THE DATABASE, ONLY VALIDATION AND LESSONSLEARNED THINGS =====");
		testValidation();
		testLocking();
		testExperienceRules();
		testCollection();
		
		System.out.println("\n===== TESTS THAT USE THE DATABASE, CRUD STUFF =====");
		db.connectToDatabase();
		makeUser(ADMIN_USER, true, false);
		makeUser(CONTRIB_ONE, false, true);
		makeUser(CONTRIB_TWO, false, true);
		
		testCreateAndRead();
		testUpdate();
		testDelete();
		testExperience();
		testRoles();
		testPersistence();
		
		// An admin may delete locked lessons, so this removes everything the tests created
		for (int id : createdIds) db.deleteLesson(id, ADMIN_USER, ADMIN);
		
		System.out.println("\nPassed: " + passed + "   Failed: " + failed);
		db.closeConnection();
	}

	/*-********************************************************************************************

	Tests that do not use the database
	
	*/
	
	private static void testValidation() {
		report("Empty title", titleRejected(""));
		report("Title length", !titleRejected(repeat("x", 100))
				&& titleRejected(repeat("x", 101)));
		report("Description length", descriptionRejected(repeat("a", 19))
				&& !descriptionRejected(repeat("a", 20)));
		report("Module range", moduleRejected(0) && moduleRejected(21)
				&& !moduleRejected(1) && !moduleRejected(20));
		report("No category", categoryRejected(null));
		report("Empty author", authorRejected(""));
	}
	
	private static void testLocking() {
		LessonsLearned lesson = build("Lock me", "ann");
		report("New lesson", !lesson.isLocked());
		
		lesson.setLockedFields("title, category");
		report("Lock title and category", lesson.isFieldLocked("title")
				&& lesson.isFieldLocked("category") && !lesson.isFieldLocked("module"));
		report("Lock the author", lockRejected(lesson, "author"));
	}
	
	private static void testExperienceRules() {
		report("Good experience",
				LessonsLearned.validateExperience("a", "b", 3.5, "c").isEmpty());
		report("0 and -2 hours", !LessonsLearned.validateHours(0).isEmpty()
				&& !LessonsLearned.validateHours(-2).isEmpty());
		report("Empty \"what was done\"",
				!LessonsLearned.validateExperience("", "b", 1, "c").isEmpty());
	}
	
	private static void testCollection() {
		LessonCollection collection = new LessonCollection();
		report("New collection", collection.isEmpty());
		
		LessonsLearned one = build("One", "ann");
		one.setId(900);
		LessonsLearned two = build("Two", "bob");
		two.setId(901);
		two.setCategory(LessonsLearned.Category.EXAM);
		collection.add(one);
		collection.add(two);
		report("Add two lessons", collection.size() == 2 && collection.getById(901) == two);
		
		report("Filter by author, then category",
				collection.filterByAuthor("ann").size() == 1
				&& collection.filterByCategory(LessonsLearned.Category.EXAM).size() == 1);
		report("Filter with no matches", collection.filterByAuthor("nobody").isEmpty());
		report("Add null", nullAddRejected(collection));
		report("Remove by id", collection.remove(900) && !collection.remove(900));
		
		collection.clear();
		report("Clear", collection.isEmpty());
		
		LessonCollection big = new LessonCollection();
		for (int i = 1; i <= 5000; i++) {
			LessonsLearned lesson = build("Bulk", "ann");
			lesson.setId(i);
			big.add(lesson);
		}
		report("5,000 lessons", big.size() == 5000);
	}

	/*-********************************************************************************************

	Tests that use the database
	
	*/
	
	private static void testCreateAndRead() {
		LessonsLearned lesson = build("Merge conflicts", CONTRIB_ONE);
		String error = db.createLesson(lesson);
		if (error.isEmpty()) createdIds.add(lesson.getId());
		report("Save a valid lesson", error.isEmpty() && lesson.getId() > 0);
		
		String ghostError = db.createLesson(build("Ghost author", "nobodyHere"));
		report("Author is not a user",
				ghostError.equals("Author 'nobodyHere' is not an existing user."));
		
		LessonsLearned first = create("Contrib1 lesson", CONTRIB_ONE);
		create("Contrib2 lesson", CONTRIB_TWO);
		
		LessonCollection mine = db.getLessonsForUser(CONTRIB_ONE, CONTRIBUTOR);
		report("Contributor's own list", !mine.isEmpty()
				&& mine.filterByAuthor(CONTRIB_ONE).size() == mine.size());
		
		LessonCollection none = db.getLessonsForUser("hw2Nobody", CONTRIBUTOR);
		report("No lessons yet", none != null && none.isEmpty());
		
		LessonCollection all = db.getLessonsForUser(ADMIN_USER, ADMIN);
		report("Admin list", !all.filterByAuthor(CONTRIB_ONE).isEmpty()
				&& !all.filterByAuthor(CONTRIB_TWO).isEmpty());
		
		report("Read by id", db.getLesson(first.getId()).getTitle().equals("Contrib1 lesson"));
		report("Missing id", db.getLesson(-1) == null);
		report("Someone else's lesson",
				db.getLessonForUser(first.getId(), CONTRIB_TWO, CONTRIBUTOR) == null);
	}
	
	private static void testUpdate() {
		LessonsLearned saved = create("Original title", CONTRIB_ONE);
		
		LessonsLearned edit = db.getLesson(saved.getId());
		edit.setTitle("Edited title");
		db.updateLesson(edit, CONTRIB_ONE, CONTRIBUTOR);
		report("Edit an unlocked field",
				db.getLesson(saved.getId()).getTitle().equals("Edited title"));
		
		LessonsLearned lock = db.getLesson(saved.getId());
		lock.setLockedFields("title,category");
		db.updateLesson(lock, ADMIN_USER, ADMIN);
		LessonsLearned locked = db.getLesson(saved.getId());
		report("Admin locks fields",
				locked.isFieldLocked("title") && locked.isFieldLocked("category"));
		
		LessonsLearned blocked = db.getLesson(saved.getId());
		blocked.setTitle("Sneaky change");
		String lockedError = db.updateLesson(blocked, CONTRIB_ONE, CONTRIBUTOR);
		report("Edit the locked title",
				lockedError.equals("The title is locked and cannot be edited."));
		
		LessonsLearned desc = db.getLesson(saved.getId());
		desc.setDescription("A different but still valid description text.");
		report("Edit another field", db.updateLesson(desc, CONTRIB_ONE, CONTRIBUTOR).isEmpty());
		
		LessonsLearned unlock = db.getLesson(saved.getId());
		unlock.setLockedFields("");
		db.updateLesson(unlock, CONTRIB_ONE, CONTRIBUTOR);
		report("Remove the locks", db.getLesson(saved.getId()).isFieldLocked("title"));
		
		String otherError = db.updateLesson(db.getLesson(saved.getId()), CONTRIB_TWO, CONTRIBUTOR);
		report("Someone else's update",
				otherError.equals("You can only access lessons you created."));
	}
	
	private static void testDelete() {
		LessonsLearned free = create("Deletable", CONTRIB_ONE);
		String freeError = db.deleteLesson(free.getId(), CONTRIB_ONE, CONTRIBUTOR);
		report("Delete own unlocked lesson",
				freeError.isEmpty() && db.getLesson(free.getId()) == null);
		
		LessonsLearned locked = create("Locked lesson", CONTRIB_ONE);
		LessonsLearned toLock = db.getLesson(locked.getId());
		toLock.setLockedFields("description");
		db.updateLesson(toLock, ADMIN_USER, ADMIN);
		
		String lockedError = db.deleteLesson(locked.getId(), CONTRIB_ONE, CONTRIBUTOR);
		report("Delete own locked lesson", lockedError.equals(
				"This lesson has locked fields (description) and cannot be deleted.")
				&& db.getLesson(locked.getId()) != null);
		
		String otherError = db.deleteLesson(locked.getId(), CONTRIB_TWO, CONTRIBUTOR);
		report("Delete someone else's lesson",
				!otherError.isEmpty() && db.getLesson(locked.getId()) != null);
		
		String adminError = db.deleteLesson(locked.getId(), ADMIN_USER, ADMIN);
		report("Admin deletes a locked lesson",
				adminError.isEmpty() && db.getLesson(locked.getId()) == null);
		
		report("Missing id", db.deleteLesson(-1, ADMIN_USER, ADMIN).equals(
				"No lesson exists with id -1."));
	}
	
	private static void testExperience() {
		LessonsLearned saved = create("Experience lesson", CONTRIB_ONE);
		
		db.addExperience(saved.getId(), CONTRIB_ONE, CONTRIBUTOR, "Ran retro", "Whiteboard",
				3.5, "Ana 2h, Ben 1.5h");
		report("Save experience", db.getLesson(saved.getId()).getHoursSpent() == 3.5);
		
		report("0 hours", !db.addExperience(saved.getId(), CONTRIB_ONE, CONTRIBUTOR,
				"a", "b", 0, "c").isEmpty());
		report("Another contributor", !db.addExperience(saved.getId(), CONTRIB_TWO,
				CONTRIBUTOR, "a", "b", 1, "c").isEmpty());
		
		LessonsLearned toLock = db.getLesson(saved.getId());
		toLock.setLockedFields("title");
		db.updateLesson(toLock, ADMIN_USER, ADMIN);
		report("Locked lesson", db.addExperience(saved.getId(), CONTRIB_ONE, CONTRIBUTOR,
				"More", "Pairing", 2, "Ana 2h").isEmpty());
	}
	
	private static void testRoles() {
		LessonsLearned saved = create("Role test", CONTRIB_ONE);
		report("Role2", db.checkAccess(saved.getId(), CONTRIB_ONE, "Role2")
				.contains("cannot manage lessons"));
		report("Made-up role \"Hacker\"",
				!db.checkAccess(saved.getId(), CONTRIB_ONE, "Hacker").isEmpty());
	}
	
	private static void testPersistence() throws Exception {
		LessonsLearned saved = create("Persistent lesson", CONTRIB_ONE);
		int id = saved.getId();
		
		// Close and reopen the database to show the lesson was written to disk
		db.closeConnection();
		db = new Database();
		db.connectToDatabase();
		
		LessonsLearned back = db.getLesson(id);
		report("Restart", back != null && back.getTitle().equals("Persistent lesson"));
	}
}