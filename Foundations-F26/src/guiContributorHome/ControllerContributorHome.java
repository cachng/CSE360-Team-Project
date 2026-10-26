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
 * <p> Description: The Java/FX ContributorHome Controller.  This class provides the controller actions
 * to allow any user with the contributor role to create lessons learned. * 
 * The controller deals with the user pressing the "Create Lesson Learned" button widget being click, with additional CRUD functionality.  It also
 * supports the user click on the "Quit" button widget.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author omara
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-13 Updated JavaDoc description
 * @version 2.00		2026-10-22 Added LessonLearned Functionality and changed class to revolve around the Contributor user story
 */
public class ControllerContributorHome {

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	public ControllerContributorHome() {
	}

	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(
				ViewContributorHome.theStage, ViewContributorHome.theUser);
	}

	protected static void doCreateLesson() {
		guiNewLessonsLearned.ViewGuiNewLessonsLearned.displayNewLessonLearned(
				ViewContributorHome.theStage, ViewContributorHome.theUser);
	}

	/**********
	 * <p> Method: doEditLesson() </p>
	 * <p> Description: Edits the title of the selected lesson using database.updateLesson() </p>
	 */
	protected static void doEditLesson() {
		int lessonId = getSelectedLessonId();
		if (lessonId == -1) return;

		LessonsLearned lesson = theDatabase.getLesson(lessonId);
		if (lesson == null) return;

		TextInputDialog dialog = new TextInputDialog(lesson.getTitle());
		dialog.setTitle("Edit Lesson Title");
		dialog.setHeaderText("Update the title for lesson ID " + lessonId);
		dialog.setContentText("New Title:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().isEmpty()) {
			lesson.setTitle(result.get().trim());
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
	 * <p> Description: Adds experience notes and hours spent using database.addExperience() </p>
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
		if (!whatRes.isPresent()) return;

		// Prompt for How it was done
		TextInputDialog howDialog = new TextInputDialog("Whiteboard session");
		howDialog.setTitle("Experience: How It Was Done");
		howDialog.setHeaderText("Step 2 of 4");
		howDialog.setContentText("How it was done:");
		Optional<String> howRes = howDialog.showAndWait();
		if (!howRes.isPresent()) return;

		// Prompt for Hours spent
		TextInputDialog hoursDialog = new TextInputDialog("2.0");
		hoursDialog.setTitle("Experience: Hours Spent");
		hoursDialog.setHeaderText("Step 3 of 4");
		hoursDialog.setContentText("Hours spent (> 0):");
		Optional<String> hoursRes = hoursDialog.showAndWait();
		if (!hoursRes.isPresent()) return;

		// Prompt for Effort notes
		TextInputDialog effortDialog = new TextInputDialog("Team pairing");
		effortDialog.setTitle("Experience: Effort Notes");
		effortDialog.setHeaderText("Step 4 of 4");
		effortDialog.setContentText("Effort notes:");
		Optional<String> effortRes = effortDialog.showAndWait();
		if (!effortRes.isPresent()) return;

		try {
			double hours = Double.parseDouble(hoursRes.get().trim());
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
	 * <p> Description: Deletes the selected lesson using database.deleteLesson() </p>
	 */
	protected static void doDeleteLesson() {
		int lessonId = getSelectedLessonId();
		if (lessonId == -1) return;

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirm Deletion");
		confirm.setHeaderText("Delete Lesson ID " + lessonId + "?");
		confirm.setContentText("Are you sure you want to delete this lesson?");

		Optional<ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
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
	 * <p> Helper: Extracts the numeric ID from the selected ListView string </p>
	 */
	private static int getSelectedLessonId() {
		String selected = ViewContributorHome.listView_Lessons.getSelectionModel().getSelectedItem();
		if (selected == null || !selected.startsWith("ID:")) {
			showAlert("Selection Error", "Please select a valid lesson from the list first.");
			return -1;
		}
		try {
			// Format is "ID: 123 | [CATEGORY ...]"
			String idPart = selected.split("\\|")[0].replace("ID:", "").trim();
			return Integer.parseInt(idPart);
		} catch (Exception e) {
			showAlert("Error", "Could not parse lesson ID.");
			return -1;
		}
	}

	private static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewContributorHome.theStage);
	}

	protected static void performQuit() {
		System.exit(0);
	}
}