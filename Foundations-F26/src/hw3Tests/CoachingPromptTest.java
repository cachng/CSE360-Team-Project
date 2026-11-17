package hw3Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import database.Database;
import entityClasses.CoachingPrompt;
import entityClasses.CoachingPrompt.LessonField;
import entityClasses.CoachingPrompt.Trigger;
import entityClasses.CoachingPromptList;
import entityClasses.LessonsLearned;

/*******
 * <p> Title: CoachingPromptTest Class. </p>
 *
 * <p> Description: HW3 Task 5 tests for the TP3 aspect "Staff Automated Feedback &amp; AI Coaching
 * Prompts": staff can review and configure the automated coaching prompts students receive when
 * writing lessons.  The tests were written before the code (test-driven development).  Each test
 * names the requirement it checks (CP1-CP12, listed in TP3 Testing Requirements.pdf) and the
 * input and expected output are the ones in TP3 Testing Details and Rationale.pdf.</p>
 *
 * <p> The tests are in three sections: the prompt rules and what a student sees use no database;
 * the stored-prompt tests use a separate in-memory H2 database, so the real ~/FoundationDatabase
 * is never changed.</p>
 *
 * @author Sean Anderson (Team 37)
 *
 * @version 1.00		2026-11-07 Initial version
 */
class CoachingPromptTest {

	/** A description long enough that a "shorter than 20" prompt does not fire. */
	private static final String LONG_TEXT = "I learned to start the homework two days earlier.";

	/**
	 * Build a prompt without going through a database.
	 *
	 * @param text is the advice the student sees
	 * @param field is the lesson field the prompt is about
	 * @param trigger is when the prompt appears
	 * @param value is the trigger's setting ("" for EMPTY)
	 * @return the new prompt
	 */
	private static CoachingPrompt prompt(String text, LessonField field, Trigger trigger, String value) {
		CoachingPrompt p = new CoachingPrompt();
		p.setText(text);
		p.setRule(field, trigger, value);
		return p;
	}

	/**
	 * A lesson draft the way the New Lesson Learned form holds it: the raw text of each box.
	 *
	 * @param title is the title box
	 * @param description is the description box
	 * @param category is the category box
	 * @return the draft as a field-to-text map
	 */
	private static Map<LessonField, String> draft(String title, String description, String category) {
		Map<LessonField, String> d = new EnumMap<>(LessonField.class);
		d.put(LessonField.TITLE, title);
		d.put(LessonField.DESCRIPTION, description);
		d.put(LessonField.CATEGORY, category);
		return d;
	}

	/*-********************************************************************************************

	The prompt rules, without a database

	*/

	@ParameterizedTest(name = "CP2: text of length {0} is accepted")
	@ValueSource(ints = { 1, 100, 200 })
	void validTextLengths(int length) {
		assertEquals("", CoachingPrompt.validateText("x".repeat(length)));
	}

	@ParameterizedTest(name = "CP2: text of length {0} is refused")
	@ValueSource(ints = { 0, 201 })
	void invalidTextLengths(int length) {
		assertNotEquals("", CoachingPrompt.validateText("x".repeat(length)));
	}

	@Test
	@DisplayName("CP2: null and spaces-only text are refused; spaces around text are trimmed")
	void blankText() {
		assertNotEquals("", CoachingPrompt.validateText(null));
		assertNotEquals("", CoachingPrompt.validateText("     "));
		CoachingPrompt p = new CoachingPrompt();
		p.setText("  Say what you would change.  ");
		assertEquals("Say what you would change.", p.getText());
	}

	@Test
	@DisplayName("CP2: an invalid text cannot be put into a prompt object")
	void setterRefusesBadText() {
		assertThrows(IllegalArgumentException.class, () -> new CoachingPrompt().setText(""));
	}

	@Test
	@DisplayName("CP3: a missing field or trigger is refused")
	void missingFieldOrTrigger() {
		assertNotEquals("", CoachingPrompt.validateRule(null, Trigger.EMPTY, ""));
		assertNotEquals("", CoachingPrompt.validateRule(LessonField.TITLE, null, ""));
	}

	@ParameterizedTest(name = "CP4: SHORTER_THAN \"{0}\" is accepted")
	@ValueSource(strings = { "1", "20", "2000", " 50 " })
	void validLengths(String value) {
		assertEquals("", CoachingPrompt.validateRule(LessonField.DESCRIPTION,
				Trigger.SHORTER_THAN, value));
	}

	@ParameterizedTest(name = "CP4: SHORTER_THAN \"{0}\" is refused")
	@ValueSource(strings = { "0", "2001", "-5", "abc", "", "1.5" })
	void invalidLengths(String value) {
		assertNotEquals("", CoachingPrompt.validateRule(LessonField.DESCRIPTION,
				Trigger.SHORTER_THAN, value));
	}

	@ParameterizedTest(name = "CP4: CATEGORY_IS \"{0}\" is accepted")
	@ValueSource(strings = { "GENERAL", "HOMEWORK", "EXAM", "PROJECTS", "exam" })
	void validCategories(String value) {
		assertEquals("", CoachingPrompt.validateRule(LessonField.CATEGORY,
				Trigger.CATEGORY_IS, value));
	}

	@ParameterizedTest(name = "CP4: CATEGORY_IS \"{0}\" is refused")
	@ValueSource(strings = { "QUIZ", "" })
	void invalidCategories(String value) {
		assertNotEquals("", CoachingPrompt.validateRule(LessonField.CATEGORY,
				Trigger.CATEGORY_IS, value));
	}

	@Test
	@DisplayName("CP4: CATEGORY_IS only makes sense on the category field")
	void categoryTriggerOnWrongField() {
		assertNotEquals("", CoachingPrompt.validateRule(LessonField.TITLE,
				Trigger.CATEGORY_IS, "EXAM"));
	}

	@Test
	@DisplayName("CP4: EMPTY takes no setting")
	void emptyTakesNoValue() {
		assertEquals("", CoachingPrompt.validateRule(LessonField.TITLE, Trigger.EMPTY, ""));
		assertNotEquals("", CoachingPrompt.validateRule(LessonField.TITLE, Trigger.EMPTY, "5"));
	}

	@Test
	@DisplayName("CP5: two prompts with the same rule and text (ignoring case) are duplicates")
	void duplicates() {
		CoachingPrompt a = prompt("Add more detail.", LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "20");
		CoachingPrompt b = prompt("ADD MORE DETAIL.", LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "20");
		CoachingPrompt c = prompt("Add more detail.", LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "40");
		assertTrue(a.isDuplicateOf(b));
		assertFalse(a.isDuplicateOf(c));
	}

	/*-********************************************************************************************

	Which prompts a student sees, without a database

	*/

	/** Three sample prompts, rebuilt before every test, used by the CP8, CP10 and CP11 tests. */
	private CoachingPromptList list;
	private CoachingPrompt titleEmpty, shortDescription, examCategory;

	@BeforeEach
	void setUpSampleList() {
		list = new CoachingPromptList();
		titleEmpty = prompt("Give your lesson a title.", LessonField.TITLE, Trigger.EMPTY, "");
		shortDescription = prompt("Say what you would do differently next time.",
				LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "20");
		examCategory = prompt("Which topic on the exam surprised you?",
				LessonField.CATEGORY, Trigger.CATEGORY_IS, "EXAM");
		list.add(titleEmpty);
		list.add(shortDescription);
		list.add(examCategory);
	}

	@Test
	@DisplayName("CP10: an empty draft gets the empty-title and short-description prompts")
	void emptyDraft() {
		CoachingPromptList shown = list.promptsFor(draft("", "", ""));
		assertEquals(2, shown.size());
		assertTrue(shown.contains(titleEmpty));
		assertTrue(shown.contains(shortDescription));
	}

	@Test
	@DisplayName("CP10: a complete draft gets no prompts")
	void completeDraft() {
		assertTrue(list.promptsFor(draft("Start early", LONG_TEXT, "HOMEWORK")).isEmpty());
	}

	@ParameterizedTest(name = "CP10: a description of length {0} shows the prompt: {1}")
	@CsvSource({ "0, true", "19, true", "20, false", "21, false" })
	void shorterThanBoundary(int length, boolean shown) {
		// "Shorter than 20" must fire at 19 and stop at 20; testing 0 and 21 as well catches
		// a <= written where < was meant, which no single value would show.
		CoachingPromptList result = list.promptsFor(draft("T", "d".repeat(length), "GENERAL"));
		assertEquals(shown, result.contains(shortDescription));
	}

	@ParameterizedTest(name = "CP10: category \"{0}\" shows the exam prompt: {1}")
	@CsvSource({ "EXAM, true", "exam, true", "' Exam ', true", "HOMEWORK, false" })
	void categoryMatch(String category, boolean shown) {
		CoachingPromptList result = list.promptsFor(draft("T", LONG_TEXT, category));
		assertEquals(shown, result.contains(examCategory));
	}

	@Test
	@DisplayName("CP8: a prompt that is turned off is never shown")
	void disabledNotShown() {
		titleEmpty.setEnabled(false);
		assertFalse(list.promptsFor(draft("", LONG_TEXT, "GENERAL")).contains(titleEmpty));
	}

	@Test
	@DisplayName("CP11: odd drafts never throw (null draft, missing and null fields)")
	void oddDraftsNeverThrow() {
		// The form passes raw box text, so every shape of "nothing typed" must be safe: a
		// prompt that throws here would stop the student from saving their lesson.
		assertDoesNotThrow(() -> list.promptsFor(null));
		assertDoesNotThrow(() -> list.promptsFor(new EnumMap<>(LessonField.class)));
		assertDoesNotThrow(() -> list.promptsFor(draft(null, null, null)));
		assertEquals(2, list.promptsFor(draft(null, null, null)).size(),
				"missing text counts as empty");
	}

	/*-********************************************************************************************

	Saving, changing and reading prompts in the database

	*/

	private static final String URL = "jdbc:h2:mem:hw3prompts;DB_CLOSE_DELAY=-1";
	private static final String STAFF = "taAlex";
	private Database db;
	private Connection raw;		// a second connection, used only to damage rows

	@BeforeEach
	void setUp() throws SQLException {
		// A separate in-memory database, rebuilt for every test, so tests never touch the
		// real ~/FoundationDatabase and never depend on each other's prompts.
		db = new Database(URL);
		db.connectToDatabase();
		raw = DriverManager.getConnection(URL, "sa", "");
	}

	@AfterEach
	void tearDown() throws SQLException {
		try (Statement s = raw.createStatement()) {
			s.execute("DROP ALL OBJECTS");
		}
		raw.close();
		db.closeConnection();
	}

	private CoachingPrompt shortDescription() {
		return prompt("Say what you would do differently next time.",
				LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "20");
	}

	@Test
	@DisplayName("CP1: staff create a prompt and it is read back unchanged")
	void createAndReadBack() {
		// Read the prompt back from the database rather than checking the object we saved:
		// only a fresh read proves every value was really stored.
		CoachingPrompt p = shortDescription();
		assertEquals("", db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF));
		assertTrue(p.getId() > 0);
		CoachingPrompt read = db.getPrompt(p.getId());
		assertAll(
			() -> assertEquals(p.getText(), read.getText()),
			() -> assertEquals(LessonField.DESCRIPTION, read.getField()),
			() -> assertEquals(Trigger.SHORTER_THAN, read.getTrigger()),
			() -> assertEquals("20", read.getTriggerValue()),
			() -> assertTrue(read.isEnabled()),
			() -> assertEquals(STAFF, read.getUpdatedBy()));
	}

	@Test
	@DisplayName("CP1: Admin counts as staff")
	void adminIsStaff() {
		assertEquals("", db.createPrompt(shortDescription(), "admin", LessonsLearned.ROLE_ADMIN));
	}

	@Test
	@DisplayName("CP2-CP4: an invalid prompt is refused and nothing is saved")
	void invalidPromptNotSaved() {
		CoachingPrompt p = new CoachingPrompt();		// no text, no rule
		assertNotEquals("", db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF));
		assertEquals(0, db.getAllPrompts().size());
	}

	@Test
	@DisplayName("CP5: a duplicate prompt is refused")
	void duplicateRefused() {
		assertEquals("", db.createPrompt(shortDescription(), STAFF, CoachingPrompt.ROLE_STAFF));
		String error = db.createPrompt(shortDescription(), STAFF, CoachingPrompt.ROLE_STAFF);
		assertTrue(error.contains("already exists"), error);
		assertEquals(1, db.getAllPrompts().size());
	}

	@ParameterizedTest(name = "CP6: role \"{0}\" cannot create a prompt")
	@ValueSource(strings = { "Contributor", "Student", "", "staff" })
	void nonStaffCannotCreate(String role) {
		// "staff" in lower case is included because role names are compared exactly; a
		// case-insensitive check would quietly widen who can change prompts.
		String error = db.createPrompt(shortDescription(), "sam", role);
		assertTrue(error.contains("Only staff"), error);
		assertEquals(0, db.getAllPrompts().size());
	}

	@Test
	@DisplayName("CP6: a non-staff user cannot edit, turn off or delete a prompt")
	void nonStaffCannotChange() {
		CoachingPrompt p = shortDescription();
		db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF);
		p.setText("Changed by a student.");
		String role = LessonsLearned.ROLE_CONTRIBUTOR;
		assertAll(
			() -> assertNotEquals("", db.updatePrompt(p, "sam", role)),
			() -> assertNotEquals("", db.setPromptEnabled(p.getId(), false, "sam", role)),
			() -> assertNotEquals("", db.deletePrompt(p.getId(), "sam", role)));
		CoachingPrompt stored = db.getPrompt(p.getId());
		assertEquals("Say what you would do differently next time.", stored.getText());
		assertTrue(stored.isEnabled());
	}

	@Test
	@DisplayName("CP7: staff edit a prompt; the change and the editor are saved")
	void staffEdit() {
		CoachingPrompt p = shortDescription();
		db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF);
		p.setText("What will you do differently next time?");
		p.setRule(LessonField.DESCRIPTION, Trigger.SHORTER_THAN, "40");
		assertEquals("", db.updatePrompt(p, "profKim", CoachingPrompt.ROLE_STAFF));
		CoachingPrompt read = db.getPrompt(p.getId());
		assertEquals("What will you do differently next time?", read.getText());
		assertEquals("40", read.getTriggerValue());
		assertEquals("profKim", read.getUpdatedBy());
	}

	@Test
	@DisplayName("CP7: an edit into a duplicate of another prompt is refused")
	void editIntoDuplicate() {
		CoachingPrompt a = shortDescription();
		CoachingPrompt b = prompt("Give your lesson a title.", LessonField.TITLE, Trigger.EMPTY, "");
		db.createPrompt(a, STAFF, CoachingPrompt.ROLE_STAFF);
		db.createPrompt(b, STAFF, CoachingPrompt.ROLE_STAFF);
		b.setText(a.getText());
		b.setRule(a.getField(), a.getTrigger(), a.getTriggerValue());
		assertNotEquals("", db.updatePrompt(b, STAFF, CoachingPrompt.ROLE_STAFF));
	}

	@Test
	@DisplayName("CP8: staff turn a prompt off and on; students only see it while on")
	void turnOffAndOn() {
		CoachingPrompt p = shortDescription();
		db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF);
		Map<LessonField, String> shortDraft = draft("T", "too short", "GENERAL");

		assertEquals("", db.setPromptEnabled(p.getId(), false, STAFF, CoachingPrompt.ROLE_STAFF));
		assertTrue(db.getPromptsForDraft(shortDraft).isEmpty());
		assertEquals("", db.setPromptEnabled(p.getId(), true, STAFF, CoachingPrompt.ROLE_STAFF));
		assertEquals(1, db.getPromptsForDraft(shortDraft).size());
	}

	@Test
	@DisplayName("CP9: staff delete a prompt; deleting it again gives an error, not a crash")
	void delete() {
		CoachingPrompt p = shortDescription();
		db.createPrompt(p, STAFF, CoachingPrompt.ROLE_STAFF);
		assertEquals("", db.deletePrompt(p.getId(), STAFF, CoachingPrompt.ROLE_STAFF));
		assertNull(db.getPrompt(p.getId()));
		assertTrue(db.deletePrompt(p.getId(), STAFF, CoachingPrompt.ROLE_STAFF).contains("No prompt"));
	}

	@ParameterizedTest(name = "CP12: a stored {0} of \"{1}\" is skipped, the rest still load")
	@CsvSource({ "promptField, BOGUS", "promptTrigger, NEVER", "triggerValue, abc",
			"promptText, ''" })
	void damagedRowSkipped(String column, String value) throws SQLException {
		CoachingPrompt good = prompt("Give your lesson a title.", LessonField.TITLE, Trigger.EMPTY, "");
		CoachingPrompt bad = shortDescription();
		db.createPrompt(good, STAFF, CoachingPrompt.ROLE_STAFF);
		db.createPrompt(bad, STAFF, CoachingPrompt.ROLE_STAFF);
		// Raw SQL goes around every check in createPrompt, the same way a file edited or
		// damaged outside the app would (OWASP A08 / CWE-502).
		try (Statement s = raw.createStatement()) {
			s.executeUpdate("UPDATE coachingPromptsDB SET " + column + " = '" + value
					+ "' WHERE id = " + bad.getId());
		}
		CoachingPromptList all = assertDoesNotThrow(() -> db.getAllPrompts());
		assertNotNull(all.getById(good.getId()), "the good prompt must still load");
		assertNull(all.getById(bad.getId()), "the damaged prompt must be skipped");
		assertEquals(1, all.getSkippedCount(), "the skipped row must be reported");
		assertNull(assertDoesNotThrow(() -> db.getPrompt(bad.getId())));
		assertDoesNotThrow(() -> db.getPromptsForDraft(draft("", "", "")));
	}

	@Test
	@DisplayName("CP12: a NULL in a stored prompt is skipped")
	void nullColumnSkipped() throws SQLException {
		CoachingPrompt bad = shortDescription();
		db.createPrompt(bad, STAFF, CoachingPrompt.ROLE_STAFF);
		try (Statement s = raw.createStatement()) {
			s.executeUpdate("UPDATE coachingPromptsDB SET promptField = NULL WHERE id = " + bad.getId());
		}
		assertEquals(0, assertDoesNotThrow(() -> db.getAllPrompts()).size());
	}
}
