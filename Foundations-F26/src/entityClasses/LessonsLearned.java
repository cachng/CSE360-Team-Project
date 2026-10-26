package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/*******
 * <p> Title: LessonsLearned Class. </p>
 * 
 * <p> Description: An entity class that holds all the data for one lesson learned, together
 * with the input validation rules for each of its fields.  It supports the Contributor User
 * Stories for creating, viewing, editing, and deleting lessons, and it already holds the
 * attributes expected to be needed by the TP3 epics (field locking and experience
 * information) so the class does not have to be redesigned later. </p>
 * 
 * <p> Validation design: every setter validates its input before storing it, so an invalid
 * lesson can never exist in memory.  Each rule is also available as a public static
 * validate method that returns an empty string when the input is valid, or a helpful error
 * message when it is not.  This lets the GUI show the message next to the input field, and
 * lets tests check the result, without needing try/catch.  This follows the same pattern as
 * the input evaluators in the Foundations code. </p>
 * 
 * <p> CRUD support for a single lesson:</p>
 * <ul>
 *   <li> Create: {@link #LessonsLearned()} followed by the setters; the Database class stores
 *        it and assigns its id </li>
 *   <li> Read: the getter methods, such as {@link #getTitle()} </li>
 *   <li> Update: the setter methods, followed by {@link #touch()} to record the time of the
 *        change; the Database class writes the change back </li>
 *   <li> Delete: performed by the Database class and {@link LessonsLearnedList#remove(int)};
 *        {@link #isLocked()} tells the caller whether a Contributor may delete the lesson </li>
 * </ul>
 * 
 * @author TP2 Team
 * 
 * @version 1.00	2026-10-26 Initial version for TP2
 */
public class LessonsLearned {

	/**
	 * <p> Enumeration: Category </p>
	 * 
	 * <p> Description: The kinds of lesson a Contributor can choose from.  An enum is used
	 * instead of free text so every lesson has exactly one of a known set of categories,
	 * which makes filtering by category reliable and prevents misspelled categories. </p>
	 */
	public enum Category {
		/** A lesson that does not fit any of the other categories. */
		GENERAL,
		/** A lesson learned while doing a homework assignment. */
		HOMEWORK,
		/** A lesson learned while preparing for or taking an exam. */
		EXAM,
		/** A lesson learned while working on a project. */
		PROJECTS
	}

	// ----- Validation limits -----
	// These are public so the GUI can show the limits to the user and the tests can check
	// the exact boundary values (for example, a title of exactly TITLE_MAX characters).

	/** Maximum number of characters allowed in a title, so it fits on one line in lists. */
	public static final int TITLE_MAX = 100;

	/** Minimum number of characters in a description, so a lesson says something useful. */
	public static final int DESCRIPTION_MIN = 20;

	/** Maximum number of characters in a description, to keep lessons readable. */
	public static final int DESCRIPTION_MAX = 2000;

	/** Lowest valid course module number. */
	public static final int MODULE_MIN = 1;

	/** Highest valid course module number; adjust to the course's module count. */
	public static final int MODULE_MAX = 20;

	// ----- Instance fields -----

	/**
	 * Unique id assigned by the database when the lesson is first stored.  A value of 0
	 * means the lesson has not been saved yet.  Used to find, update, and delete a lesson.
	 */
	private int id;

	/** Short name of the lesson, shown in lists so Contributors can find it. */
	private String title;

	/** Full text of the lesson: what was learned and why it matters. */
	private String description;

	/**
	 * When the lesson was created, in ISO format (for example 2026-10-05T14:30:00).  Stored
	 * as text so it can be saved in the database and compared without conversion.
	 */
	private String dateCreated;

	/** When the lesson was last changed, in the same ISO format as dateCreated. */
	private String dateUpdated;

	/**
	 * Username of the Contributor who wrote the lesson.  Used to show a Contributor only
	 * their own lessons and to decide who may edit or delete a lesson.
	 */
	private String author;

	/** The course module the lesson relates to, between MODULE_MIN and MODULE_MAX. */
	private int module;

	/** The category of the lesson; never null once set through the setter. */
	private Category category;

	// ----- Roles (Foundations role names mapped to lesson roles) -----

	/** Name of the Foundations Admin role, which may change locked lessons. */
	public static final String ROLE_ADMIN = "Admin";

	/** Name of the Contributor role, which creates and manages its own lessons. */
	public static final String ROLE_CONTRIBUTOR = "Contributor";

	// ----- Locking and experience limits (in preparation for the TP3 epics) -----

	/**
	 * The names of the fields that may be locked.  A locked field cannot be changed by a
	 * Contributor.  Only fields in this list are accepted by {@link #setLockedFields(String)}.
	 */
	public static final List<String> LOCKABLE_FIELDS =
			Arrays.asList("title", "description", "category", "module");

	/** Maximum number of characters in each experience text field. */
	public static final int EXPERIENCE_TEXT_MAX = 2000;

	/** Largest number of hours that can be recorded, to catch typing mistakes. */
	public static final double HOURS_MAX = 1000;

	/**
	 * Comma-separated names of the locked fields, taken from LOCKABLE_FIELDS.  An empty
	 * string means nothing is locked.  Stored as one string so it fits in a single database
	 * column.
	 */
	private String lockedFields;

	/** Experience information: what work was done to learn this lesson. */
	private String whatWasDone;

	/** Experience information: how that work was carried out. */
	private String howItWasDone;

	/** Experience information: hours spent.  0 means no experience has been recorded yet. */
	private double hoursSpent;

	/** Experience information: who spent the effort, e.g. "Ana 3h testing, Ben 2h review". */
	private String effortNotes;

	/**
	 * <p> Constructor: LessonsLearned() </p>
	 * 
	 * <p> Description: Creates a new, unsaved lesson with default values.  The id is 0, the
	 * text fields are empty, the module is 1, the category is GENERAL, and both timestamps
	 * are set to the current time.  The caller then fills in the fields with the setters. </p>
	 */
	public LessonsLearned() {
		id = 0;
		title = "";
		description = "";
		// Nanoseconds are dropped so the timestamp stays short and matches the ISO format
		// that validateDate() accepts
		String now = LocalDateTime.now().withNano(0).toString();
		dateCreated = now;
		dateUpdated = now;
		author = "";
		module = 1;
		category = Category.GENERAL;
		lockedFields = "";
		whatWasDone = "";
		howItWasDone = "";
		hoursSpent = 0;
		effortNotes = "";
	}

	// ----- Getters (Read) -----

	/**
	 * <p> Method: getId() </p>
	 * @return the database id of this lesson, or 0 if it has not been saved yet
	 */
	public int getId() { return id; }

	/**
	 * <p> Method: getTitle() </p>
	 * @return the title of this lesson
	 */
	public String getTitle() { return title; }

	/**
	 * <p> Method: getDescription() </p>
	 * @return the description of this lesson
	 */
	public String getDescription() { return description; }

	/**
	 * <p> Method: getDateCreated() </p>
	 * @return when this lesson was created, in ISO format
	 */
	public String getDateCreated() { return dateCreated; }

	/**
	 * <p> Method: getDateUpdated() </p>
	 * @return when this lesson was last changed, in ISO format
	 */
	public String getDateUpdated() { return dateUpdated; }

	/**
	 * <p> Method: getAuthor() </p>
	 * @return the username of the Contributor who wrote this lesson
	 */
	public String getAuthor() { return author; }

	/**
	 * <p> Method: getModule() </p>
	 * @return the course module this lesson relates to
	 */
	public int getModule() { return module; }

	/**
	 * <p> Method: getCategory() </p>
	 * @return the category of this lesson
	 */
	public Category getCategory() { return category; }

	// ----- Setters (Create and Update) -----
	// Each setter validates its parameter before storing it, so a lesson in memory is
	// always valid.  An invalid value causes an IllegalArgumentException carrying the same
	// message the matching validate method returns.

	/**
	 * <p> Method: setId(int id) </p>
	 * 
	 * <p> Description: Sets the database id.  Only the database layer should call this,
	 * after inserting the lesson, so ids always match the stored record. </p>
	 * 
	 * @param id	the id assigned by the database
	 */
	public void setId(int id) { this.id = id; }

	/**
	 * <p> Method: setTitle(String title) </p>
	 * 
	 * <p> Description: Sets the title after validating it.  Leading and trailing spaces are
	 * removed before it is stored. </p>
	 * 
	 * @param title	the new title; must not be empty or longer than TITLE_MAX characters
	 * 
	 * @throws IllegalArgumentException	if the title is not valid
	 * 
	 * @see #validateTitle(String)
	 */
	public void setTitle(String title) {
		check(validateTitle(title));
		this.title = title.trim();
	}

	/**
	 * <p> Method: setDescription(String description) </p>
	 * 
	 * <p> Description: Sets the description after validating it.  Leading and trailing
	 * spaces are removed before it is stored. </p>
	 * 
	 * @param description	the new description; between DESCRIPTION_MIN and
	 * DESCRIPTION_MAX characters
	 * 
	 * @throws IllegalArgumentException	if the description is not valid
	 * 
	 * @see #validateDescription(String)
	 */
	public void setDescription(String description) {
		check(validateDescription(description));
		this.description = description.trim();
	}

	/**
	 * <p> Method: setAuthor(String author) </p>
	 * 
	 * <p> Description: Sets the author's username after checking it is not empty.  Whether
	 * that user actually exists is checked by the Database class. </p>
	 * 
	 * @param author	the username of the Contributor who wrote the lesson
	 * 
	 * @throws IllegalArgumentException	if the author is empty
	 * 
	 * @see #validateAuthor(String)
	 */
	public void setAuthor(String author) {
		check(validateAuthor(author));
		this.author = author.trim();
	}

	/**
	 * <p> Method: setModule(int module) </p>
	 * 
	 * <p> Description: Sets the course module after checking it is in range. </p>
	 * 
	 * @param module	a module number from MODULE_MIN to MODULE_MAX
	 * 
	 * @throws IllegalArgumentException	if the module is out of range
	 * 
	 * @see #validateModule(int)
	 */
	public void setModule(int module) {
		check(validateModule(module));
		this.module = module;
	}

	/**
	 * <p> Method: setCategory(Category category) </p>
	 * 
	 * <p> Description: Sets the category after checking it is not null. </p>
	 * 
	 * @param category	the new category
	 * 
	 * @throws IllegalArgumentException	if the category is null
	 * 
	 * @see #validateCategory(Category)
	 */
	public void setCategory(Category category) {
		check(validateCategory(category));
		this.category = category;
	}

	/**
	 * <p> Method: setDateCreated(String dateCreated) </p>
	 * 
	 * <p> Description: Sets the creation time.  Used by the Database class when loading a
	 * stored lesson, so the original creation time is kept. </p>
	 * 
	 * @param dateCreated	the creation time in ISO format, e.g. 2026-10-05T14:30:00
	 * 
	 * @throws IllegalArgumentException	if the date is empty or not in ISO format
	 * 
	 * @see #validateDate(String)
	 */
	public void setDateCreated(String dateCreated) {
		check(validateDate(dateCreated));
		this.dateCreated = dateCreated;
	}

	/**
	 * <p> Method: setDateUpdated(String dateUpdated) </p>
	 * 
	 * <p> Description: Sets the last-changed time.  Used by the Database class when loading
	 * a stored lesson; after an edit, call {@link #touch()} instead. </p>
	 * 
	 * @param dateUpdated	the last-changed time in ISO format
	 * 
	 * @throws IllegalArgumentException	if the date is empty or not in ISO format
	 * 
	 * @see #validateDate(String)
	 */
	public void setDateUpdated(String dateUpdated) {
		check(validateDate(dateUpdated));
		this.dateUpdated = dateUpdated;
	}

	/**
	 * <p> Method: touch() </p>
	 * 
	 * <p> Description: Sets dateUpdated to the current time.  Call this after any edit so
	 * the lesson shows when it was last changed.  It is a separate method, rather than being
	 * built into every setter, so that loading a lesson from the database does not change
	 * its stored update time. </p>
	 */
	public void touch() {
		this.dateUpdated = LocalDateTime.now().withNano(0).toString();
	}

	/**
	 * <p> Method: check(String error) </p>
	 * 
	 * <p> Description: Helper used by the setters.  Turns a non-empty error message from a
	 * validate method into an exception, so each setter needs only one line of validation. </p>
	 * 
	 * @param error	the result of a validate method; "" means valid
	 * 
	 * @throws IllegalArgumentException	if error is not empty
	 */
	private static void check(String error) {
		if (!error.isEmpty()) throw new IllegalArgumentException(error);
	}

	// ----- Static validations -----
	// Each returns "" when the input is valid, or an error message the GUI can show.

	/**
	 * <p> Method: validateTitle(String title) </p>
	 * 
	 * <p> Description: Checks that a title is present and no longer than TITLE_MAX
	 * characters, ignoring leading and trailing spaces. </p>
	 * 
	 * @param title	the title to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateTitle(String title) {
		if (title == null || title.trim().isEmpty())
			return "Title cannot be empty.";
		if (title.trim().length() > TITLE_MAX)
			return "Title cannot be longer than " + TITLE_MAX + " characters.";
		return "";
	}

	/**
	 * <p> Method: validateDescription(String description) </p>
	 * 
	 * <p> Description: Checks that a description is present and between DESCRIPTION_MIN and
	 * DESCRIPTION_MAX characters, ignoring leading and trailing spaces.  The minimum length
	 * stops lessons that are too short to be useful to other students. </p>
	 * 
	 * @param description	the description to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateDescription(String description) {
		if (description == null || description.trim().isEmpty())
			return "Description cannot be empty.";
		int len = description.trim().length();
		if (len < DESCRIPTION_MIN)
			return "Description must be at least " + DESCRIPTION_MIN + " characters.";
		if (len > DESCRIPTION_MAX)
			return "Description cannot be longer than " + DESCRIPTION_MAX + " characters.";
		return "";
	}

	/**
	 * <p> Method: validateAuthor(String author) </p>
	 * 
	 * <p> Description: Checks that an author username is present.  Whether the user exists
	 * is checked in the Database class, because only it can see the user table. </p>
	 * 
	 * @param author	the username to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateAuthor(String author) {
		if (author == null || author.trim().isEmpty())
			return "Author cannot be empty.";
		return "";
	}

	/**
	 * <p> Method: validateModule(int module) </p>
	 * 
	 * <p> Description: Checks that a module number is from MODULE_MIN to MODULE_MAX,
	 * inclusive. </p>
	 * 
	 * @param module	the module number to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateModule(int module) {
		if (module < MODULE_MIN || module > MODULE_MAX)
			return "Module must be between " + MODULE_MIN + " and " + MODULE_MAX + ".";
		return "";
	}

	/**
	 * <p> Method: validateCategory(Category category) </p>
	 * 
	 * <p> Description: Checks that a category has been chosen.  The error message lists the
	 * valid categories so the user knows what to pick. </p>
	 * 
	 * @param category	the category to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateCategory(Category category) {
		if (category == null)
			return "Category is required. Choose one of: " + Arrays.toString(Category.values());
		return "";
	}

	/**
	 * <p> Method: parseCategory(String text) </p>
	 * 
	 * <p> Description: Converts text typed or selected by the user into a Category.  The
	 * match ignores case and surrounding spaces, so "homework" and " Homework " both give
	 * HOMEWORK.  Returning null instead of throwing lets the caller pass the result straight
	 * to {@link #validateCategory(Category)} to get a helpful message. </p>
	 * 
	 * @param text	the category name to convert
	 * 
	 * @return the matching Category, or null if the text does not match any category
	 */
	public static Category parseCategory(String text) {
		if (text == null) return null;
		try {
			return Category.valueOf(text.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			// valueOf throws when there is no match; treat that as "no category"
			return null;
		}
	}

	/**
	 * <p> Method: validateDate(String date) </p>
	 * 
	 * <p> Description: Checks that a date is present and in ISO date-time format, such as
	 * 2026-10-05T14:30:00.  A single fixed format keeps stored dates consistent and sortable. </p>
	 * 
	 * @param date	the date text to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateDate(String date) {
		if (date == null || date.trim().isEmpty())
			return "Date cannot be empty.";
		try {
			LocalDateTime.parse(date);
		} catch (DateTimeParseException e) {
			return "Date must be in ISO format, e.g. 2026-10-05T14:30:00.";
		}
		return "";
	}

	/**
	 * <p> Method: validateLesson(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Checks all the required fields of a whole lesson before it is saved.
	 * The fields are checked in the order they appear on the form, and the first error found
	 * is returned, so the user is directed to the first field that needs fixing. </p>
	 * 
	 * @param lesson	the lesson to check
	 * 
	 * @return "" if the lesson is valid, otherwise the first error message found
	 */
	public static String validateLesson(LessonsLearned lesson) {
		if (lesson == null) return "Lesson cannot be null.";
		String[] errors = {
			validateTitle(lesson.getTitle()),
			validateDescription(lesson.getDescription()),
			validateAuthor(lesson.getAuthor()),
			validateModule(lesson.getModule()),
			validateCategory(lesson.getCategory())
		};
		for (String e : errors) if (!e.isEmpty()) return e;
		return "";
	}

	// ----- Locking (in preparation for the TP3 epics) -----

	/**
	 * <p> Method: getLockedFields() </p>
	 * @return the comma-separated names of the locked fields, or "" if none are locked
	 */
	public String getLockedFields() { return lockedFields; }

	/**
	 * <p> Method: isFieldLocked(String field) </p>
	 * 
	 * <p> Description: Reports whether one field is locked.  The edit window uses this to
	 * disable the input for a locked field so a Contributor cannot change it. </p>
	 * 
	 * @param field	a field name from LOCKABLE_FIELDS, such as "title"
	 * 
	 * @return true if that field is locked, otherwise false
	 */
	public boolean isFieldLocked(String field) {
		return Arrays.asList(lockedFields.split(",")).contains(field);
	}

	/**
	 * <p> Method: isLocked() </p>
	 * 
	 * <p> Description: Reports whether any field of the lesson is locked.  A lesson with any
	 * locked field counts as locked, and a Contributor cannot delete it. </p>
	 * 
	 * @return true if at least one field is locked, otherwise false
	 */
	public boolean isLocked() { return !lockedFields.isEmpty(); }

	/**
	 * <p> Method: setLockedFields(String csv) </p>
	 * 
	 * <p> Description: Sets which fields are locked, after checking every name is in
	 * LOCKABLE_FIELDS.  Spaces around the names are removed, so "title, module" is stored
	 * as "title,module".  A null or empty value unlocks every field. </p>
	 * 
	 * @param csv	comma-separated field names, or "" to unlock all fields
	 * 
	 * @throws IllegalArgumentException	if any name is not a lockable field
	 * 
	 * @see #validateLockedFields(String)
	 */
	public void setLockedFields(String csv) {
		check(validateLockedFields(csv));
		if (csv == null || csv.trim().isEmpty()) { lockedFields = ""; return; }
		// Rebuild the list without spaces so isFieldLocked() can match names exactly
		StringBuilder sb = new StringBuilder();
		for (String f : csv.split(",")) {
			if (sb.length() > 0) sb.append(",");
			sb.append(f.trim());
		}
		lockedFields = sb.toString();
	}

	/**
	 * <p> Method: validateLockedFields(String csv) </p>
	 * 
	 * <p> Description: Checks that every name in a comma-separated list is a lockable
	 * field.  An empty list is valid and means nothing is locked. </p>
	 * 
	 * @param csv	comma-separated field names to check
	 * 
	 * @return "" if valid, otherwise an error message naming the first bad field
	 */
	public static String validateLockedFields(String csv) {
		if (csv == null || csv.trim().isEmpty()) return "";
		for (String f : csv.split(","))
			if (!LOCKABLE_FIELDS.contains(f.trim()))
				return "'" + f.trim() + "' cannot be locked. Lockable fields: " + LOCKABLE_FIELDS;
		return "";
	}

	// ----- Experience information (in preparation for the TP3 epics) -----

	/**
	 * <p> Method: getWhatWasDone() </p>
	 * @return the description of the work done, or "" if no experience is recorded
	 */
	public String getWhatWasDone() { return whatWasDone; }

	/**
	 * <p> Method: getHowItWasDone() </p>
	 * @return how the work was carried out, or "" if no experience is recorded
	 */
	public String getHowItWasDone() { return howItWasDone; }

	/**
	 * <p> Method: getHoursSpent() </p>
	 * @return the hours spent, or 0 if no experience is recorded
	 */
	public double getHoursSpent() { return hoursSpent; }

	/**
	 * <p> Method: getEffortNotes() </p>
	 * @return notes on who spent the effort, or "" if no experience is recorded
	 */
	public String getEffortNotes() { return effortNotes; }

	/**
	 * <p> Method: hasExperience() </p>
	 * 
	 * <p> Description: Reports whether experience information has been recorded.  Hours
	 * must be greater than 0 when experience is set, so a value of 0 means none was set. </p>
	 * 
	 * @return true if experience information has been recorded, otherwise false
	 */
	public boolean hasExperience() { return hoursSpent > 0; }

	/**
	 * <p> Method: setExperience(String what, String how, double hours, String effort) </p>
	 * 
	 * <p> Description: Records the experience information for this lesson.  All four values
	 * are validated together and set together, so a lesson never has only part of its
	 * experience recorded. </p>
	 * 
	 * @param what	what work was done
	 * @param how	how the work was carried out
	 * @param hours	hours spent, greater than 0 and at most HOURS_MAX
	 * @param effort	who spent the effort
	 * 
	 * @throws IllegalArgumentException	if any value is not valid
	 * 
	 * @see #validateExperience(String, String, double, String)
	 */
	public void setExperience(String what, String how, double hours, String effort) {
		check(validateExperience(what, how, hours, effort));
		this.whatWasDone = what.trim();
		this.howItWasDone = how.trim();
		this.hoursSpent = hours;
		this.effortNotes = effort.trim();
	}

	/**
	 * <p> Method: validateExperience(String what, String how, double hours, String effort) </p>
	 * 
	 * <p> Description: Checks all four experience values in form order and returns the first
	 * error found. </p>
	 * 
	 * @param what	what work was done
	 * @param how	how the work was carried out
	 * @param hours	hours spent
	 * @param effort	who spent the effort
	 * 
	 * @return "" if all values are valid, otherwise the first error message found
	 */
	public static String validateExperience(String what, String how, double hours, String effort) {
		String e = validateExperienceText("What was done", what);
		if (e.isEmpty()) e = validateExperienceText("How it was done", how);
		if (e.isEmpty()) e = validateHours(hours);
		if (e.isEmpty()) e = validateExperienceText("Effort notes", effort);
		return e;
	}

	/**
	 * <p> Method: validateExperienceText(String label, String text) </p>
	 * 
	 * <p> Description: Checks one experience text field is present and no longer than
	 * EXPERIENCE_TEXT_MAX characters.  The label is put in the message so the user knows
	 * which field to fix. </p>
	 * 
	 * @param label	the field name to show in the error message
	 * @param text	the text to check
	 * 
	 * @return "" if valid, otherwise an error message that starts with the label
	 */
	public static String validateExperienceText(String label, String text) {
		if (text == null || text.trim().isEmpty()) return label + " cannot be empty.";
		if (text.trim().length() > EXPERIENCE_TEXT_MAX)
			return label + " cannot be longer than " + EXPERIENCE_TEXT_MAX + " characters.";
		return "";
	}

	/**
	 * <p> Method: validateHours(double hours) </p>
	 * 
	 * <p> Description: Checks that hours spent is greater than 0 and at most HOURS_MAX.
	 * NaN (not a number) is rejected explicitly because comparisons with NaN are always
	 * false and would otherwise let it through. </p>
	 * 
	 * @param hours	the number of hours to check
	 * 
	 * @return "" if valid, otherwise an error message
	 */
	public static String validateHours(double hours) {
		if (Double.isNaN(hours) || hours <= 0) return "Hours spent must be greater than 0.";
		if (hours > HOURS_MAX) return "Hours spent cannot be more than " + HOURS_MAX + ".";
		return "";
	}
}