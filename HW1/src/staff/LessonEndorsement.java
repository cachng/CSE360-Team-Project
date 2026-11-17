package staff;

/**
 * Title: LessonEndorsement
 * 
 * Description: A simple class that stores information when a staff member
 * (TA or Instructor) reviews and endorses a student's lesson.
 * It tracks who endorsed it, their feedback notes, and the date it was endorsed.
 * 
 * Author: Samuel Joseph (Team 37)
 */

public class LessonEndorsement {
	
	// Simple fields to hold the endorsement details
	private int lessonId;
	private boolean isEndorsed;
	private String endorsedBy;
	private String endorsementNotes;
	private String dateEndorsed;
	
	// Default constructor (starts as not endorsed)
	public LessonEndorsement() {
		this.isEndorsed = false;
		this.endorsedBy = "";
		this.endorsementNotes = "";
		this.dateEndorsed = "";
	}
	
	// Constructor to set all fields when staff endorses a lesson
	public LessonEndorsement(int lessonId, boolean isEndorsed, String endorsedBy, String notes, String dateEndorsed) {
		this.lessonId = lessonId;
		this.isEndorsed = isEndorsed;
		this.endorsedBy = endorsedBy;
		this.endorsementNotes = notes;
		this.dateEndorsed = dateEndorsed;
	}
	
	// Standard getters and setters
	public int getLessonId() { return lessonId; }
	public void setLessonId(int id) {this.lessonId = id; }
	
	public boolean isEndorsed() {return isEndorsed; }
	public void setEndorsed(boolean endorsed) {this.isEndorsed = endorsed; }
	
	public String getEndorsedBy() {return endorsedBy; }
	public void setEndorsedBy(String user) {this.endorsedBy = user; }
	
	public String getEndorsementNotes() {return endorsementNotes; }
	public void setEndorsementNotes(String notes) {this.endorsementNotes = notes; }
	
	public String getDateEndorsed() {return dateEndorsed; }
	public void setDateEndorsed(String date) {this.dateEndorsed = date; }

}
