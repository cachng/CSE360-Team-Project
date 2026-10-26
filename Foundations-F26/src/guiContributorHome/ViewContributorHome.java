package guiContributorHome;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;

/*******
 * <p> Title: ViewContributorHome Class. </p>
 * 
 * <p> Description: The Java/FX ContributorHome View.  This class builds and displays the home
 * page for a user with the Contributor role.  From this page a Contributor can see a list of
 * the lessons learned they have written and create, edit, add experience information to, and
 * delete those lessons.  It also supports the Account Update, Logout, and Quit buttons found
 * on the other Foundations home pages.</p>
 * 
 * <p> This is the View part of the Model-View-Controller pattern used by the Foundations GUI
 * packages.  The View only creates the widgets, places them in the window, and shows data.
 * When a button is pressed, it calls the matching method in ControllerContributorHome, which
 * does the work.  This keeps the screen layout separate from the logic, so either can be
 * changed without breaking the other.</p>
 * 
 * <p> The window is divided into three areas, following the layout of the other Foundations
 * home pages:</p>
 * <ul>
 *   <li> Area 1: the page title, the current user, and the Account Update button </li>
 *   <li> Area 2: the lesson action buttons and the list of the Contributor's lessons </li>
 *   <li> Area 3: the Logout and Quit buttons </li>
 * </ul>
 * 
 * <p> Following the Foundations pattern, the widgets are static and the View is a singleton:
 * the window is built only once, the first time it is displayed, and is reused after that.
 * The widgets are declared "protected" so that only the Controller in this package can reach
 * them (for example, to read which lesson is selected).  Do not change them to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author Project Team 37
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 2.00		2026-10-22 Changed the page to support the Contributor User Stories for
 * lessons learned
 * @version 2.01		2026-10-26 Completed the Javadoc for all attributes and methods
 */
public class ViewContributorHome {

	/** Width of the window, taken from FoundationsMain so every page is the same size. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Height of the window, taken from FoundationsMain so every page is the same size. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// ----- GUI Area 1: page title, current user, and account update -----

	/** Title shown at the top of the page so the user knows which page they are on. */
	protected static Label label_PageTitle = new Label();

	/** Shows the username of the logged-in Contributor. */
	protected static Label label_UserDetails = new Label();

	/** Opens the Foundations User Update page so the user can change their account. */
	protected static Button button_UpdateThisUser = new Button("Account Update");

	/** Separates Area 1 from Area 2. */
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// ----- GUI Area 2: lesson actions and the list of the Contributor's lessons -----

	/** Opens the New Lesson Learned window (Contributor User Story: create a lesson). */
	protected static Button button_CreateLesson = new Button("Create New Lesson");

	/** Edits the selected lesson (Contributor User Story: update a lesson). */
	protected static Button button_EditLesson = new Button("Edit Selected Lesson");

	/** Records experience information for the selected lesson (planned for the TP3 epics). */
	protected static Button button_AddExperience = new Button("Add Experience Notes");

	/** Deletes the selected lesson after confirming (Contributor User Story: delete a lesson). */
	protected static Button button_DeleteLesson = new Button("Delete Selected Lesson");

	/** Heading for the lesson list, telling the user to select a lesson before acting on it. */
	protected static Label label_MyLessons = new Label("My Lessons Learned (Select one to manage):");

	/**
	 * The list of lessons written by this Contributor (Contributor User Story: view my
	 * lessons).  Each entry is a single line of text that starts with "ID: n |", so the
	 * Controller can read the id of the selected lesson from it.  It is protected so the
	 * Controller can get the current selection.
	 */
	protected static ListView<String> listView_Lessons = new ListView<String>();

	// ----- GUI Area 3: logout and quit -----

	/** Separates Area 2 from Area 3. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	/** Logs the user out and returns to the login page. */
	protected static Button button_Logout = new Button("Logout");

	/** Ends the application. */
	protected static Button button_Quit = new Button("Quit");

	// ----- Other attributes -----

	/**
	 * The single instance of this View.  It is null until the page is first displayed;
	 * after that it shows the window has been built and does not need to be built again.
	 */
	private static ViewContributorHome theView;

	/** The shared Database object created in FoundationsMain, used to read the lessons. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The application's main window, which this page is shown in.  Used by the Controller
	 * to open other pages in the same window. */
	protected static Stage theStage;

	/** The pane that holds every widget on this page. */
	protected static Pane theRootPane;

	/** The logged-in user.  Used to show their name, list their lessons, and identify them
	 * to the Database as the author when lessons are changed. */
	protected static User theUser;

	/** The scene for this page.  (The name comes from the Foundations role home page this
	 * class was based on.) */
	private static Scene theViewRole1HomeScene;

	/** The number FoundationsMain uses to record that the Contributor home page is active. */
	protected static final int theRole = 2;

	/**********
	 * <p> Method: displayContributorHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: The single entry point for showing the Contributor home page.  Other
	 * pages call this, for example after a Contributor logs in or after a new lesson is
	 * saved. </p>
	 * 
	 * <p> It saves the stage and user, builds the window the first time only, records that
	 * this page is active, and reloads the lesson list so it always shows the current lessons
	 * (including any just created or changed on another page). </p>
	 * 
	 * @param ps	the JavaFX Stage (window) to show the page in
	 * @param user	the logged-in Contributor
	 */
	public static void displayContributorHome(Stage ps, User user) {
		// Save these first, because the constructor and the Controller use them
		theStage = ps;
		theUser = user;

		// Build the window only once (singleton, as in the other Foundations views)
		if (theView == null)
			theView = new ViewContributorHome();

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		// Update the parts that change from one visit to the next
		label_UserDetails.setText("User: " + theUser.getUserName());
		refreshLessonList();

		theStage.setTitle("CSE 360 Foundations: Contributor Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}

	/**********
	 * <p> Constructor: ViewContributorHome() </p>
	 * 
	 * <p> Description: Builds the page: creates the scene, sets the text, font, size, and
	 * position of every widget, connects each button to its Controller method, and adds the
	 * widgets to the window.  It is private so it can only be called by
	 * {@link #displayContributorHome(Stage, User)}, which makes sure it runs only once. </p>
	 */
	private ViewContributorHome() {
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Contributor Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {
			ControllerContributorHome.performUpdate();
		});

		// GUI Area 2: Action Buttons & ListView
		// Each button only calls the Controller; the View does no lesson logic itself (MVC)
		setupButtonUI(button_CreateLesson, "Dialog", 16, 220, Pos.CENTER, 20, 110);
		button_CreateLesson.setOnAction((_) -> {
			ControllerContributorHome.doCreateLesson();
		});

		setupButtonUI(button_EditLesson, "Dialog", 16, 220, Pos.CENTER, 290, 110);
		button_EditLesson.setOnAction((_) -> {
			ControllerContributorHome.doEditLesson();
		});

		setupButtonUI(button_AddExperience, "Dialog", 16, 220, Pos.CENTER, 20, 150);
		button_AddExperience.setOnAction((_) -> {
			ControllerContributorHome.doAddExperience();
		});

		setupButtonUI(button_DeleteLesson, "Dialog", 16, 220, Pos.CENTER, 290, 150);
		button_DeleteLesson.setOnAction((_) -> {
			ControllerContributorHome.doDeleteLesson();
		});

		setupLabelUI(label_MyLessons, "Arial", 16, 400, Pos.BASELINE_LEFT, 20, 190);

		// The list fills the width of the window between the buttons and Area 3
		listView_Lessons.setLayoutX(20);
		listView_Lessons.setLayoutY(215);
		listView_Lessons.setPrefWidth(width - 40);
		listView_Lessons.setPrefHeight(295);

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> {
			ControllerContributorHome.performLogout();
		});

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> {
			ControllerContributorHome.performQuit();
		});

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				button_CreateLesson, button_EditLesson, button_AddExperience, button_DeleteLesson,
				label_MyLessons, listView_Lessons,
				line_Separator4, button_Logout, button_Quit);
	}

	/**********
	 * <p> Method: refreshLessonList() </p>
	 * 
	 * <p> Description: Reloads the list with the lessons written by the logged-in
	 * Contributor, using Database.getLessonsByAuthor(), which returns a LessonsLearnedList.
	 * This supports the Contributor User Story for viewing one's own lessons.  The Controller
	 * calls it after every create, edit, experience update, or delete, so the list always
	 * matches what is stored. </p>
	 * 
	 * <p> Each lesson is shown on one line as
	 * "ID: n | [CATEGORY - Mod m] Title (Hours: h)".  The line must start with "ID: n |"
	 * because ControllerContributorHome.getSelectedLessonId() reads the id from that part.
	 * If the Contributor has no lessons, a hint telling them how to create one is shown
	 * instead.  The hint does not start with "ID:", so it cannot be selected as a lesson. </p>
	 */
	protected static void refreshLessonList() {
		ObservableList<String> items = FXCollections.observableArrayList();
		if (theUser != null) {
			LessonsLearnedList lessons = theDatabase.getLessonsByAuthor(theUser.getUserName());
			for (LessonsLearned l : lessons.getAll()) {
				// Keep "ID: n |" at the start; the Controller depends on this format
				items.add("ID: " + l.getId() + " | [" + l.getCategory() + " - Mod " + l.getModule() + "] "
						+ l.getTitle() + " (Hours: " + l.getHoursSpent() + ")");
			}
		}
		if (items.isEmpty()) {
			items.add("No lessons created yet. Click 'Create New Lesson' to add one.");
		}
		listView_Lessons.setItems(items);
	}

	/**********
	 * <p> Method: setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
	 * double y) </p>
	 * 
	 * <p> Description: Helper that sets the font, minimum width, alignment, and position of
	 * a label in one call, as in the other Foundations views. </p>
	 * 
	 * @param l		the label to set up
	 * @param ff	the font family name
	 * @param f		the font size
	 * @param w		the minimum width
	 * @param p		the alignment of the text within the label
	 * @param x		the distance from the left edge of the window
	 * @param y		the distance from the top edge of the window
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
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
	 * <p> Description: Helper that sets the font, minimum width, alignment, and position of
	 * a button in one call, as in the other Foundations views. </p>
	 * 
	 * @param b		the button to set up
	 * @param ff	the font family name
	 * @param f		the font size
	 * @param w		the minimum width
	 * @param p		the alignment of the text within the button
	 * @param x		the distance from the left edge of the window
	 * @param y		the distance from the top edge of the window
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}