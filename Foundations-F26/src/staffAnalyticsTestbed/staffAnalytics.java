package staffAnalyticsTestbed;

/***
 * <p> Title: staffAnalytics Class </p>
 *
 * <p> Description: This is a basic, un-integrated class that shows an example
 *  of the logic behind how a proposed staff analytics page is supposed to work. </p>
 * 
 */

public class staffAnalytics{
	
	private int[] counts;
	
	/***
	 * <p> Title: staffAnalytics Constructor </p>
	 *
	 * <p> Description: This starts the basic memory that
	 * remembers how many of each of category of lessonsLearned
	 * were put onto the system. </p>
	 * 
	 */
	
	public staffAnalytics(){
		// used for tracking how many lessonsLearned there are among 6 enumerated categories
		counts = new int[6];
		// assures that the list starts at a known state
		for(int i = 0; i < 6; i++) {
			counts[i] = 0;
		}
	}
	
	/***
	 * <p> Title: learnLessonInCat Method </p>
	 *
	 * <p> Description: This increments the counter associated with
	 * the integer inputed. For any number not within the range of 0 to 5
	 * will be interpreted as its divide-ability by 6. </p>
	 * 
	 */
	
	public void learnLessonInCat(int c){
		// % 6 is there to make sure the method can handle any integer in accordance with its remainder /6
		counts[(c % 6)]++;
	}
	
	/***
	 * <p> Title: currentAnalysis Method </p>
	 *
	 * <p> Description: Sends a string to explain the state of
	 * the counts in each category. </p>
	 * 
	 */
	
	public String currentAnalysis() {
		String out = "";
		for(int i = 0; i < 6; i++){
			out += "Category " + (i+1) + " has " + counts[i] + " lessonsLearned\n";
		}
		return out;
	}
	
}