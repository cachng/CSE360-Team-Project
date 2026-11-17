package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import entityClasses.CoachingPrompt.LessonField;

/*******
 * <p> Title: CoachingPromptList Class. </p>
 *
 * <p> Description: A collection of coaching prompts, and the place that decides which prompts a
 * student sees for the lesson they are writing.  It also counts the stored prompts that were
 * skipped because they were damaged, so staff can be told about them.</p>
 *
 * <p> Copyright: Sean Anderson © 2026 </p>
 *
 * @author Sean Anderson (Team 37)
 *
 * @version 1.00		2026-11-07 Initial version (HW3 Task 5 prototype)
 */
public class CoachingPromptList {

	private final List<CoachingPrompt> prompts = new ArrayList<>();
	private int skippedCount = 0;

	/** Default constructor: an empty list. */
	public CoachingPromptList() {}

	/**
	 * Add a prompt.  Null is refused so the list never holds a null entry.
	 *
	 * @param prompt is the prompt to add
	 */
	public void add(CoachingPrompt prompt) {
		if (prompt == null) throw new IllegalArgumentException("Cannot add a null prompt.");
		prompts.add(prompt);
	}

	/** Count one stored prompt that could not be read because it was damaged. */
	public void addSkipped() { skippedCount++; }

	/** @return how many damaged stored prompts were skipped while building this list */
	public int getSkippedCount() { return skippedCount; }

	/**
	 * Find one prompt by id.
	 *
	 * @param id is the prompt's database id
	 * @return the prompt, or null if it is not in this list
	 */
	public CoachingPrompt getById(int id) {
		for (CoachingPrompt p : prompts) if (p.getId() == id) return p;
		return null;
	}

	/**
	 * @param prompt is the prompt to look for
	 * @return true if this exact prompt object is in the list
	 */
	public boolean contains(CoachingPrompt prompt) { return prompts.contains(prompt); }

	/** @return a read-only view of the prompts */
	public List<CoachingPrompt> getAll() { return Collections.unmodifiableList(prompts); }

	/** @return the number of prompts */
	public int size() { return prompts.size(); }

	/** @return true if there are no prompts */
	public boolean isEmpty() { return prompts.isEmpty(); }

	/*******
	 * <p> Method: CoachingPromptList promptsFor(Map&lt;LessonField, String&gt; draft) </p>
	 *
	 * <p> Description: The prompts a student sees for a lesson being written: every prompt that
	 * is turned on and whose trigger matches the text of its field.  Staff preview uses the same
	 * method with a sample draft, so the preview always shows exactly what a student would see.</p>
	 *
	 * @param draft maps each lesson field to the text currently in its box; null, or a missing
	 * 		field, counts as empty
	 *
	 * @return a new list of the prompts to show, possibly empty, never null
	 */
	public CoachingPromptList promptsFor(Map<LessonField, String> draft) {
		CoachingPromptList shown = new CoachingPromptList();
		for (CoachingPrompt p : prompts) {
			if (!p.isEnabled()) continue;
			String text = draft == null ? null : draft.get(p.getField());
			// Prompts are advice only and must never stop a student from saving a lesson, so a
			// prompt that cannot be checked is left out rather than allowed to throw.
			try {
				if (p.matches(text)) shown.add(p);
			} catch (RuntimeException e) {
				continue;
			}
		}
		return shown;
	}
}
