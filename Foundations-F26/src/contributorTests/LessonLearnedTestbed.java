package contributorTests;

import entityClasses.LessonsLearned;

/** Tests for the LessonsLearned class. Prints PASS or FAIL for each test. */
public class LessonLearnedTestbed {

	/** Default constructor. */
	public LessonLearnedTestbed() {}

	/** Number of tests passed. */
	static int passed = 0;

	/** Number of tests failed. */
	static int failed = 0;

	/**
	 * Prints PASS if the result matches what was expected.
	 * @param num test number
	 * @param expectValid true if the input should be accepted
	 * @param error the result ("" means valid)
	 */
	static void check(int num, boolean expectValid, String error) {
		boolean actualValid = error.isEmpty();
		boolean ok = (actualValid == expectValid);
		System.out.println("Test " + num + ": " + (ok ? "PASS" : "FAIL")
				+ (error.isEmpty() ? "" : "  (" + error + ")"));
		if (ok) passed++; else failed++;
	}

	/** Requirement: FILL IN. A valid lesson is accepted. PASS = accepted. */
	static void test1_validLesson() {
		LessonsLearned good = new LessonsLearned();
		good.setTitle("Save Early");
		good.setDescription("Commit often so a bad edit is easy to undo.");
		good.setAuthor("Sean");
		good.setModule(2);
		good.setCategory(LessonsLearned.Category.HOMEWORK);
		check(1, true, LessonsLearned.validateLesson(good));
	}

	/** Requirement: FILL IN. An empty title is rejected. PASS = rejected. */
	static void test2_emptyTitle() {
		check(2, false, LessonsLearned.validateTitle(""));
	}

	/**
	 * Runs every test and prints the total.
	 * @param args not used
	 */
	public static void main(String[] args) {
		test1_validLesson();
		test2_emptyTitle();

		System.out.println(passed + " passed, " + failed + " failed");
	}
}
