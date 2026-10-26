package guiNewLessonsLearned;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearned.Category;

/*******
 * <p> Title: ControllerGuiNewLessonsLearned Class. </p>
 * 
 * <p> Description: The JavaFX-based New Lesson Learned Controller. This class validates
 * user input from ViewGuiNewLessonsLearned and stores the new lesson in the database. </p>
 */
public class ControllerGuiNewLessonsLearned {

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor is not used.
	 */
	public ControllerGuiNewLessonsLearned() {
	}

	/**********
	 * <p> Method: protected static void doCreateLesson() </p>
	 * 
	 * <p> Description: Fetches input fields from ViewGuiNewLessonsLearned, validates each field
	 * using LessonsLearned validation rules, and saves the new lesson to the database. </p>
	 */
	protected static void doCreateLesson() {
		String lessonTitle = ViewGuiNewLessonsLearned.text_Title.getText();
		String lessonDescription = ViewGuiNewLessonsLearned.text_Description.getText();
		String lessonAuthor = ViewGuiNewLessonsLearned.text_Author;
		String moduleText = ViewGuiNewLessonsLearned.text_Module.getText();
		String categoryText = ViewGuiNewLessonsLearned.text_Category.getText();

		System.out.println("** New Lesson: " + lessonTitle + " Created by: " + lessonAuthor);

		// 1. Validate Title
		String titleErrMessage = LessonsLearned.validateTitle(lessonTitle);
		if (!titleErrMessage.isEmpty()) {
			ViewGuiNewLessonsLearned.text_Title.setText("");
			ViewGuiNewLessonsLearned.alertTitleError.setContentText(
					titleErrMessage + " Please enter a valid title and try again.");
			ViewGuiNewLessonsLearned.alertTitleError.showAndWait();
			return;
		}

		// 2. Validate Description
		String descriptionErrMessage = LessonsLearned.validateDescription(lessonDescription);
		if (!descriptionErrMessage.isEmpty()) {
			ViewGuiNewLessonsLearned.text_Description.setText("");
			ViewGuiNewLessonsLearned.alertDescriptionError.setContentText(
					descriptionErrMessage + " Please enter a valid description and try again.");
			ViewGuiNewLessonsLearned.alertDescriptionError.showAndWait();
			return;
		}

		// 3. Validate Author
		String authorErrMessage = LessonsLearned.validateAuthor(lessonAuthor);
		if (!authorErrMessage.isEmpty()) {
			ViewGuiNewLessonsLearned.alertAuthorError.setContentText(authorErrMessage);
			ViewGuiNewLessonsLearned.alertAuthorError.showAndWait();
			return;
		}

		// 4. Parse and Validate Module Number
		int lessonModule;
		try {
			lessonModule = Integer.parseInt(moduleText.trim());
		} catch (NumberFormatException e) {
			ViewGuiNewLessonsLearned.text_Module.setText("");
			ViewGuiNewLessonsLearned.alertModuleError.setContentText(
					"Module must be a valid integer between " + LessonsLearned.MODULE_MIN
							+ " and " + LessonsLearned.MODULE_MAX + ".");
			ViewGuiNewLessonsLearned.alertModuleError.showAndWait();
			return;
		}

		String moduleErrMessage = LessonsLearned.validateModule(lessonModule);
		if (!moduleErrMessage.isEmpty()) {
			ViewGuiNewLessonsLearned.text_Module.setText("");
			ViewGuiNewLessonsLearned.alertModuleError.setContentText(
					moduleErrMessage + " Please enter a valid module number and try again.");
			ViewGuiNewLessonsLearned.alertModuleError.showAndWait();
			return;
		}

		// 5. Parse and Validate Category
		Category lessonCategory = LessonsLearned.parseCategory(categoryText);
		String categoryErrMessage = LessonsLearned.validateCategory(lessonCategory);
		if (!categoryErrMessage.isEmpty()) {
			ViewGuiNewLessonsLearned.text_Category.setText("");
			ViewGuiNewLessonsLearned.alertCategoryError.setContentText(
					categoryErrMessage + " Please enter a valid category and try again.");
			ViewGuiNewLessonsLearned.alertCategoryError.showAndWait();
			return;
		}

		// 6. Build the Lesson object and save to the database
		LessonsLearned lesson = new LessonsLearned();
		lesson.setTitle(lessonTitle);
		lesson.setDescription(lessonDescription);
		lesson.setAuthor(lessonAuthor);
		lesson.setModule(lessonModule);
		lesson.setCategory(lessonCategory);

		String dbError = theDatabase.createLesson(lesson);
		if (!dbError.isEmpty()) {
			ViewGuiNewLessonsLearned.alertAuthorError.setContentText(dbError);
			ViewGuiNewLessonsLearned.alertAuthorError.showAndWait();
			return;
		}

		// Clear fields and notify user of success
		ViewGuiNewLessonsLearned.text_Title.setText("");
		ViewGuiNewLessonsLearned.text_Description.setText("");
		ViewGuiNewLessonsLearned.text_Module.setText("");
		ViewGuiNewLessonsLearned.text_Category.setText("");

		ViewGuiNewLessonsLearned.alertLessonCreated.setContentText(
				"Lesson '" + lesson.getTitle() + "' (ID: " + lesson.getId() + ") was created!");
		ViewGuiNewLessonsLearned.alertLessonCreated.showAndWait();
	}

	/**********
	 * <p> Method: protected static void performReturn() </p>
	 * 
	 * <p> Description: Returns the user to their active home page. </p>
	 */
	protected static void performReturn() {
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(
					ViewGuiNewLessonsLearned.theStage, ViewGuiNewLessonsLearned.theUser);
			break;
		case 2:
			guiContributorHome.ViewContributorHome.displayContributorHome(
					ViewGuiNewLessonsLearned.theStage, ViewGuiNewLessonsLearned.theUser);
			break;
		case 3:
			guiRole2.ViewRole2Home.displayRole2Home(
					ViewGuiNewLessonsLearned.theStage, ViewGuiNewLessonsLearned.theUser);
			break;
		default:
			guiContributorHome.ViewContributorHome.displayContributorHome(
					ViewGuiNewLessonsLearned.theStage, ViewGuiNewLessonsLearned.theUser);
			break;
		}
	}

	/**********
	 * <p> Method: protected static void performQuit() </p>
	 * 
	 * <p> Description: Terminates the execution of the application. </p>
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}
}