package staffAnalyticsTestbed;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class staffAnalyticsTest {

	staffAnalytics s = new staffAnalytics();
	
	/***
	 * <p> Title: testBase Test/"method" </p>
	 *
	 * <p> Description: This tests if the base case begins at a
	 * known state. </p>
	 * 
	 */
	@Test
	void testBase() {
		String expectation = "Category 1 has 0 lessonsLearned\nCategory 2 has 0 lessonsLearned\n";
		expectation += "Category 3 has 0 lessonsLearned\nCategory 4 has 0 lessonsLearned\n";
		expectation += "Category 5 has 0 lessonsLearned\nCategory 6 has 0 lessonsLearned\n";
		assertEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test1 Test/"method" </p>
	 *
	 * <p> Description: This tests if the first insertion is handled
	 * correctly from the positive perspective. </p>
	 * 
	 */
	@Test
	void test1() {
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 1 lessonsLearned\nCategory 4 has 0 lessonsLearned\n";
		expectation += "Category 5 has 0 lessonsLearned\nCategory 6 has 0 lessonsLearned\n";
		assertEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test2 Test/"method" </p>
	 *
	 * <p> Description: This tests if the first insertion is handled
	 * correctly from the negative perspective. </p>
	 * 
	 */
	@Test
	void test2(){
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 1 lessonsLearned\nCategory 4 has 1 lessonsLearned\n";
		expectation += "Category 5 has 0 lessonsLearned\nCategory 6 has 0 lessonsLearned\n";
		assertNotEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test3 Test/"method" </p>
	 *
	 * <p> Description: This tests if the second insertion is handled
	 * correctly from the positive perspective. </p>
	 * 
	 */
	@Test
	void test3() {
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		s.learnLessonInCat(2);
		s.learnLessonInCat(3);
		s.learnLessonInCat(4);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 2 lessonsLearned\nCategory 4 has 1 lessonsLearned\n";
		expectation += "Category 5 has 1 lessonsLearned\nCategory 6 has 0 lessonsLearned\n";
		assertEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test4 Test/"method" </p>
	 *
	 * <p> Description: This tests if the second insertion is handled
	 * correctly from the negative perspective. </p>
	 * 
	 */
	@Test
	void test4(){
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		s.learnLessonInCat(2);
		s.learnLessonInCat(3);
		s.learnLessonInCat(4);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 2 lessonsLearned\nCategory 4 has 1 lessonsLearned\n";
		expectation += "Category 5 has 1 lessonsLearned\nCategory 6 has 1 lessonsLearned\n";
		assertNotEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test5 Test/"method" </p>
	 *
	 * <p> Description: This tests if the third insertion is handled
	 * correctly from the positive perspective. </p>
	 * 
	 */
	@Test
	void test5() {
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		s.learnLessonInCat(2);
		s.learnLessonInCat(3);
		s.learnLessonInCat(4);
		s.learnLessonInCat(2);
		s.learnLessonInCat(4);
		s.learnLessonInCat(5);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 3 lessonsLearned\nCategory 4 has 1 lessonsLearned\n";
		expectation += "Category 5 has 2 lessonsLearned\nCategory 6 has 1 lessonsLearned\n";
		assertEquals(expectation, s.currentAnalysis());
	}
	
	/***
	 * <p> Title: test6 Test/"method" </p>
	 *
	 * <p> Description: This tests if the third insertion is handled
	 * correctly from the negative perspective. </p>
	 * 
	 */
	@Test
	void test6(){
		s.learnLessonInCat(0);
		s.learnLessonInCat(1);
		s.learnLessonInCat(2);
		s.learnLessonInCat(2);
		s.learnLessonInCat(3);
		s.learnLessonInCat(4);
		s.learnLessonInCat(2);
		s.learnLessonInCat(4);
		s.learnLessonInCat(5);
		String expectation = "Category 1 has 1 lessonsLearned\nCategory 2 has 1 lessonsLearned\n";
		expectation += "Category 3 has 3 lessonsLearned\nCategory 4 has 1 lessonsLearned\n";
		expectation += "Category 5 has 1 lessonsLearned\nCategory 6 has 1 lessonsLearned\n";
		assertNotEquals(expectation, s.currentAnalysis());
	}

}
