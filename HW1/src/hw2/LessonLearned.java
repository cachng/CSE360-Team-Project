package hw2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Title: LessonLearned Class.
 * 
 * Description: A simple model class representing a lesson learned post.
 * It holds the post ID, title, body content, author name, and date.
 * 
 * @author Samuel Joseph
 * @version 1.00 2026-10-05
 */
public class LessonLearned {

    // Post attributes
    private int id;
    private String title;
    private String content;
    private String author;
    private long createdAt;

    // Default constructor
    public LessonLearned() {
        this.id = 0;
        this.title = "";
        this.content = "";
        this.author = "";
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor used when a student creates a new lesson (before DB gives it an ID)
    public LessonLearned(String title, String content, String author) {
        this.id = 0;
        this.title = title != null ? title.trim() : "";
        this.content = content != null ? content.trim() : "";
        this.author = author != null ? author.trim() : "";
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor used when loading a lesson from the database (with its ID and timestamp)
    public LessonLearned(int id, String title, String content, String author, long createdAt) {
        this.id = id;
        this.title = title != null ? title.trim() : "";
        this.content = content != null ? content.trim() : "";
        this.author = author != null ? author.trim() : "";
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content != null ? content.trim() : "";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author.trim() : "";
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to format the timestamp into a nice readable date like "Oct 5, 2026 2:30 PM"
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
        return sdf.format(new Date(createdAt));
    }

    @Override
    public String toString() {
        return "#" + id + ": " + title + " (by @" + author + ")";
    }
}
