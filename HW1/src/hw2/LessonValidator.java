package hw2;

/**
 * Title: LessonValidator Class.
 * 
 * Description: Input validation class that checks user inputs against our
 * project rules (Title: 5-100 chars, Content: 10-1000 chars, Author: 3-50 chars).
 * 
 * @author Samuel Joseph
 * @version 1.00 2026-10-05
 */
public class LessonValidator {

    // Character length limits matching our User Stories
    public static final int MIN_TITLE = 5;
    public static final int MAX_TITLE = 100;
    public static final int MIN_CONTENT = 10;
    public static final int MAX_CONTENT = 1000;
    public static final int MIN_AUTHOR = 3;
    public static final int MAX_AUTHOR = 50;
    public static final int MAX_SEARCH = 50;

    /**
     * Checks if the lesson title is valid.
     * Returns an empty string "" if valid, or an error message if invalid.
     */
    public static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Error: Title cannot be empty. Must be 5 to 100 characters.";
        }
        String cleanTitle = title.trim();
        if (cleanTitle.length() < MIN_TITLE) {
            return "Error: Title is too short. Must be at least 5 characters.";
        }
        if (cleanTitle.length() > MAX_TITLE) {
            return "Error: Title is too long. Maximum is 100 characters.";
        }
        return ""; // No errors
    }

    /**
     * Checks if the lesson content/body is valid.
     */
    public static String validateContent(String content) {
        if (content == null || content.trim().isEmpty() || content.trim().length() < MIN_CONTENT) {
            return "Error: Content is too short. Must be at least 10 characters.";
        }
        String cleanContent = content.trim();
        if (cleanContent.length() > MAX_CONTENT) {
            return "Error: Content is too long. Maximum is 1,000 characters.";
        }
        return ""; // No errors
    }

    /**
     * Checks if the author username is valid.
     */
    public static String validateAuthor(String author) {
        if (author == null || author.trim().isEmpty() || 
            author.trim().length() < MIN_AUTHOR || 
            author.trim().length() > MAX_AUTHOR) {
            return "Error: Author name must be between 3 and 50 characters.";
        }
        return ""; // No errors
    }

    /**
     * Checks if the search query is within 50 characters.
     */
    public static String validateSearchQuery(String query) {
        if (query != null && query.trim().length() > MAX_SEARCH) {
            return "Error: Search query cannot exceed 50 characters.";
        }
        return ""; // No errors
    }

    /**
     * Checks all 3 fields at once before saving a post.
     */
    public static String validateAll(String title, String content, String author) {
        String tErr = validateTitle(title);
        if (!tErr.isEmpty()) return tErr;

        String cErr = validateContent(content);
        if (!cErr.isEmpty()) return cErr;

        String aErr = validateAuthor(author);
        if (!aErr.isEmpty()) return aErr;

        return ""; // Everything is valid!
    }
}