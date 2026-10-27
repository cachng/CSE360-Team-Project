package contributorTests;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearned.Category;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: LessonLearnedTestbed Class. </p>
 *
 * <p> Description: Tests for the LessonsLearned and LessonsLearnedList classes, in the style
 * of the Foundations PasswordEvaluationTestbed.  Run it as a Java application.  Each test
 * prints one line with PASS or FAIL, and the totals are printed at the end.  A correct
 * implementation prints "22 passed, 0 failed". </p>
 *
 * <p> Every requirement has two tests: a positive test (something valid that must be
 * accepted) and a negative test (something wrong that must be rejected).  Requirement CN is
 * checked by Test 2N-1 (positive) and Test 2N (negative):</p>
 * <ul>
 *   <li> C1, Tests 1-2: a Contributor can create a lesson with valid fields </li>
 *   <li> C2, Tests 3-4: bad input is rejected </li>
 *   <li> C3, Tests 5-6: a Contributor can read their own lessons </li>
 *   <li> C4, Tests 7-8: an Admin can see every lesson, read-only </li>
 *   <li> C5, Tests 9-10: a Contributor can update their own lesson </li>
 *   <li> C6, Tests 11-12: fields can be locked, but the author cannot </li>
 *   <li> C7, Tests 13-14: a Contributor can delete their own unlocked lesson </li>
 *   <li> C8, Tests 15-16: a Contributor can add experience information </li>
 *   <li> C9, Tests 17-18: only the Contributor and Admin roles manage lessons </li>
 *   <li> C10, Tests 19-20: the lesson list holds, filters, and protects lessons </li>
 *   <li> C11, Tests 21-22: lessons survive being saved and reloaded </li>
 * </ul>
 *
 * @author Project Team 37
 *
 * @version 1.00	2026-10-26 22 tests, one positive and one negative per requirement
 */
public class LessonLearnedTestbed {

	/** Default constructor.  Not used, since every method is static. */
	public LessonLearnedTestbed() {}

	/** Number of tests passed. */
	public static int passed = 0;

	/** Number of tests failed. */
	public static int failed = 0;

	/**
	 * <p> Method: check(int num, String what, boolean expectValid, String error) </p>
	 *
	 * <p> Description: Prints one line saying whether the test passed.  The result is passed
	 * in as a String, like the validate methods return: "" means the input was accepted, and
	 * anything else is the error message saying why it was rejected.  The test passes when
	 * that matches what was expected. </p>
	 *
	 * @param num			the test number
	 * @param what			a short description of the test
	 * @param expectValid	true if the input should be accepted, false if it should be rejected
	 * @param error			the result: "" means accepted, otherwise the error message
	 */
	public static void check(int num, String what, boolean expectValid, String error) {
		boolean actualValid = error.isEmpty();			// "" means the input was accepted
		boolean ok = (actualValid == expectValid);		// PASS when it matches the expectation
		System.out.println("Test " + num + ": " + (ok ? "PASS" : "FAIL") + " - " + what
				+ (error.isEmpty() ? "" : "  (" + error + ")"));
		if (ok) passed++; else failed++;
	}

	/**
	 * <p> Method: makeValidLesson() </p>
	 *
	 * <p> Description: Builds a lesson where every field is valid.  Tests change one field at
	 * a time from this starting point, so a FAIL points at that one field's rule. </p>
	 *
	 * @return a lesson that passes validateLesson()
	 */
	public static LessonsLearned makeValidLesson() {
		LessonsLearned lesson = new LessonsLearned();
		lesson.setTitle("Save Early");
		lesson.setDescription("Commit often so a bad edit is easy to undo.");
		lesson.setAuthor("Sean");
		lesson.setModule(2);
		lesson.setCategory(Category.HOMEWORK);
		return lesson;
	}

	/**
	 * <p> Method: makeLesson(int id, String author, Category category) </p>
	 *
	 * <p> Description: Builds a valid lesson with a given id, author, and category, as if it
	 * had been loaded from the database. </p>
	 *
	 * @param id		the lesson's database id
	 * @param author	the username of the Contributor who wrote it
	 * @param category	the lesson's category
	 * @return a valid lesson with those values
	 */
	public static LessonsLearned makeLesson(int id, String author, Category category) {
		LessonsLearned lesson = makeValidLesson();
		lesson.setId(id);
		lesson.setAuthor(author);
		lesson.setCategory(category);
		return lesson;
	}

	/**
	 * <p> Method: makeSampleList() </p>
	 *
	 * <p> Description: Builds a list of three lessons: two by "Sean" (ids 1 and 2) and one by
	 * "Ana" (id 3).  Lessons 1 and 3 are HOMEWORK; lesson 2 is EXAM. </p>
	 *
	 * @return the sample list
	 */
	public static LessonsLearnedList makeSampleList() {
		LessonsLearnedList list = new LessonsLearnedList();
		list.add(makeLesson(1, "Sean", Category.HOMEWORK));
		list.add(makeLesson(2, "Sean", Category.EXAM));
		list.add(makeLesson(3, "Ana", Category.HOMEWORK));
		return list;
	}

	// ----- C1: a Contributor can create a lesson with valid fields -----

	/**
	 * Test 1, Requirement C1 (positive).  A lesson with every field valid is accepted by
	 * validateLesson(), which is called before a lesson is saved.  PASS = accepted.
	 */
	public static void test1_validLesson() {
		LessonsLearned lesson = makeValidLesson();				// every field is valid
		String error = LessonsLearned.validateLesson(lesson);	// "" means accepted
		check(1, "A lesson with every field valid is accepted", true, error);
	}

	/**
	 * Test 2, Requirement C1 (negative).  A new lesson with nothing filled in has an empty
	 * title, so it cannot be saved.  PASS = rejected with "Title cannot be empty."
	 */
	public static void test2_blankLesson() {
		LessonsLearned lesson = new LessonsLearned();			// nothing filled in
		String error = LessonsLearned.validateLesson(lesson);	// should give an error
		check(2, "A lesson with nothing filled in is rejected", false, error);
	}

	// ----- C2: bad input is rejected -----

	/**
	 * Test 3, Requirement C2 (positive, boundary).  A title of exactly TITLE_MAX (100)
	 * characters is the longest allowed and is accepted.  PASS = accepted.
	 */
	public static void test3_titleAtMax() {
		String title = "a".repeat(LessonsLearned.TITLE_MAX);	// exactly 100 characters
		String error = LessonsLearned.validateTitle(title);		// should be ""
		check(3, "A title of exactly 100 characters is accepted", true, error);
	}

	/**
	 * Test 4, Requirement C2 (negative, boundary).  A title of 101 characters is one over the
	 * limit and is rejected.  With Test 3, this shows the limit is exactly 100.
	 * PASS = rejected with "Title cannot be longer than 100 characters."
	 */
	public static void test4_titleOverMax() {
		String title = "a".repeat(LessonsLearned.TITLE_MAX + 1);	// 101 characters
		String error = LessonsLearned.validateTitle(title);			// should give an error
		check(4, "A title over 100 characters is rejected", false, error);
	}

	// ----- C3: a Contributor can read their own lessons -----

	/**
	 * Test 5, Requirement C3 (positive).  The home page lists "My Lessons" by filtering on the
	 * logged-in user's name.  This test does the same filter with "Sean" as the logged-in
	 * user.  PASS = Sean's list holds his 2 lessons.
	 */
	public static void test5_ownLessons() {
		LessonsLearnedList mine = makeSampleList().filterByAuthor("Sean");	// Sean is logged in
		String error = "";
		if (mine.size() != 2)									// Sean wrote 2 of the 3 lessons
			error = "Sean's list has " + mine.size() + " lessons, not 2";
		check(5, "The logged-in user's list holds their 2 lessons", true, error);
	}

	/**
	 * Test 6, Requirement C3 (negative).  Another user's lesson must not appear in the
	 * logged-in user's list.  Ana's lesson (id 3) must be filtered out of Sean's list.
	 * PASS = rejected, meaning Ana's lesson is not there.
	 */
	public static void test6_othersLessonHidden() {
		LessonsLearnedList mine = makeSampleList().filterByAuthor("Sean");	// Sean is logged in
		String error = "";
		if (mine.getById(3) == null)							// Ana's lesson was left out
			error = "Ana's lesson is not in Sean's list";
		check(6, "Another user's lesson is kept out of my list", false, error);
	}

	// ----- C4: an Admin can see every lesson, read-only -----

	/**
	 * Test 7, Requirement C4 (positive).  The full list holds every lesson from every
	 * author, so an Admin sees all 3.  PASS = accepted (3 lessons listed).
	 */
	public static void test7_seeAllLessons() {
		LessonsLearnedList all = makeSampleList();				// the list of all lessons
		String error = "";
		if (all.getAll().size() != 3)							// both Sean's and Ana's
			error = "only " + all.getAll().size() + " lessons listed, not 3";
		check(7, "The full list shows all 3 lessons from both authors", true, error);
	}

	/**
	 * Test 8, Requirement C4 (negative).  The list from getAll() is read-only, so nobody can
	 * add a lesson through it.  PASS = rejected (the add is refused).
	 */
	public static void test8_viewIsReadOnly() {
		LessonsLearnedList all = makeSampleList();
		String error = "";
		try {
			all.getAll().add(makeValidLesson());				// try to add through the view
		} catch (UnsupportedOperationException e) {
			error = "the read-only list refused the add";		// refused, as it should be
		}
		check(8, "Adding a lesson through the read-only view is refused", false, error);
	}

	// ----- C5: a Contributor can update their own lesson -----

	/**
	 * Test 9, Requirement C5 (positive).  Editing the description stores the new text and
	 * the lesson stays valid.  PASS = accepted.
	 */
	public static void test9_editLesson() {
		LessonsLearned lesson = makeValidLesson();
		String newText = "Push to the remote at the end of every session.";
		lesson.setDescription(newText);							// the edit
		lesson.touch();											// record when it was edited
		String error = LessonsLearned.validateLesson(lesson);	// still valid?
		if (error.isEmpty() && !lesson.getDescription().equals(newText))
			error = "the new description was not stored";
		check(9, "Editing the description stores the new text", true, error);
	}

	/**
	 * Test 10, Requirement C5 (negative).  An edit to module 25 is above MODULE_MAX (20), so
	 * setModule() refuses it.  PASS = rejected with "Module must be between 1 and 20."
	 */
	public static void test10_badEdit() {
		LessonsLearned lesson = makeValidLesson();
		String error = "";
		try {
			lesson.setModule(25);								// out of range
		} catch (IllegalArgumentException e) {
			error = e.getMessage();								// refused; keep the message
		}
		check(10, "Editing the module to 25 is rejected", false, error);
	}

	// ----- C6: fields can be locked, but the author cannot -----

	/**
	 * Test 11, Requirement C6 (positive).  "title" and "module" are lockable fields, so
	 * locking them is accepted.  PASS = accepted.
	 */
	public static void test11_lockFields() {
		String error = LessonsLearned.validateLockedFields("title,module");	// both lockable
		check(11, "Locking title and module is accepted", true, error);
	}

	/**
	 * Test 12, Requirement C6 (negative).  The author is not a lockable field, so locking it
	 * is rejected.  PASS = rejected with "'author' cannot be locked."
	 */
	public static void test12_lockAuthor() {
		String error = LessonsLearned.validateLockedFields("author");	// not lockable
		check(12, "Locking the author is rejected", false, error);
	}

	// ----- C7: a Contributor can delete their own unlocked lesson -----

	/**
	 * Test 13, Requirement C7 (positive).  Lesson 1 is unlocked, so it can be deleted by its
	 * id, leaving 2 lessons.  PASS = accepted.
	 */
	public static void test13_deleteLesson() {
		LessonsLearnedList list = makeSampleList();
		String error = "";
		if (!list.remove(1))									// delete lesson 1
			error = "lesson 1 was not removed";
		else if (list.size() != 2)								// the other 2 remain
			error = list.size() + " lessons left, not 2";
		check(13, "Deleting lesson 1 removes it", true, error);
	}

	/**
	 * Test 14, Requirement C7 (negative).  No lesson has id 99, so deleting it removes
	 * nothing.  PASS = rejected (nothing removed).
	 */
	public static void test14_deleteMissingId() {
		LessonsLearnedList list = makeSampleList();
		String error = "";
		if (!list.remove(99))									// no lesson has id 99
			error = "no lesson with id 99, nothing removed";
		check(14, "Deleting an id that does not exist is rejected", false, error);
	}

	// ----- C8: a Contributor can add experience information -----

	/**
	 * Test 15, Requirement C8 (positive).  Experience with all four values filled in and
	 * 3.5 hours is accepted.  PASS = accepted.
	 */
	public static void test15_addExperience() {
		String error = LessonsLearned.validateExperience(
				"Wrote unit tests",									// what was done
				"Ran them after every change",						// how it was done
				3.5,												// hours spent
				"Sean 3.5h testing");								// effort notes
		check(15, "Experience with 3.5 hours is accepted", true, error);
	}

	/**
	 * Test 16, Requirement C8 (negative).  Hours must be greater than 0, so experience with
	 * 0 hours is rejected.  PASS = rejected with "Hours spent must be greater than 0."
	 */
	public static void test16_zeroHours() {
		String error = LessonsLearned.validateExperience(
				"Wrote unit tests",
				"Ran them after every change",
				0,													// invalid: must be > 0
				"Sean testing");
		check(16, "Experience with 0 hours is rejected", false, error);
	}

	// ----- C9: only the Contributor and Admin roles manage lessons -----

	/**
	 * Test 17, Requirement C9 (positive).  "Contributor" is one of the lesson roles, so it
	 * is allowed to manage lessons.  PASS = accepted.
	 */
	public static void test17_contributorAllowed() {
		String role = "Contributor";
		String error = "";
		if (!role.equals(LessonsLearned.ROLE_CONTRIBUTOR) && !role.equals(LessonsLearned.ROLE_ADMIN))
			error = role + " is not a lesson role";
		check(17, "The Contributor role may manage lessons", true, error);
	}

	/**
	 * Test 18, Requirement C9 (negative).  "Student" is not one of the lesson roles, so it
	 * is not allowed to manage lessons.  PASS = rejected.
	 */
	public static void test18_studentNotAllowed() {
		String role = "Student";
		String error = "";
		if (!role.equals(LessonsLearned.ROLE_CONTRIBUTOR) && !role.equals(LessonsLearned.ROLE_ADMIN))
			error = role + " is not a lesson role";
		check(18, "The Student role may not manage lessons", false, error);
	}

	// ----- C10: the lesson list holds, filters, and protects lessons -----

	/**
	 * Test 19, Requirement C10 (positive).  Filtering by HOMEWORK returns lessons 1 and 3.
	 * PASS = accepted (2 lessons found).
	 */
	public static void test19_filterByCategory() {
		LessonsLearnedList homework = makeSampleList().filterByCategory(Category.HOMEWORK);
		String error = "";
		if (homework.size() != 2)								// lessons 1 and 3 are HOMEWORK
			error = homework.size() + " HOMEWORK lessons found, not 2";
		check(19, "Filtering by HOMEWORK finds 2 lessons", true, error);
	}

	/**
	 * Test 20, Requirement C10 (negative).  The list refuses a null lesson, so it never holds
	 * an empty entry.  PASS = rejected with "Cannot add a null lesson."
	 */
	public static void test20_addNull() {
		LessonsLearnedList list = makeSampleList();
		String error = "";
		try {
			list.add(null);										// should be refused
		} catch (IllegalArgumentException e) {
			error = e.getMessage();								// refused; keep the message
		}
		check(20, "Adding a null lesson to the list is rejected", false, error);
	}

	// ----- C11: lessons survive being saved and reloaded -----

	/**
	 * Test 21, Requirement C11 (positive).  A stored date in ISO format, as the Database
	 * saves it, is accepted when the lesson is loaded back.  PASS = accepted.
	 */
	public static void test21_goodStoredDate() {
		String error = LessonsLearned.validateDate("2026-10-05T14:30:00");	// ISO format
		check(21, "A stored date in ISO format is accepted", true, error);
	}

	/**
	 * Test 22, Requirement C11 (negative).  A stored date that is not in ISO format is
	 * damaged data and is rejected when the lesson is loaded.
	 * PASS = rejected with "Date must be in ISO format."
	 */
	public static void test22_badStoredDate() {
		String error = LessonsLearned.validateDate("yesterday");	// not ISO format
		check(22, "A stored date that is not ISO format is rejected", false, error);
	}

	/**
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Runs all 22 tests and prints the totals. </p>
	 *
	 * @param args	not used
	 */
	public static void main(String[] args) {
		test1_validLesson();
		test2_blankLesson();
		test3_titleAtMax();
		test4_titleOverMax();
		test5_ownLessons();
		test6_othersLessonHidden();
		test7_seeAllLessons();
		test8_viewIsReadOnly();
		test9_editLesson();
		test10_badEdit();
		test11_lockFields();
		test12_lockAuthor();
		test13_deleteLesson();
		test14_deleteMissingId();
		test15_addExperience();
		test16_zeroHours();
		test17_contributorAllowed();
		test18_studentNotAllowed();
		test19_filterByCategory();
		test20_addNull();
		test21_goodStoredDate();
		test22_badStoredDate();

		System.out.println(passed + " passed, " + failed + " failed");
	}
}