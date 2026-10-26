package contributorTests;

import entityClasses.LessonsLearned;

/**
 * <p> Title: LessonLearnedTestbed </p>
 *
 * <p> Description: Semi-automated tests for the LessonsLearned class. Each test
 *  prints PASS or FAIL, and a total is printed at the end. </p>
 */
public class LessonLearnedTestbed {

	static int passed = 0, failed = 0;

	/**
	 * Prints PASS when the actual result matches the expected one.
	 *
	 * @param num the test number
	 * @param expectValid true if the input should be accepted
	 * @param error the validation result ("" means valid)
	 */
	static void check(int num, boolean expectValid, String error) {
		boolean ok = (error.isEmpty() == expectValid);
		System.out.println("Test " + num + ": " + (ok ? "PASS" : "FAIL")
				+ (error.isEmpty() ? "" : "  (" + error + ")"));
		if (ok) passed++; else failed++;
	}

	/**
	 * Requirement: FILL IN. A lesson with valid fields is accepted.
	 * PASS = the lesson was accepted.
	 */
	static void test1_validLesson() {
		LessonsLearned good = new LessonsLearned();
		good.setTitle("Save Early");
		good.setDescription("Commit often so a bad edit is easy to undo.");
		good.setAuthor("Sean");
		good.setModule(2);
		good.setCategory(LessonsLearned.Category.HOMEWORK);
		check(1, true, LessonsLearned.validateLesson(good));
	}

	/**
	 * Requirement: FILL IN. An empty title is rejected.
	 * PASS = the empty title was rejected.
	 */
	static void test2_emptyTitle() {
		check(2, false, LessonsLearned.validateTitle(""));
	}

	/**
	 * Runs every test and prints the total.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		test1_validLesson();
		test2_emptyTitle();

		System.out.println(passed + " passed, " + failed + " failed");
	}
}
