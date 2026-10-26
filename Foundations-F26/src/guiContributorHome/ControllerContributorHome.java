package guiContributorHome;

import java.util.Optional;
import database.Database;
import entityClasses.LessonsLearned;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/*******
 * <p> Title: ControllerContributorHome Class. </p>
 * 
 * <p> Description: The Java/FX ContributorHome Controller.  This class provides the controller
 * actions that let a user with the Contributor role create and manage lessons learned.  It is
 * the Controller part of the Model-View-Controller pattern used by the Foundations GUI
 * packages: the View (ViewContributorHome) builds the window and calls these methods when a
 * button is pressed, and these methods do the work by calling the Database class.</p>
 * 
 * <p> The controller handles the following actions, each tied to a Contributor User Story:</p>
 * <ul>
 *   <li> Create a lesson: {@link #doCreateLesson()} opens the New Lesson Learned window </li>
 *   <li> Edit a lesson: {@link #doEditLesson()} changes the title of the selected lesson </li>
 *   <li> Add experience: {@link #doAddExperience()} records what was done, how, the hours
 *        spent, and effort notes for the selected lesson </li>
 *   <li> Delete a lesson: {@link #doDeleteLesson()} removes the selected lesson after the
 *        user confirms </li>
 *   <li> Update account, Logout, and Quit, as in the other Foundations home pages </li>
 * </ul>
 * 
 * <p> Permission checks (for example, a Contributor may only change their own unlocked
 * lessons) are done by the Database methods, which receive the current user's name and role.
 * Keeping those rules in one place means every window enforces them the same way.  Any error
 * message the Database returns is shown to the user in an alert.</p>
 * 
 * <p> The class has been written assuming that the View or the Model are the only class
 * methods that can invoke these methods.  This is why each has been declared as "protected".
 * Do not change any of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author omara
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-13 Updated JavaDoc description
 * @version 2.00		2026-10-22 Added LessonLearned Functionality and changed class to revolve
 * around the Contributor user story
 * @version 2.01		2026-10-26 Completed the Javadoc for all attributes and methods
 */
public class ControllerContributorHome {

	/**
	 * <p> Attribute: theDatabase </p>
	 * 
	 * <p> Description: Reference to the single shared Database object created in
	 * FoundationsMain.  Every action in this controller reads or changes lessons through it.
	 * A shared reference is used, as in the other Foundations controllers, so all windows see
	 * the same data and there is only one database connection. </p>
	 */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * <p> Constructor: ControllerContributorHome() </p>
	 * 
	 * <p> Description: Default constructor.  It is never used, because every method in this
	 * class is static; it exists only so the Javadoc tool has something to document here,
	 * as in the other Foundations controllers. </p>
	 */
	public ControllerContributorHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Opens the User Update window so the Contributor can change their
	 * account information.  This reuses the existing Foundations guiUserUpdate package. </p>
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(
				ViewContributorHome.theStage, ViewContributorHome.theUser);
	}

	/**********
	 * <p> Method: doCreateLesson() </p>
	 * 
	 * <p> Description: Opens the New Lesson Learned window (guiNewLessonsLearned package),
	 * where the Contributor enters and saves a new lesson.  This supports the Contributor
	 * User Story for creating a lesson learned.  The same stage and user are passed along so
	 * the new window replaces this one and knows who the author is. </p>
	 */
	protected static void doCreateLesson() {
		guiNewLessonsLearned.ViewGuiNewLessonsLearned.displayNewLessonLearned(
				ViewContributorHome.theStage, ViewContributorHome.theUser);
	}

	/**********
	 * <p> Method: doEditLesson() </p>
	 * 
	 * <p> Description: Edits the title of the lesson selected in the list.  This supports
	 * the Contributor User Story for updating a lesson. </p>
	 * 
	 * <p> Steps: get the selected lesson's id, load the lesson from the database, ask for a
	 * new title in a dialog that starts with the current title, then save the change with
	 * Database.updateLesson().  The user's name and the Contributor role are passed so the
	 * Database can refuse the change if the lesson belongs to someone else or its title is
	 * locked.  On success, the list is refreshed so the new title appears. </p>
	 * 
	 * <p> If the dialog is cancelled or the new title is blank, nothing is changed. </p>
	 */
	protected static void doEditLesson() {
		// Stop if nothing (or something that is not a lesson) is selected;
		// getSelectedLessonId() has already told the user why
		int lessonId = getSelectedLessonId();
		if (lessonId == -1) return;

		LessonsLearned lesson = theDatabase.getLesson(lessonId);
		if (lesson == null) return;

		// Start the dialog with the current title so small changes are easy
		TextInputDialog dialog = new TextInputDialog(lesson.getTitle());
		dialog.setTitle("Edit Lesson Title");
		dialog.setHeaderText("Update the title for lesson ID " + lessonId);
		dialog.setContentText("New Title:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().isEmpty()) {
			lesson.setTitle(result.get().trim());
			// The Database checks ownership and locking before saving
			String error = theDatabase.updateLesson(lesson, ViewContributorHome.theUser.getUserName(), LessonsLearned.ROLE_CONTRIBUTOR);
			
			if (!error.isEmpty()) {
				showAlert("Edit Error", error);
			} else {
				showAlert("Success", "Lesson title updated successfully!");
				ViewContributorHome.refreshLessonList();
			}
		}
	}

	/**********
	 * <p> Method: doAddExperience() </p>
	 * 
	 * <p> Description: Records experience information for the lesson selected in the list:
	 * what was done, how it was done, the hours spent, and effort notes.  This supports the
	 * experience-related user story planned for TP3 and uses the experience attributes of the
	 * LessonsLearned class. </p>
	 * 
	 * <p> The four values are asked for in four dialogs, one after another ("Step 1 of 4" to
	 * "Step 4 of 4").  Cancelling any dialog stops without saving anything, so experience is
	 * never saved half-entered.  Each dialog starts with an example value to show the user
	 * what kind of answer is expected. </p>
	 * 
	 * <p> All four values are then sent together to Database.addExperience(), which checks
	 * them with LessonsLearned.validateExperience() and checks the user may change this
	 * lesson.  Hours typed as text that is not a number are caught here and reported. </p>
	 */
	protected static void doAddExperience() {
		int lessonId = getSelectedLessonId();
		if (lessonId == -1) return;

		// Prompt for What was done
		TextInputDialog whatDialog = new TextInputDialog("Ran retro");
		whatDialog.setTitle("Experience: What Was Done");
		whatDialog.setHeaderText("Step 1 of 4");
		whatDialog.setContentText("What was done:");
		Optional<String> whatRes = whatDialog.showAndWait();
		if (!whatRes.isPresent()) return;	// Cancelled: save nothing

		// Prompt for How it was done
		TextInputDialog howDialog = new TextInputDialog("Whiteboard session");
		howDialog.setTitle("Experience: How It Was Done");
		howDialog.setHeaderText("Step 2 of 4");
		howDialog.setContentText("How it was done:");
		Optional<String> howRes = howDialog.showAndWait();
		if (!howRes.isPresent()) return;	// Cancelled: save nothing

		// Prompt for Hours spent
		TextInputDialog hoursDialog = new TextInputDialog("2.0");
		hoursDialog.setTitle("Experience: Hours Spent");
		hoursDialog.setHeaderText("Step 3 of 4");
		hoursDialog.setContentText("Hours spent (> 0):");
		Optional<String> hoursRes = hoursDialog.showAndWait();
		if (!hoursRes.isPresent()) return;	// Cancelled: save nothing

		// Prompt for Effort notes
		TextInputDialog effortDialog = new TextInputDialog("Team pairing");
		effortDialog.setTitle("Experience: Effort Notes");
		effortDialog.setHeaderText("Step 4 of 4");
		effortDialog.setContentText("Effort notes:");
		Optional<String> effortRes = effortDialog.showAndWait();
		if (!effortRes.isPresent()) return;	// Cancelled: save nothing

		try {
			// Converting the hours text can fail, which is caught below
			double hours = Double.parseDouble(hoursRes.get().trim());
			// Range and length checks happen in the Database via LessonsLearned validation
			String error = theDatabase.addExperience(
					lessonId,
					ViewContributorHome.theUser.getUserName(),
					LessonsLearned.ROLE_CONTRIBUTOR,
					whatRes.get().trim(),
					howRes.get().trim(),
					hours,
					effortRes.get().trim()
			);

			if (!error.isEmpty()) {
				showAlert("Experience Error", error);
			} else {
				showAlert("Success", "Experience information saved successfully!");
				ViewContributorHome.refreshLessonList();
			}
		} catch (NumberFormatException e) {
			showAlert("Invalid Input", "Hours spent must be a valid number.");
		}
	}

	/**********
	 * <p> Method: doDeleteLesson() </p>
	 * 
	 * <p> Description: Deletes the lesson selected in the list, after the user confirms.
	 * This supports the Contributor User Story for deleting a lesson. </p>
	 * 
	 * <p> A confirmation dialog is shown first because a deletion cannot be undone.  The
	 * user's name and the Contributor role are passed to Database.deleteLesson() so it can
	 * refuse to delete a lesson that belongs to someone else or is locked.  On success, the
	 * list is refreshed so the deleted lesson disappears. </p>
	 */
	protected static void doDeleteLesson() {
		int lessonId = getSelectedLessonId();
		if (lessonId == -1) return;

		// Ask before deleting, since the deletion cannot be undone
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirm Deletion");
		confirm.setHeaderText("Delete Lesson ID " + lessonId + "?");
		confirm.setContentText("Are you sure you want to delete this lesson?");

		Optional<ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// The Database checks ownership and locking before deleting
			String error = theDatabase.deleteLesson(
					lessonId,
					ViewContributorHome.theUser.getUserName(),
					LessonsLearned.ROLE_CONTRIBUTOR
			);

			if (!error.isEmpty()) {
				showAlert("Deletion Error", error);
			} else {
				showAlert("Success", "Lesson deleted successfully!");
				ViewContributorHome.refreshLessonList();
			}
		}
	}

	/**********
	 * <p> Method: getSelectedLessonId() </p>
	 * 
	 * <p> Description: Helper that returns the id of the lesson selected in the View's list.
	 * Each list entry is a string that starts with the id, in the form
	 * "ID: 123 | [CATEGORY ...]", so the id is read from the text before the first "|". </p>
	 * 
	 * <p> If nothing is selected, or the text cannot be read as an id, an alert explains the
	 * problem and -1 is returned, so every caller only needs to check for -1. </p>
	 * 
	 * @return the selected lesson's id, or -1 if no valid lesson is selected
	 */
	private static int getSelectedLessonId() {
		String selected = ViewContributorHome.listView_Lessons.getSelectionModel().getSelectedItem();
		if (selected == null || !selected.startsWith("ID:")) {
			showAlert("Selection Error", "Please select a valid lesson from the list first.");
			return -1;
		}
		try {
			// Format is "ID: 123 | [CATEGORY ...]"; take the part before "|" and drop "ID:"
			String idPart = selected.split("\\|")[0].replace("ID:", "").trim();
			return Integer.parseInt(idPart);
		} catch (Exception e) {
			showAlert("Error", "Could not parse lesson ID.");
			return -1;
		}
	}

	/**********
	 * <p> Method: showAlert(String title, String message) </p>
	 * 
	 * <p> Description: Helper that shows a message in a pop-up and waits for the user to
	 * close it.  Used for both success and error messages so they all look the same. </p>
	 * 
	 * @param title		the text for the pop-up's title bar
	 * @param message	the message to show
	 */
	private static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs the user out by returning to the Foundations login window. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewContributorHome.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: Ends the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}