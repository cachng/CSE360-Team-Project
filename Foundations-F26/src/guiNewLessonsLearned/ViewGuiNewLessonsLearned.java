package guiNewLessonsLearned;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewGuiNewLessonsLearned Class. </p>
 * 
 * <p> Description: The ViewGuiNewLessonsLearned Page is used to enable a user to create a 
 * new Lesson Learned entry in the database. </p>
 */
public class ViewGuiNewLessonsLearned {

	/*-********************************************************************************************
	Attributes
	*********************************************************************************************/

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_ApplicationTitle = new Label("New Lesson Learned");
	protected static Label label_NewLessonCreation = new Label("New Lesson Learned");
	protected static Label label_NewLesson = new Label(
			"Please enter a title, a description, the module, and the category.");

	protected static TextField text_Title = new TextField();
	protected static TextField text_Description = new TextField();
	protected static String text_Author;
	protected static TextField text_Module = new TextField();
	protected static TextField text_Category = new TextField();
	protected static Button button_CreateLessonSetup = new Button("Lesson Creation");
	protected static Button button_Return = new Button("Return");
	protected static Button button_Quit = new Button("Quit");

	// Alerts for input validation errors and creation confirmation
	protected static Alert alertTitleError = new Alert(AlertType.INFORMATION);
	protected static Alert alertDescriptionError = new Alert(AlertType.INFORMATION);
	protected static Alert alertAuthorError = new Alert(AlertType.INFORMATION);
	protected static Alert alertModuleError = new Alert(AlertType.INFORMATION);
	protected static Alert alertCategoryError = new Alert(AlertType.INFORMATION);
	protected static Alert alertLessonCreated = new Alert(AlertType.INFORMATION);

	private static ViewGuiNewLessonsLearned theView;

	@SuppressWarnings("unused")
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	private static Pane theRootPane;
	protected static User theUser;

	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	private static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	public static Scene theNewLessonScene = null;

	/*-********************************************************************************************
	Constructors
	*********************************************************************************************/

	/**********
	 * <p> Method: displayNewLessonLearned(Stage ps, User user) </p>
	 * 
	 * <p> Description: Single entry point from outside this package to display the
	 * New Lesson Learned page. </p>
	 * 
	 * @param ps   specifies the JavaFX Stage to be used for this GUI
	 * @param user specifies the currently logged-in User (used as the lesson author)
	 */
	public static void displayNewLessonLearned(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) {
			theView = new ViewGuiNewLessonsLearned();
		}

		// Clear the input fields so previously entered values do not appear for a new Lesson
		text_Title.setText("");
		text_Description.setText("");
		text_Author = (theUser != null) ? theUser.getUserName() : "";
		text_Module.setText("");
		text_Category.setText("");

		// Place all of the established GUI elements into the pane
		theRootPane.getChildren().clear();
		theRootPane.getChildren().addAll(
				label_NewLessonCreation, label_NewLesson, line_Separator1,
				text_Title, text_Module, text_Description, text_Category,
				button_CreateLessonSetup, line_Separator4, button_Return, button_Quit);

		theStage.setTitle("CSE 360 Foundation Code: New Lesson Learned");
		theStage.setScene(theNewLessonScene);
		theStage.show();
	}

	/**********
	 * <p> Constructor: ViewGuiNewLessonsLearned() </p>
	 * 
	 * <p> Description: Initializes all of the common GUI widgets for the page once. </p>
	 */
	private ViewGuiNewLessonsLearned() {
		theRootPane = new Pane();
		theNewLessonScene = new Scene(theRootPane, width, height);

		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		setupLabelUI(label_NewLessonCreation, "Arial", 32, width, Pos.CENTER, 0, 10);
		setupLabelUI(label_NewLesson, "Arial", 18, width, Pos.CENTER, 0, 60);

		// Title input field
		setupTextUI(text_Title, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 140, true);
		text_Title.setPromptText("Enter the title (max 100 chars)");

		// Module input field
		setupTextUI(text_Module, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 200, true);
		text_Module.setPromptText("Enter module number (1 - 20)");

		// Description input field
		setupTextUI(text_Description, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 260, true);
		text_Description.setPromptText("Enter description (min 20 chars)");

		// Category input field
		setupTextUI(text_Category, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 320, true);
		text_Category.setPromptText("Category (GENERAL, HOMEWORK, EXAM, PROJECTS)");

		// Configure alert headers and titles
		alertTitleError.setTitle("Invalid Title");
		alertTitleError.setHeaderText("The lesson title is not valid.");

		alertDescriptionError.setTitle("Invalid Description");
		alertDescriptionError.setHeaderText("The lesson description is not valid.");

		alertAuthorError.setTitle("Invalid Author");
		alertAuthorError.setHeaderText("The lesson author is not valid.");

		alertModuleError.setTitle("Invalid Module");
		alertModuleError.setHeaderText("The module number is not valid.");

		alertCategoryError.setTitle("Invalid Category");
		alertCategoryError.setHeaderText("The category is not valid.");

		alertLessonCreated.setTitle("Lesson Created");
		alertLessonCreated.setHeaderText("Success!");

		// Set up the Lesson Creation button
		setupButtonUI(button_CreateLessonSetup, "Dialog", 18, 200, Pos.CENTER, 475, 230);
		button_CreateLessonSetup.setOnAction((_) -> {
			ControllerGuiNewLessonsLearned.doCreateLesson();
		});

		// Set up Return and Quit buttons
		setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 50, 540);
		button_Return.setOnAction((_) -> {
			ControllerGuiNewLessonsLearned.performReturn();
		});

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 450, 540);
		button_Quit.setOnAction((_) -> {
			ControllerGuiNewLessonsLearned.performQuit();
		});
	}

	/*-********************************************************************************************
	Helper methods to reduce code length
	*********************************************************************************************/

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}