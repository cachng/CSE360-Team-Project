package entityClasses;

import java.util.Arrays;

/*******
 * <p> Title: CoachingPrompt Class. </p>
 *
 * <p> Description: One automated coaching prompt for the TP3 aspect "Staff Automated Feedback
 * &amp; AI Coaching Prompts".  A prompt is a short piece of advice that staff write, plus a rule
 * that decides when a student sees it while writing a lesson: which lesson field it is about
 * (the field) and what has to be true of that field (the trigger and its setting).  For example,
 * "Say what you would do differently next time" when the DESCRIPTION is SHORTER_THAN 20
 * characters.</p>
 *
 * <p> Like LessonsLearned, every rule has a static validator that returns "" when the value is
 * valid or a helpful error message, and every setter refuses a value its validator rejects.</p>
 *
 * <p> Copyright: Sean Anderson © 2026 </p>
 *
 * @author Sean Anderson (Team 37)
 *
 * @version 1.00		2026-11-07 Initial version (HW3 Task 5 prototype)
 */
public class CoachingPrompt {

	/** The lesson fields a prompt can be about, matching the New Lesson Learned form. */
	public enum LessonField { TITLE, DESCRIPTION, CATEGORY, MODULE, EXPERIENCE }

	/**
	 * When a prompt appears: the field is EMPTY, its text is SHORTER_THAN a number of
	 * characters, or (category field only) the chosen category IS a given category.
	 */
	public enum Trigger { EMPTY, SHORTER_THAN, CATEGORY_IS }

	/** The longest prompt text, so the advice fits beside the lesson form. */
	public static final int TEXT_MAX = 200;
	/** The smallest SHORTER_THAN setting. */
	public static final int LENGTH_MIN = 1;
	/** The largest SHORTER_THAN setting; the longest lesson description is 2000 characters. */
	public static final int LENGTH_MAX = LessonsLearned.DESCRIPTION_MAX;
	/** The role name for course staff (instructors, TAs, graders). */
	public static final String ROLE_STAFF = "Staff";

	private int id;					// 0 until the database stores the prompt
	private String text;			// the advice the student sees
	private LessonField field;		// null until a rule is set
	private Trigger trigger;		// null until a rule is set
	private String triggerValue;	// "" for EMPTY, a number for SHORTER_THAN, a category name for CATEGORY_IS
	private boolean enabled;		// staff can turn a prompt off without deleting it
	private String updatedBy;		// the staff member who last created or changed it

	/*******
	 * <p> Method: CoachingPrompt() </p>
	 *
	 * <p> Description: A new prompt starts with no text and no rule, and turned on.  It is not
	 * valid until setText and setRule have been called.</p>
	 */
	public CoachingPrompt() {
		id = 0;
		text = "";
		field = null;
		trigger = null;
		triggerValue = "";
		enabled = true;
		updatedBy = "";
	}

	/*-********************************************************************************************

	Getters and setters

	*/

	/** @return the database id, or 0 if the prompt has not been stored */
	public int getId() { return id; }
	/** @return the advice the student sees */
	public String getText() { return text; }
	/** @return the lesson field the prompt is about */
	public LessonField getField() { return field; }
	/** @return when the prompt appears */
	public Trigger getTrigger() { return trigger; }
	/** @return the trigger's setting ("" for EMPTY) */
	public String getTriggerValue() { return triggerValue; }
	/** @return true if students can see this prompt */
	public boolean isEnabled() { return enabled; }
	/** @return the username of the staff member who last created or changed the prompt */
	public String getUpdatedBy() { return updatedBy; }

	/**
	 * Only the database layer should call this, after an insert.
	 *
	 * @param id is the id the database gave the prompt
	 */
	public void setId(int id) { this.id = id; }

	/*******
	 * <p> Method: void setText(String text) </p>
	 *
	 * <p> Description: Set the advice the student sees.  Spaces at either end are removed.</p>
	 *
	 * @param text is the advice, 1 to TEXT_MAX characters after trimming
	 *
	 * @throws IllegalArgumentException if validateText rejects the text
	 */
	public void setText(String text) {
		check(validateText(text));
		this.text = text.trim();
	}

	/*******
	 * <p> Method: void setRule(LessonField field, Trigger trigger, String value) </p>
	 *
	 * <p> Description: Set when the prompt appears.  The three parts are set together because
	 * whether a setting is valid depends on the trigger (a number for SHORTER_THAN, a category
	 * for CATEGORY_IS, nothing for EMPTY).</p>
	 *
	 * @param field is the lesson field the prompt is about
	 * @param trigger is the condition on that field
	 * @param value is the trigger's setting
	 *
	 * @throws IllegalArgumentException if validateRule rejects the combination
	 */
	public void setRule(LessonField field, Trigger trigger, String value) {
		check(validateRule(field, trigger, value));
		this.field = field;
		this.trigger = trigger;
		// Store one spelling of each setting (" 50 " -> "50", "exam" -> "EXAM") so the duplicate
		// check and the matching code never have to worry about spaces or case.
		String v = value == null ? "" : value.trim();
		if (trigger == Trigger.SHORTER_THAN) v = String.valueOf(Integer.parseInt(v));
		if (trigger == Trigger.CATEGORY_IS) v = v.toUpperCase();
		this.triggerValue = v;
	}

	/**
	 * Turn the prompt on or off.
	 *
	 * @param enabled is true if students should see the prompt
	 */
	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	/**
	 * Record who last created or changed the prompt.
	 *
	 * @param updatedBy is the staff member's username
	 */
	public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy == null ? "" : updatedBy; }

	private static void check(String error) {
		if (!error.isEmpty()) throw new IllegalArgumentException(error);
	}

	/*-********************************************************************************************

	Static validators: "" means valid, anything else is the message to show staff

	*/

	/*******
	 * <p> Method: String validateText(String text) </p>
	 *
	 * <p> Description: Prompt text must not be empty and must fit beside the lesson form.</p>
	 *
	 * @param text is the proposed advice
	 *
	 * @return "" if valid, else a helpful error message
	 */
	public static String validateText(String text) {
		if (text == null || text.trim().isEmpty())
			return "Prompt text cannot be empty.";
		if (text.trim().length() > TEXT_MAX)
			return "Prompt text cannot be longer than " + TEXT_MAX + " characters.";
		return "";
	}

	/*******
	 * <p> Method: String validateRule(LessonField field, Trigger trigger, String value) </p>
	 *
	 * <p> Description: Checks that the field, the trigger and the trigger's setting make sense
	 * together.</p>
	 *
	 * @param field is the lesson field the prompt is about
	 * @param trigger is the condition on that field
	 * @param value is the trigger's setting
	 *
	 * @return "" if valid, else a helpful error message
	 */
	public static String validateRule(LessonField field, Trigger trigger, String value) {
		if (field == null)
			return "Choose the lesson field the prompt is about: " + Arrays.toString(LessonField.values());
		if (trigger == null)
			return "Choose when the prompt appears: " + Arrays.toString(Trigger.values());
		String v = value == null ? "" : value.trim();
		switch (trigger) {
		case EMPTY:
			// A setting here would suggest it does something; refuse it so staff are not misled.
			if (!v.isEmpty()) return "An EMPTY prompt takes no setting.";
			return "";
		case SHORTER_THAN:
			int length;
			try {
				length = Integer.parseInt(v);
			} catch (NumberFormatException e) {
				return "SHORTER_THAN needs a whole number of characters.";
			}
			if (length < LENGTH_MIN || length > LENGTH_MAX)
				return "SHORTER_THAN must be between " + LENGTH_MIN + " and " + LENGTH_MAX + ".";
			return "";
		case CATEGORY_IS:
			// The category is chosen in the category box, so a CATEGORY_IS prompt shown beside
			// any other field would appear next to the wrong box.
			if (field != LessonField.CATEGORY)
				return "CATEGORY_IS can only be used on the CATEGORY field.";
			if (LessonsLearned.parseCategory(v) == null)
				return "CATEGORY_IS needs one of: " + Arrays.toString(LessonsLearned.Category.values());
			return "";
		default:
			return "Unknown trigger.";
		}
	}

	/*******
	 * <p> Method: String validatePrompt(CoachingPrompt prompt) </p>
	 *
	 * <p> Description: Returns the first error in a whole prompt, or "" if it can be saved.</p>
	 *
	 * @param prompt is the prompt to check
	 *
	 * @return "" if valid, else a helpful error message
	 */
	public static String validatePrompt(CoachingPrompt prompt) {
		if (prompt == null) return "Prompt cannot be null.";
		String error = validateText(prompt.getText());
		if (error.isEmpty()) error = validateRule(prompt.getField(), prompt.getTrigger(),
				prompt.getTriggerValue());
		return error;
	}

	/*******
	 * <p> Method: boolean isStaff(String role) </p>
	 *
	 * <p> Description: Only staff may change prompts.  Admin counts as staff so a new system,
	 * which starts with only an Admin, can still be set up.</p>
	 *
	 * @param role is the acting user's role
	 *
	 * @return true if the role may create, edit, turn off or delete prompts
	 */
	public static boolean isStaff(String role) {
		return ROLE_STAFF.equals(role) || LessonsLearned.ROLE_ADMIN.equals(role);
	}

	/*-********************************************************************************************

	Behaviour

	*/

	/*******
	 * <p> Method: boolean matches(String fieldText) </p>
	 *
	 * <p> Description: Decide whether this prompt's trigger is true for the text a student has
	 * typed into the prompt's field.  This only looks at the trigger; whether the prompt is
	 * turned on is checked by CoachingPromptList.promptsFor.</p>
	 *
	 * @param fieldText is the current text of the prompt's field; null counts as empty
	 *
	 * @return true if the student should see this prompt
	 */
	public boolean matches(String fieldText) {
		// The form hands over raw box text, so null and spaces both mean "nothing typed yet".
		String t = fieldText == null ? "" : fieldText.trim();
		if (trigger == null) return false;
		switch (trigger) {
		case EMPTY:
			return t.isEmpty();
		case SHORTER_THAN:
			return t.length() < Integer.parseInt(triggerValue);
		case CATEGORY_IS:
			return t.equalsIgnoreCase(triggerValue);
		default:
			return false;
		}
	}

	/*******
	 * <p> Method: boolean isDuplicateOf(CoachingPrompt other) </p>
	 *
	 * <p> Description: Two prompts are duplicates when they have the same rule and the same
	 * text, ignoring case.  A duplicate would show the student the same advice twice.</p>
	 *
	 * @param other is the prompt to compare with
	 *
	 * @return true if the two prompts are duplicates
	 */
	public boolean isDuplicateOf(CoachingPrompt other) {
		return other != null
				&& field == other.field
				&& trigger == other.trigger
				&& triggerValue.equals(other.triggerValue)
				&& text.equalsIgnoreCase(other.text);
	}
}
