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
 * <p> Description: The Java/FX New Lesson Learned View.  This page lets a Contributor enter a
 * new lesson learned and save it in the database.  It supports the Contributor User Story
 * for creating a lesson learned.  The Contributor enters a title, a module number, a
 * description, and a category; the author is filled in automatically from the logged-in
 * user, so a lesson is always credited to the person who wrote it.</p>
 * 
 * <p> This is the View part of the Model-View-Controller pattern used by the Foundations GUI
 * packages.  The View creates and places the widgets and holds the alerts used to report
 * results.  When the Lesson Creation button is pressed, it calls
 * ControllerGuiNewLessonsLearned.doCreateLesson(), which reads the fields, validates them
 * with the LessonsLearned validation methods, and saves the lesson.</p>
 * 
 * <p> Following the Foundations pattern, the widgets are static and the View is a singleton:
 * the page is built only once and reused, and the input fields are cleared every time it is
 * displayed.  The fields and alerts are "protected" so only the Controller in this package
 * can reach them.  Do not change them to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author TP2 Team
 * 
 * @version 1.00		2026-10-22 Initial version, based on the Foundations View pattern
 * @version 1.01		2026-10-26 Completed the Javadoc for all attributes and methods
 */
public class ViewGuiNewLessonsLearned {

	/*-********************************************************************************************
	Attributes
	*********************************************************************************************/

	/** Width of the window, taken from FoundationsMain so every page is the same size. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Height of the window, taken from FoundationsMain so every page is the same size. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** Application title label.  It is set up but not currently placed on the page;
	 * label_NewLessonCreation is used as the page title instead. */
	private static Label label_ApplicationTitle = new Label("New Lesson Learned");

	/** Page title shown at the top of the window. */
	protected static Label label_NewLessonCreation = new Label("New Lesson Learned");

	/** Instructions telling the Contributor which fields to fill in. */
	protected static Label label_NewLesson = new Label(
			"Please enter a title, a description, the module, and the category.");

	/** Input for the lesson title.  Checked by LessonsLearned.validateTitle(). */
	protected static TextField text_Title = new TextField();

	/** Input for the lesson description.  Checked by LessonsLearned.validateDescription(). */
	protected static TextField text_Description = new TextField();

	/**
	 * The author's username.  This is a String, not an input field, because the author is
	 * always the logged-in user and must not be typed in.  This stops a Contributor from
	 * creating a lesson under someone else's name.
	 */
	protected static String text_Author;

	/** Input for the course module number.  Converted to a number and checked by
	 * LessonsLearned.validateModule(). */
	protected static TextField text_Module = new TextField();

	/** Input for the category.  Converted with LessonsLearned.parseCategory() and checked by
	 * LessonsLearned.validateCategory(). */
	protected static TextField text_Category = new TextField();

	/** Validates the inputs and saves the new lesson (Contributor User Story: create a lesson). */
	protected static Button button_CreateLessonSetup = new Button("Lesson Creation");

	/** Returns to the Contributor home page without saving. */
	protected static Button button_Return = new Button("Return");

	/** Ends the application. */
	protected static Button button_Quit = new Button("Quit");

	// Alerts for input validation errors and creation confirmation.
	// There is one alert per field so each error has a title and header naming the field to
	// fix; the Controller fills in the specific message from the matching validate method.

	/** Shown when the title is not valid. */
	protected static Alert alertTitleError = new Alert(AlertType.INFORMATION);

	/** Shown when the description is not valid. */
	protected static Alert alertDescriptionError = new Alert(AlertType.INFORMATION);

	/** Shown when the author is not valid (for example, no user is logged in). */
	protected static Alert alertAuthorError = new Alert(AlertType.INFORMATION);

	/** Shown when the module is not a number or is out of range. */
	protected static Alert alertModuleError = new Alert(AlertType.INFORMATION);

	/** Shown when the category does not match one of the allowed categories. */
	protected static Alert alertCategoryError = new Alert(AlertType.INFORMATION);

	/** Shown when the lesson has been saved successfully. */
	protected static Alert alertLessonCreated = new Alert(AlertType.INFORMATION);

	/**
	 * The single instance of this View.  It is null until the page is first displayed;
	 * after that it shows the page has been built and does not need to be built again.
	 */
	private static ViewGuiNewLessonsLearned theView;

	/** The shared Database object created in FoundationsMain.  Saving is done by the
	 * Controller, so the View does not use it directly. */
	@SuppressWarnings("unused")
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The application's main window, which this page is shown in. */
	protected static Stage theStage;

	/** The pane that holds every widget on this page. */
	private static Pane theRootPane;

	/** The logged-in user, who becomes the author of the new lesson. */
	protected static User theUser;

	/** Separates the title area from the input area. */
	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	/** Separates the input area from the Return and Quit buttons. */
	private static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	/** The scene for this page. */
	public static Scene theNewLessonScene = null;

	/*-********************************************************************************************
	Constructors
	*********************************************************************************************/

	/**********
	 * <p> Method: displayNewLessonLearned(Stage ps, User user) </p>
	 * 
	 * <p> Description: Single entry point from outside this package to display the New
	 * Lesson Learned page.  ControllerContributorHome calls this when the Contributor presses
	 * Create New Lesson. </p>
	 * 
	 * <p> It saves the stage and user, builds the page the first time only, clears every
	 * input field so values from an earlier lesson do not appear, sets the author to the
	 * logged-in user, and shows the page. </p>
	 * 
	 * @param ps   specifies the JavaFX Stage to be used for this GUI
	 * @param user specifies the currently logged-in User (used as the lesson author)
	 */
	public static void displayNewLessonLearned(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		// Build the page only once (singleton, as in the other Foundations views)
		if (theView == null) {
			theView = new ViewGuiNewLessonsLearned();
		}

		// Clear the input fields so previously entered values do not appear for a new Lesson
		text_Title.setText("");
		text_Description.setText("");
		// The author is always the logged-in user; an empty author is rejected on save
		text_Author = (theUser != null) ? theUser.getUserName() : "";
		text_Module.setText("");
		text_Category.setText("");

		// Place all of the established GUI elements into the pane.  The pane is cleared
		// first so the widgets are not added twice when the page is shown again.
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
	 * <p> Description: Initializes all of the common GUI widgets for the page once: the
	 * labels, the four input fields with prompt text, the alerts, and the buttons, which are
	 * connected to their Controller methods.  It is private so it can only be called by
	 * {@link #displayNewLessonLearned(Stage, User)}, which makes sure it runs only once. </p>
	 * 
	 * <p> The prompt text in each field shows the rule the input must follow (for example,
	 * "max 100 chars"), so the Contributor knows the limits before typing.  These match the
	 * limits defined in the LessonsLearned class. </p>
	 */
	private ViewGuiNewLessonsLearned() {
		theRootPane = new Pane();
		theNewLessonScene = new Scene(theRootPane, width, height);

		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		setupLabelUI(label_NewLessonCreation, "Arial", 32, width, Pos.CENTER, 0, 10);
		setupLabelUI(label_NewLesson, "Arial", 18, width, Pos.CENTER, 0, 60);

		// Title input field (limit matches LessonsLearned.TITLE_MAX)
		setupTextUI(text_Title, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 140, true);
		text_Title.setPromptText("Enter the title (max 100 chars)");

		// Module input field (range matches LessonsLearned.MODULE_MIN and MODULE_MAX)
		setupTextUI(text_Module, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 200, true);
		text_Module.setPromptText("Enter module number (1 - 20)");

		// Description input field (limit matches LessonsLearned.DESCRIPTION_MIN)
		setupTextUI(text_Description, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 260, true);
		text_Description.setPromptText("Enter description (min 20 chars)");

		// Category input field (choices match the LessonsLearned.Category values)
		setupTextUI(text_Category, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 320, true);
		text_Category.setPromptText("Category (GENERAL, HOMEWORK, EXAM, PROJECTS)");

		// Configure alert headers and titles; the Controller sets each specific message
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

		// Set up the Lesson Creation button; the Controller does the validation and saving
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

	/**********
	 * <p> Method: setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
	 * double y) </p>
	 * 
	 * <p> Description: Sets the font, minimum width, alignment, and position of a label in
	 * one call, as in the other Foundations views. </p>
	 * 
	 * @param l		the label to set up
	 * @param ff	the font family name
	 * @param f		the font size
	 * @param w		the minimum width
	 * @param p		the alignment of the text within the label
	 * @param x		the distance from the left edge of the window
	 * @param y		the distance from the top edge of the window
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * <p> Method: setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
	 * double y) </p>
	 * 
	 * <p> Description: Sets the font, minimum width, alignment, and position of a button in
	 * one call, as in the other Foundations views. </p>
	 * 
	 * @param b		the button to set up
	 * @param ff	the font family name
	 * @param f		the font size
	 * @param w		the minimum width
	 * @param p		the alignment of the text within the button
	 * @param x		the distance from the left edge of the window
	 * @param y		the distance from the top edge of the window
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * <p> Method: setupTextUI(TextField t, String ff, double f, double w, Pos p, double x,
	 * double y, boolean e) </p>
	 * 
	 * <p> Description: Sets the font, width, alignment, position, and editability of a text
	 * field in one call, as in the other Foundations views.  The minimum and maximum widths
	 * are both set so every input field is exactly the same width. </p>
	 * 
	 * @param t		the text field to set up
	 * @param ff	the font family name
	 * @param f		the font size
	 * @param w		the width of the field
	 * @param p		the alignment of the text within the field
	 * @param x		the distance from the left edge of the window
	 * @param y		the distance from the top edge of the window
	 * @param e		true if the user can type in the field, false if it is read-only
	 */
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