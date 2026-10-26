package contributorTests;

import entityClasses.LessonLearned;

/**
 * <p> Title: LessonLearnedTestbed </p>
 *
 * <p> Description: A set of semi-automated tests for the LessonLearned entity
 *  class. Each test feeds one input to the class under test and compares the
 *  actual result with the expected result, then prints a PASS or FAIL line.
 *  Tests come in pairs: a positive test uses valid input that should be
 *  accepted, and a negative test uses invalid input that should be rejected. </p>
 *
 * <p> Reading the output: each line is "Test N: PASS" or "Test N: FAIL". A FAIL
 *  means the class did something other than what the requirement expects. The
 *  final line shows how many tests passed and failed. </p>
 */
public class LessonLearnedTestbed {

	static int passed = 0, failed = 0;

	/**
	 * <p> Method: check </p>
	 *
	 * <p> Description: Judges one test. The lesson is valid when validate()
	 *  returns an empty string. The test passes when that actual result matches
	 *  the expected result, so a correctly rejected bad input is a PASS. </p>
	 *
	 * @param num the test number, used in the printout
	 *
	 * @param expectValid true if the input should be accepted, false if it should be rejected
	 *
	 * @param error the String returned by the lesson's validate() method
	 */
	static void check(int num, boolean expectValid, String error) {
		boolean actualValid = error.isEmpty();
		boolean ok = (actualValid == expectValid);
		System.out.println("Test " + num + ": " + (ok ? "PASS" : "FAIL")
				+ (error.isEmpty() ? "" : "  (" + error + ")"));
		if (ok) passed++; else failed++;
	}

	/**
	 * <p> Method: test1_validLesson </p>
	 *
	 * <p> Requirement: FILL IN - the Contributor user story that says a lesson
	 *  with all required fields is accepted (e.g. "Cx: a contributor can create
	 *  a lesson when every field is valid"). </p>
	 *
	 * <p> How it tests it: builds a LessonLearned with a good value in every
	 *  field, then calls validate() and expects it to return "" (valid). </p>
	 *
	 * <p> How to read the output: PASS means the valid lesson was accepted;
	 *  FAIL means validate() rejected a lesson that should have been allowed. </p>
	 */
	static void test1_validLesson() {
		LessonLearned good = new LessonLearned(1, "Save Early", "Commit often.", "Sean", "git");
		check(1, true, good.validate());
	}

	/**
	 * <p> Method: test2_emptyTitle </p>
	 *
	 * <p> Requirement: FILL IN - the Contributor user story that requires a
	 *  lesson to have a title (e.g. "Cx: a lesson must have a non-empty title"). </p>
	 *
	 * <p> How it tests it: builds a LessonLearned whose title is the empty
	 *  string, then calls validate() and expects it to return an error (invalid). </p>
	 *
	 * <p> How to read the output: PASS means the empty title was correctly
	 *  rejected; FAIL means validate() let a title-less lesson through. </p>
	 */
	static void test2_emptyTitle() {
		LessonLearned bad = new LessonLearned(2, "", "Commit often.", "Sean", "git");
		check(2, false, bad.validate());
	}

	/**
	 * <p> Method: main </p>
	 *
	 * <p> Description: Runs every test in order and prints the pass/fail total. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		test1_validLesson();
		test2_emptyTitle();

		System.out.println(passed + " passed, " + failed + " failed");
	}
}
