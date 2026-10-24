package lessonsLearned;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/*******
 * <p> Title: LessonsLearned Class. </p>
 * 
 * <p> Description: One lesson learned, plus the validation rules for its fields.  Setters
 * validate their input so an invalid lesson can never exist in memory.  The static validate
 * methods return an empty string when the input is valid, or a helpful error message when it
 * is not, so callers (and tests) can show or check the message without try/catch.</p>
 */
public class LessonsLearned {

	public enum Category { GENERAL, HOMEWORK, EXAM, PROJECTS }

	// ----- Validation limits -----
	public static final int TITLE_MAX = 100;
	public static final int DESCRIPTION_MIN = 20;
	public static final int DESCRIPTION_MAX = 2000;
	public static final int MODULE_MIN = 1;
	public static final int MODULE_MAX = 20; // adjust if necessary

	// ----- Instance fields -----
	private int id;                 // assigned by the database, 0 = not yet saved
	private String title;
	private String description;
	private String dateCreated;
	private String dateUpdated;
	private String author;
	private int module;
	private Category category;
	
	// ----- Roles (Foundation role names mapped to lesson roles) -----
	public static final String ROLE_ADMIN = "Admin";
	public static final String ROLE_CONTRIBUTOR = "Role1";

	// ----- Locking and experience limits -----
	public static final List<String> LOCKABLE_FIELDS =
			Arrays.asList("title", "description", "category", "module");
	public static final int EXPERIENCE_TEXT_MAX = 2000;
	public static final double HOURS_MAX = 1000;

	private String lockedFields;      // comma-separated names from LOCKABLE_FIELDS, "" = none
	private String whatWasDone;
	private String howItWasDone;
	private double hoursSpent;        // 0 means no experience recorded yet
	private String effortNotes;       // e.g. "Ana 3h testing, Ben 2h review"

	/** A new lesson starts with defaults and timestamps set to now. */
	public LessonsLearned() {
		id = 0;
		title = "";
		description = "";
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
		lockedFields = "";
		whatWasDone = "";
		howItWasDone = "";
		hoursSpent = 0;
		effortNotes = "";
	}

	// Getter
	public int getId() { return id; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public String getDateCreated() { return dateCreated; }
	public String getDateUpdated() { return dateUpdated; }
	public String getAuthor() { return author; }
	public int getModule() { return module; }
	public Category getCategory() { return category; }

	// Setters, validates each parameter before setting into instance fields

	// Only the database layer should call this, after an insert.
	public void setId(int id) { this.id = id; }

	public void setTitle(String title) {
		check(validateTitle(title));
		this.title = title.trim();
	}

	public void setDescription(String description) {
		check(validateDescription(description));
		this.description = description.trim();
	}

	public void setAuthor(String author) {
		check(validateAuthor(author));
		this.author = author.trim();
	}

	public void setModule(int module) {
		check(validateModule(module));
		this.module = module;
	}

	public void setCategory(Category category) {
		check(validateCategory(category));
		this.category = category;
	}

	public void setDateCreated(String dateCreated) {
		check(validateDate(dateCreated));
		this.dateCreated = dateCreated;
	}

	public void setDateUpdated(String dateUpdated) {
		check(validateDate(dateUpdated));
		this.dateUpdated = dateUpdated;
	}

	// Call after any edit so dateUpdated reflects the change.
	public void touch() {
		this.dateUpdated = LocalDateTime.now().withNano(0).toString();
	}

	private static void check(String error) {
		if (!error.isEmpty()) throw new IllegalArgumentException(error);
	}

	// Static validations

	public static String validateTitle(String title) {
		if (title == null || title.trim().isEmpty())
			return "Title cannot be empty.";
		if (title.trim().length() > TITLE_MAX)
			return "Title cannot be longer than " + TITLE_MAX + " characters.";
		return "";
	}

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

	// Whether the author exists as a user is checked in Database, which can see the user table.
	public static String validateAuthor(String author) {
		if (author == null || author.trim().isEmpty())
			return "Author cannot be empty.";
		return "";
	}

	public static String validateModule(int module) {
		if (module < MODULE_MIN || module > MODULE_MAX)
			return "Module must be between " + MODULE_MIN + " and " + MODULE_MAX + ".";
		return "";
	}

	public static String validateCategory(Category category) {
		if (category == null)
			return "Category is required. Choose one of: " + Arrays.toString(Category.values());
		return "";
	}

	/** Converts user text to a Category, or returns null if it doesn't match. */
	public static Category parseCategory(String text) {
		if (text == null) return null;
		try {
			return Category.valueOf(text.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

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

	// Returns the first error found in a whole lesson, or "" if it is valid.
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
	
	// ----- Locking -----
	public String getLockedFields() { return lockedFields; }

	public boolean isFieldLocked(String field) {
		return Arrays.asList(lockedFields.split(",")).contains(field);
	}

	/** A lesson with any locked field counts as locked (contributors cannot delete it). */
	public boolean isLocked() { return !lockedFields.isEmpty(); }

	public void setLockedFields(String csv) {
		check(validateLockedFields(csv));
		if (csv == null || csv.trim().isEmpty()) { lockedFields = ""; return; }
		StringBuilder sb = new StringBuilder();
		for (String f : csv.split(",")) {
			if (sb.length() > 0) sb.append(",");
			sb.append(f.trim());
		}
		lockedFields = sb.toString();
	}

	public static String validateLockedFields(String csv) {
		if (csv == null || csv.trim().isEmpty()) return "";
		for (String f : csv.split(","))
			if (!LOCKABLE_FIELDS.contains(f.trim()))
				return "'" + f.trim() + "' cannot be locked. Lockable fields: " + LOCKABLE_FIELDS;
		return "";
	}

	// ----- Experience information -----
	public String getWhatWasDone() { return whatWasDone; }
	public String getHowItWasDone() { return howItWasDone; }
	public double getHoursSpent() { return hoursSpent; }
	public String getEffortNotes() { return effortNotes; }
	public boolean hasExperience() { return hoursSpent > 0; }

	public void setExperience(String what, String how, double hours, String effort) {
		check(validateExperience(what, how, hours, effort));
		this.whatWasDone = what.trim();
		this.howItWasDone = how.trim();
		this.hoursSpent = hours;
		this.effortNotes = effort.trim();
	}

	/** Returns the first error found in a set of experience values, or "" if all are valid. */
	public static String validateExperience(String what, String how, double hours, String effort) {
		String e = validateExperienceText("What was done", what);
		if (e.isEmpty()) e = validateExperienceText("How it was done", how);
		if (e.isEmpty()) e = validateHours(hours);
		if (e.isEmpty()) e = validateExperienceText("Effort notes", effort);
		return e;
	}

	public static String validateExperienceText(String label, String text) {
		if (text == null || text.trim().isEmpty()) return label + " cannot be empty.";
		if (text.trim().length() > EXPERIENCE_TEXT_MAX)
			return label + " cannot be longer than " + EXPERIENCE_TEXT_MAX + " characters.";
		return "";
	}

	public static String validateHours(double hours) {
		if (Double.isNaN(hours) || hours <= 0) return "Hours spent must be greater than 0.";
		if (hours > HOURS_MAX) return "Hours spent cannot be more than " + HOURS_MAX + ".";
		return "";
	}
}
