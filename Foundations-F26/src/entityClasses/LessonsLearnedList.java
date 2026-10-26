package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entityClasses.LessonsLearned.Category;

/*******
 * <p> Title: LessonsLearnedList Class. </p>
 * 
 * <p> Description: An in-memory container for LessonsLearned objects.  A single instance can
 * represent every lesson stored in the system, or any subset of them, such as the lessons
 * written by one Contributor, the lessons in one category, or the results of a keyword search.
 * An empty list is a valid result (for example, a search that matches nothing).</p>
 * 
 * <p> This class supports the Contributor User Stories that require viewing, finding, and
 * managing groups of lessons.  Persistence is deliberately not handled here: the Database class
 * reads lessons from storage and fills a LessonsLearnedList, and writes changes back.  Keeping
 * storage out of this class means the same list can be used for the full set of lessons and for
 * temporary subsets without any risk of a subset being written to the database by accident.</p>
 * 
 * <p> CRUD support for lists of lessons:</p>
 * <ul>
 *   <li> Create: {@link #add(LessonsLearned)} </li>
 *   <li> Read: {@link #getById(int)}, {@link #getAll()}, {@link #size()}, {@link #isEmpty()},
 *        {@link #filterByCategory(Category)}, {@link #filterByAuthor(String)} </li>
 *   <li> Delete: {@link #remove(int)}, {@link #clear()} </li>
 * </ul>
 * 
 * @author Cardin
 * @author TP2 Team
 * 
 * @version 1.00	2026-10-26 Initial version for TP2
 */

public class LessonsLearnedList {

	/**
	 * <p> Attribute: lessons </p>
	 * 
	 * <p> Description: The lessons held by this list, in the order they were added. </p>
	 * 
	 * <p> Rationale: An ArrayList is used so there is no fixed upper limit on the number of
	 * lessons the system can hold, and so lessons keep a predictable display order in the GUI.
	 * The field is final so the reference can never be replaced, and private so all changes
	 * must go through the methods below, which enforce the rule that no null entry is ever
	 * stored. </p>
	 */
	private final List<LessonsLearned> lessons = new ArrayList<>();
	
	/**
	 * <p> Constructor: LessonsLearnedList() </p>
	 * 
	 * <p> Description: Creates an empty list of lessons.  Lessons are then added with
	 * {@link #add(LessonsLearned)}, either by the Database class when loading stored lessons
	 * or by the filter methods when building a subset. </p>
	 */
	public LessonsLearnedList() {
	}

	/**
	 * <p> Method: add(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Adds a lesson to the end of this list (the Create operation). </p>
	 * 
	 * <p> A null lesson is rejected with an exception rather than silently ignored.  This keeps
	 * every other method free of null checks and makes a programming error visible immediately
	 * instead of causing a NullPointerException later in the GUI. </p>
	 * 
	 * @param lesson	the lesson to add; must not be null
	 * 
	 * @throws IllegalArgumentException	if lesson is null
	 */
	public void add(LessonsLearned lesson) {
		// Input validation: the list must never hold a null entry
		if (lesson == null) throw new IllegalArgumentException("Cannot add a null lesson.");
		lessons.add(lesson);
	}

	/**
	 * <p> Method: remove(int id) </p>
	 * 
	 * <p> Description: Removes the lesson with the given id from this list (the Delete
	 * operation).  This supports the Contributor User Story for deleting a lesson the
	 * Contributor no longer wants shared; the Database class removes the stored copy. </p>
	 * 
	 * @param id	the unique id of the lesson to remove
	 * 
	 * @return true if a lesson with that id was found and removed, false if no lesson in this
	 * list has that id
	 */
	public boolean remove(int id) {
		// removeIf scans the whole list, so any entry with this id is removed
		return lessons.removeIf(l -> l.getId() == id);
	}

	/**
	 * <p> Method: clear() </p>
	 * 
	 * <p> Description: Removes every lesson from this list, leaving it empty.  Used when a
	 * list is reloaded from the database or when a search is reset, so stale results are not
	 * mixed with new ones. </p>
	 */
	public void clear() { lessons.clear(); }

	/**
	 * <p> Method: getById(int id) </p>
	 * 
	 * <p> Description: Finds one lesson in this list by its unique id (a Read operation).  The
	 * GUI uses this to open the lesson a Contributor selected so it can be viewed or edited. </p>
	 * 
	 * @param id	the unique id of the lesson to find
	 * 
	 * @return the matching lesson, or null if no lesson in this list has that id
	 */
	public LessonsLearned getById(int id) {
		for (LessonsLearned l : lessons) if (l.getId() == id) return l;
		return null;
	}

	/**
	 * <p> Method: getAll() </p>
	 * 
	 * <p> Description: Returns every lesson in this list (a Read operation), for example to
	 * fill a table or list view in the GUI. </p>
	 * 
	 * <p> The returned list is read-only.  Callers cannot add or remove entries through it,
	 * so the null check in {@link #add(LessonsLearned)} cannot be bypassed and the list cannot
	 * be changed behind its back. </p>
	 * 
	 * @return an unmodifiable view of the lessons in this list, in the order they were added
	 */
	public List<LessonsLearned> getAll() { return Collections.unmodifiableList(lessons); }

	/**
	 * <p> Method: size() </p>
	 * 
	 * <p> Description: Returns the number of lessons in this list, for example to show how
	 * many results a search returned. </p>
	 * 
	 * @return the number of lessons in this list
	 */
	public int size() { return lessons.size(); }

	/**
	 * <p> Method: isEmpty() </p>
	 * 
	 * <p> Description: Reports whether this list holds no lessons.  The GUI uses this to show
	 * a "no lessons found" message instead of an empty table. </p>
	 * 
	 * @return true if this list holds no lessons, otherwise false
	 */
	public boolean isEmpty() { return lessons.isEmpty(); }

	/**
	 * <p> Method: filterByCategory(Category category) </p>
	 * 
	 * <p> Description: Returns a new list holding only the lessons in the given category (a
	 * Read operation that produces a subset).  This supports finding lessons by category. </p>
	 * 
	 * <p> This list is not changed.  A new LessonsLearnedList is returned so a filter can
	 * never remove lessons from the full list of all lessons by mistake. </p>
	 * 
	 * @param category	the category to keep
	 * 
	 * @return a new list with only the matching lessons; empty if none match
	 */
	public LessonsLearnedList filterByCategory(Category category) {
		LessonsLearnedList subset = new LessonsLearnedList();
		for (LessonsLearned l : lessons)
			// Enum values are unique objects, so == is the correct comparison here
			if (l.getCategory() == category) subset.add(l);
		return subset;
	}

	/**
	 * <p> Method: filterByAuthor(String author) </p>
	 * 
	 * <p> Description: Returns a new list holding only the lessons written by the given
	 * author (a Read operation that produces a subset).  This supports the Contributor User
	 * Story for viewing the lessons that Contributor has written. </p>
	 * 
	 * <p> This list is not changed.  A new LessonsLearnedList is returned so the full list of
	 * all lessons is never altered by a filter. </p>
	 * 
	 * @param author	the username of the author whose lessons are wanted
	 * 
	 * @return a new list with only that author's lessons; empty if there are none
	 */
	public LessonsLearnedList filterByAuthor(String author) {
		LessonsLearnedList subset = new LessonsLearnedList();
		for (LessonsLearned l : lessons)
			// Exact, case-sensitive match on the username
			if (l.getAuthor().equals(author)) subset.add(l);
		return subset;
	}
}