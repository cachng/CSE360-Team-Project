package lessonsLearned;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonCollection Class. </p>
 * 
 * <p> Description: An in-memory container for lessons.  It can represent all stored lessons or
 * any subset (such as search results), including an empty one.  It uses an ArrayList so there
 * is no fixed upper limit.  It knows nothing about the database; the Database class fills it.</p>
 */
public class LessonCollection {

	private final List<LessonsLearned> lessons = new ArrayList<>();

	// Add a lesson. Null is rejected so the list never holds a null entry.
	public void add(LessonsLearned lesson) {
		if (lesson == null) throw new IllegalArgumentException("Cannot add a null lesson.");
		lessons.add(lesson);
	}

	// Remove the lesson with this id. Returns true if one was removed.
	public boolean remove(int id) {
		return lessons.removeIf(l -> l.getId() == id);
	}

	// Remove every lesson from this collection (not from the database).
	public void clear() { lessons.clear(); }

	// Find one lesson by id, or null if it is not in this collection.
	public LessonsLearned getById(int id) {
		for (LessonsLearned l : lessons) if (l.getId() == id) return l;
		return null;
	}

	// Read-only view, so callers cannot modify the collection behind its back.
	public List<LessonsLearned> getAll() { return Collections.unmodifiableList(lessons); }

	public int size() { return lessons.size(); }

	public boolean isEmpty() { return lessons.isEmpty(); }

	// Returns a new collection with only the lessons of the given category.
	public LessonCollection filterByCategory(LessonsLearned.Category category) {
		LessonCollection subset = new LessonCollection();
		for (LessonsLearned l : lessons)
			if (l.getCategory() == category) subset.add(l);
		return subset;
	}

	// Returns a new collection with only the lessons by the given author.
	public LessonCollection filterByAuthor(String author) {
		LessonCollection subset = new LessonCollection();
		for (LessonsLearned l : lessons)
			if (l.getAuthor().equals(author)) subset.add(l);
		return subset;
	}
}