package staff;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: StaffEndorsementService
 * 
 * Description: Handles the logic for staff members (TAs and Instructors) to review,
 * endorse, or revoke endorsements on student lessons.
 * It checks user permissions, makes sure review notes are valid, and keeps track
 * of which lessons are currently endorsed.
 * 
 * Author: Samuel Joseph (Team 37)
 */
public class StaffEndorsementService {

    // Simple role constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_CONTRIBUTOR = "Contributor";

    // Notes length limits
    public static final int MAX_NOTES_LENGTH = 2000;
    public static final int MIN_NOTES_LENGTH = 1;

    // A simple map to store endorsements by lesson ID for this prototype
    private final Map<Integer, LessonEndorsement> endorsementTable = new HashMap<>();

    /**
     * Checks if the user is allowed to endorse lessons.
     * 
     * @param actingRole the role of the user (e.g., Staff, Admin, Contributor)
     * @return true if the user is Staff or Admin, false otherwise
     */
    public boolean isAuthorizedStaff(String actingRole) {
        // Rationale: We only want teaching staff and admins grading and endorsing posts.
        // Regular student contributors should not be able to endorse their own posts.
        if (actingRole == null) return false;
        return ROLE_STAFF.equalsIgnoreCase(actingRole.trim()) || ROLE_ADMIN.equalsIgnoreCase(actingRole.trim());
    }

    /**
     * Checks if the review notes are between 1 and 2000 characters.
     * 
     * @param notes the feedback notes from the reviewer
     * @return empty string if valid, or an error message if invalid
     */
    public String validateNotes(String notes) {
        // Rationale: The notes can't be empty because students need real feedback.
        // We cap it at 2000 characters so it fits neatly in our H2 database column without errors.
        if (notes == null || notes.trim().isEmpty()) {
            return "Endorsement notes cannot be blank.";
        }
        if (notes.length() > MAX_NOTES_LENGTH) {
            return "Endorsement notes exceed " + MAX_NOTES_LENGTH + " character limit.";
        }
        return "";
    }

    /**
     * Endorses a lesson if the user has permission and the inputs are valid.
     * 
     * @param lessonId the ID of the lesson being endorsed
     * @param actingUser the username of the staff reviewer
     * @param actingRole the role of the reviewer (must be Staff or Admin)
     * @param notes feedback notes from the staff member
     * @return empty string on success, or an error message on failure
     */
    public String endorseLesson(int lessonId, String actingUser, String actingRole, String notes) {
        // Rationale: Check permissions first so unauthorized users are blocked immediately.
        if (!isAuthorizedStaff(actingRole)) {
            return "Role '" + actingRole + "' cannot endorse lessons. Staff or Admin required.";
        }

        // Rationale: A lesson ID must be a positive number (1, 2, 3...).
        if (lessonId <= 0) {
            return "No lesson exists with id " + lessonId + ".";
        }

        // Rationale: Validate note length to make sure it's between 1 and 2000 chars.
        String validationError = validateNotes(notes);
        if (!validationError.isEmpty()) {
            return validationError;
        }

        // Rationale: Record the current date and time so we know when it was endorsed.
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Save the new endorsement
        LessonEndorsement record = new LessonEndorsement(lessonId, true, actingUser, notes.trim(), timestamp);
        endorsementTable.put(lessonId, record);

        return ""; // Empty string means success
    }

    /**
     * Removes an endorsement if a lesson becomes outdated.
     * 
     * @param lessonId the ID of the lesson whose endorsement is being revoked
     * @param actingUser username of the staff member revoking it
     * @param actingRole role of the user revoking it (must be Staff or Admin)
     * @param revocationReason why the endorsement is being removed
     * @return empty string on success, or an error message on failure
     */
    public String revokeEndorsement(int lessonId, String actingUser, String actingRole, String revocationReason) {
        // Rationale: Only staff can revoke an endorsement.
        if (!isAuthorizedStaff(actingRole)) {
            return "Role '" + actingRole + "' cannot revoke endorsements. Staff or Admin required.";
        }

        // Rationale: Make sure the lesson exists in our table before trying to revoke it.
        if (lessonId <= 0 || !endorsementTable.containsKey(lessonId)) {
            return "No lesson exists with id " + lessonId + ".";
        }

        // Rationale: Staff must give a short reason for why the endorsement was removed.
        String validationError = validateNotes(revocationReason);
        if (!validationError.isEmpty()) {
            return validationError;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LessonEndorsement record = endorsementTable.get(lessonId);
        record.setEndorsed(false);
        record.setEndorsedBy(actingUser);
        record.setEndorsementNotes("REVOKED: " + revocationReason.trim());
        record.setDateEndorsed(timestamp);

        return ""; // Success
    }

    /**
     * Gets the endorsement info for a lesson.
     * 
     * @param lessonId the lesson ID to look up
     * @return the LessonEndorsement record, or null if not endorsed
     */
    public LessonEndorsement getEndorsement(int lessonId) {
        return endorsementTable.get(lessonId);
    }

    /**
     * Returns a list of all lesson IDs that currently have a staff endorsement.
     * 
     * @return list of integer lesson IDs that are currently endorsed
     */
    public List<Integer> getEndorsedLessonIds() {
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, LessonEndorsement> entry : endorsementTable.entrySet()) {
            if (entry.getValue().isEndorsed()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}