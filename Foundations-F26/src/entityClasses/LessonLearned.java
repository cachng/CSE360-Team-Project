package entityClasses;

/*******
 * <p> Title: LessonLearned Class </p>
 *
 * <p> Description: This class holds one lesson learned. It is the data for a
 *  single Ed Discussion style item: an id, a title, the body text, who wrote it,
 *  and a category. It just stores the data. The create, read, update, and delete
 *  work is done by the Database package. </p>
 *
 * <p> Copyright: Sean Anderson © 2026 </p>
 *
 * @author Sean Anderson
 */
public class LessonLearned {

	/*
	 * The private attributes for one lesson learned.
	 */
	private int    id;          // the key used to find, update, and delete a lesson
	private String title;       // a short title for the lesson
	private String body;        // the full text of the lesson
	private String author;      // who wrote it
	private String category;    // a tag so lessons can be grouped or searched

	/*****
	 * <p> Method: LessonLearned() </p>
	 *
	 * <p> Description: An empty lesson. Used before the fields are filled in. </p>
	 */
	public LessonLearned() {

	}

	/*****
	 * <p> Method: LessonLearned(int id, String title, String body, String author,
	 *      String category) </p>
	 *
	 * <p> Description: Makes a lesson with all of its fields set at once. </p>
	 *
	 * @param id the id for this lesson
	 *
	 * @param title the title for this lesson
	 *
	 * @param body the text for this lesson
	 *
	 * @param author who wrote this lesson
	 *
	 * @param category the tag for this lesson
	 */
	public LessonLearned(int id, String title, String body, String author, String category) {
		this.id       = id;
		this.title    = title;
		this.body     = body;
		this.author   = author;
		this.category = category;
	}

	/*****
	 * <p> Method: int getId() </p>
	 *
	 * <p> Description: Gives back the id. </p>
	 *
	 * @return the lesson's id
	 */
	public int getId() { return id; }

	/*****
	 * <p> Method: void setId(int id) </p>
	 *
	 * <p> Description: Sets the id. </p>
	 *
	 * @param id the new id
	 */
	public void setId(int id) { this.id = id; }

	/*****
	 * <p> Method: String getTitle() </p>
	 *
	 * <p> Description: Gives back the title. </p>
	 *
	 * @return the lesson's title
	 */
	public String getTitle() { return title; }

	/*****
	 * <p> Method: void setTitle(String title) </p>
	 *
	 * <p> Description: Sets the title. </p>
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) { this.title = title; }

	/*****
	 * <p> Method: String getBody() </p>
	 *
	 * <p> Description: Gives back the body text. </p>
	 *
	 * @return the lesson's body
	 */
	public String getBody() { return body; }

	/*****
	 * <p> Method: void setBody(String body) </p>
	 *
	 * <p> Description: Sets the body text. </p>
	 *
	 * @param body the new body
	 */
	public void setBody(String body) { this.body = body; }

	/*****
	 * <p> Method: String getAuthor() </p>
	 *
	 * <p> Description: Gives back the author. </p>
	 *
	 * @return who wrote the lesson
	 */
	public String getAuthor() { return author; }

	/*****
	 * <p> Method: void setAuthor(String author) </p>
	 *
	 * <p> Description: Sets the author. </p>
	 *
	 * @param author the new author
	 */
	public void setAuthor(String author) { this.author = author; }

	/*****
	 * <p> Method: String getCategory() </p>
	 *
	 * <p> Description: Gives back the category. </p>
	 *
	 * @return the lesson's category
	 */
	public String getCategory() { return category; }

	/*****
	 * <p> Method: void setCategory(String category) </p>
	 *
	 * <p> Description: Sets the category. </p>
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) { this.category = category; }

	/*****
	 * <p> Method: String validate() </p>
	 *
	 * <p> Description: Checks the fields for bad input and returns a short message
	 *  about the first problem it finds. Returns an empty string ("") when the
	 *  lesson is fine. The CRUD code calls this before saving so bad data is not
	 *  stored. </p>
	 *
	 * @return an empty String if the lesson is okay, else a message about what is wrong
	 */
	public String validate() {
		if (id < 0)
			return "Id can't be negative.";

		String error = checkField(title, "Title", 3, 100);
		if (!error.isEmpty())
			return error;

		error = checkField(body, "Body", 1, 5000);
		if (!error.isEmpty())
			return error;

		error = checkField(author, "Author", 1, 255);
		if (!error.isEmpty())
			return error;

		error = checkField(category, "Category", 1, 255);
		if (!error.isEmpty())
			return error;

		return "";
	}

	/*****
	 * <p> Method: String checkField(String value, String name, int minLen, int maxLen) </p>
	 *
	 * <p> Description: Checks one text field. It must not be empty, must fit inside
	 *  minLen and maxLen characters, and may only use letters, digits, spaces, and
	 *  common punctuation. Returns a helpful message about the first problem it
	 *  finds, or an empty string ("") when the field is okay. This uses the same
	 *  idea as the LessonShare evaluateTextInput method. </p>
	 *
	 * @param value the text to check
	 *
	 * @param name the field name to use in the message (like "Title")
	 *
	 * @param minLen the fewest characters allowed
	 *
	 * @param maxLen the most characters allowed
	 *
	 * @return an empty String if the field is okay, else a message about what is wrong
	 */
	private String checkField(String value, String name, int minLen, int maxLen) {
		if (value == null || value.isEmpty())
			return name + " can't be empty.";

		if (value.length() > maxLen)
			return name + " must be " + maxLen + " characters or less.";

		String allowedPunctuation = ".,!?;:'\"()[]{}-_/@#&*+=<>$%";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			boolean letter = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
			boolean digit  = (c >= '0' && c <= '9');
			boolean space  = (c == ' ' || c == '\t' || c == '\n' || c == '\r');
			boolean punct  = allowedPunctuation.indexOf(c) >= 0;
			if (!letter && !digit && !space && !punct)
				return name + " has an invalid character at position " + i + ".";
		}

		if (value.length() < minLen)
			return name + " must be at least " + minLen + " characters.";

		return "";
	}
}
