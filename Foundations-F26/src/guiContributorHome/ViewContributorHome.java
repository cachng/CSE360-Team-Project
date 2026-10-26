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
 * <p> Description: The Java/FX ContributorHome View.  This class provides the view actions for the contributor role.
 * to allow any user with the contributor role to create lessons learned. * 
 * The view gives the user a visual GUI to create, delete, edit, and add additional comments to their lessonlearned. 
 * CRUD functionality exists to make the process seemless. 
 * It also supports the user click on the "Quit" button widget.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p> */



public class ViewContributorHome {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// GUI Area 2: Lesson Actions and ListView
	protected static Button button_CreateLesson = new Button("Create New Lesson");
	protected static Button button_EditLesson = new Button("Edit Selected Lesson");
	protected static Button button_AddExperience = new Button("Add Experience Notes");
	protected static Button button_DeleteLesson = new Button("Delete Selected Lesson");

	protected static Label label_MyLessons = new Label("My Lessons Learned (Select one to manage):");
	protected static ListView<String> listView_Lessons = new ListView<String>();

	// GUI Area 3
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewContributorHome theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theViewRole1HomeScene;
	protected static final int theRole = 2;

	public static void displayContributorHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null)
			theView = new ViewContributorHome();

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());
		refreshLessonList();

		theStage.setTitle("CSE 360 Foundations: Contributor Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}

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

	protected static void refreshLessonList() {
		ObservableList<String> items = FXCollections.observableArrayList();
		if (theUser != null) {
			LessonsLearnedList lessons = theDatabase.getLessonsByAuthor(theUser.getUserName());
			for (LessonsLearned l : lessons.getAll()) {
				items.add("ID: " + l.getId() + " | [" + l.getCategory() + " - Mod " + l.getModule() + "] "
						+ l.getTitle() + " (Hours: " + l.getHoursSpent() + ")");
			}
		}
		if (items.isEmpty()) {
			items.add("No lessons created yet. Click 'Create New Lesson' to add one.");
		}
		listView_Lessons.setItems(items);
	}

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}