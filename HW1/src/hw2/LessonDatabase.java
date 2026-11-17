package hw2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: LessonDatabase Class.
 * 
 * Description: Manages the H2 database connection and CRUD SQL queries
 * so student lessons persist between separate application runs.
 * 
 * @author Samuel Joseph
 * @version 1.00 2026-10-05
 */
public class LessonDatabase {

    // Default connection to an embedded H2 database file on disk
    private static final String DEFAULT_URL = "jdbc:h2:./foundations_hw2;AUTO_SERVER=TRUE";
    private static Connection conn = null;
    private static String activeUrl = DEFAULT_URL;

    // Connects to the database and sets up the table
    public static synchronized void initialize() {
        initialize(DEFAULT_URL);
    }

    public static synchronized void initialize(String dbUrl) {
        try {
            activeUrl = dbUrl;
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(activeUrl, "sa", "");
            }
            createTableIfNotExists();
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Creates the lessons_learned table if it doesn't already exist
    private static void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS lessons_learned ("
                   + "  id INT AUTO_INCREMENT PRIMARY KEY,"
                   + "  title VARCHAR(100) NOT NULL,"
                   + "  content VARCHAR(1000) NOT NULL,"
                   + "  author VARCHAR(50) NOT NULL,"
                   + "  created_at BIGINT NOT NULL"
                   + ");";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    private static void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                initialize(activeUrl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reconnection error", e);
        }
    }

    // === CREATE (C): Insert a new lesson ====================================
    public static synchronized int createLesson(String title, String content, String author) {
        checkConnection();
        String sql = "INSERT INTO lessons_learned (title, content, author, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title.trim());
            ps.setString(2, content.trim());
            ps.setString(3, author.trim());
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();

            // Return the auto-generated unique ID from H2
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
        return -1;
    }

    // === READ (R): Get all lessons (newest first) ===========================
    public static synchronized List<LessonLearned> getAllLessons() {
        checkConnection();
        List<LessonLearned> list = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at FROM lessons_learned ORDER BY created_at DESC, id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new LessonLearned(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("author"),
                    rs.getLong("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Get all failed: " + e.getMessage());
        }
        return list;
    }

    // === READ (R): Search lessons by keyword ================================
    public static synchronized List<LessonLearned> searchLessons(String keyword) {
        checkConnection();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllLessons();
        }
        List<LessonLearned> results = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at FROM lessons_learned "
                   + "WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new LessonLearned(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author"),
                        rs.getLong("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
        return results;
    }

    // Helper: Find a single lesson by ID
    public static synchronized LessonLearned getLessonById(int id) {
        checkConnection();
        String sql = "SELECT id, title, content, author, created_at FROM lessons_learned WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LessonLearned(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author"),
                        rs.getLong("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Get by id failed: " + e.getMessage());
        }
        return null;
    }

    // Helper: Check if a post exists
    public static synchronized boolean lessonExists(int id) {
        checkConnection();
        String sql = "SELECT 1 FROM lessons_learned WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // === UPDATE (U): Edit an existing lesson ================================
    public static synchronized boolean updateLesson(int id, String newTitle, String newContent) {
        checkConnection();
        String sql = "UPDATE lessons_learned SET title = ?, content = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newTitle.trim());
            ps.setString(2, newContent.trim());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // === DELETE (D): Remove a lesson ========================================
    public static synchronized boolean deleteLesson(int id) {
        checkConnection();
        String sql = "DELETE FROM lessons_learned WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Clears the table (used to reset between automated test runs)
    public static synchronized void clearAll() {
        checkConnection();
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM lessons_learned");
        } catch (SQLException e) {
            /* ignore */
        }
    }

    // Closes the database connection cleanly
    public static synchronized void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) { /* ignore */ }
    }
}