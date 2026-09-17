package floatingPointRecognizer;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/*******
 * <p> Title: FloatingPointRecognizerMain Class - The GUI mainline version of this application</p>
 *
 * <p> Description: This is the Main Class that launches the FloatingPointRecognizer GUI version of
 * this demonstration application.  This is a JavaFX application designed to help student see how
 * integrate components into a larger application.
 * 
 * On startup, the application establishes the GUI's Model View Controller (MVC) View page, shows 
 * it to the user, and stops.  The application continues to run in the background with the JavaFX 
 * window displayed, waiting for the user to do something with the GUI elements in the window.
 * 
 * When the user interacts with the GUI's View, the GUI element the user used (e.g., a text input
 * field or a button) will cause either a model action (e.g. entering text into a text field) or a
 * controller action (e.g., pushing on a button) to perform an action.
 * 
 * In this GUI application, there is a text field into which the user is expected to enter the 
 * characters of what is supposed to be a floating point number and a button to press to signal
 * that the user is finished entering input.
 * 
 * This application does not use the command line arguments, but Java and JavaFX in Eclipse
 * requires they be made available, even if they are not needed.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 3.00	2025-09-01 Rewrite of this application for the Fall offering of CSE 360 and
 * other ASU courses.
 */


public class FPRMain extends Application {

	// These public constants allow all methods to have direct access to the size of the window.
	public final static double WINDOW_WIDTH = 500;
	public final static double WINDOW_HEIGHT = 200;
	
	// This public attribute makes it possible for the controller to hide the window after the 
	// user has finished entering the floating point value.  That action can only occur when the
	// input has been validated.
	public static Stage theStage;

	// This private attribute is the title to be used on the GUI's window.
	private String string_ApplicationTitle = new String("Enter The Numeric Value");

	/*******
	 * <p> Title: FloatingPointRecognizerMain Default Constructor </p>
	 * 
	 * <p> Description: This constructor does not perform any special function for this
	 * application. </p>
	 */
	public FPRMain() {
		// No special actions required
	}
	
	/*******
	 * <p> Title: main Method </p>
	 * 
	 * <p> Description: This method, main, calls the JavaFX system to launch the application.
	 * While it does accept the command line parameters (i.e., args), they are not used in this
	 * application and anything provided will be ignored by this application's code.  Once JavaFX
	 * is up and running, it invokes the start method with the expectation that it will produce and
	 * display a JavaFX GUI window that starts the user's engagement with the application. </p>
	 * 
	 * @param args Specifies the command line arguments for the application.	 
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*******
	 * <p> Title: start Method </p>
	 * 
     * <p> Description: The start method sets up the basic foundation for a JavaFX GUI application
     * and then turns control of the application to the provided GUI.  This application only
     * support a single stage, so it uses the stage passed into this method as it sets up the GUI
     * for the user. </p>
 	 * 
	 * @param primaryStage Specifies the Stage on which the GUI should be built.	 
	 */
	@Override
	public void start(Stage stage) {
		// Set theStage so the GUI can access it directly
		theStage = stage;
		
		// Label the window that holds the stage
		theStage.setTitle(string_ApplicationTitle);
		
		Pane theRoot = new Pane();			// Create a pane within the window
		
		new fprWindow.View(theRoot);		// Create the GUI using theRoot
		
		Scene theScene = new Scene(theRoot, WINDOW_WIDTH, WINDOW_HEIGHT);	// Create the scene
		
		theStage.setScene(theScene);						// Set the scene on the stage
		
		// Show the stage with the scene containing the pane (i.e., a window) to the user.  This 
		// means that the labels, fields, and buttons of the Graphical User Interface (GUI) are 
		// visible and it is now possible for the user to select input fields and enter values into
		// them, click on buttons, and read the labels, results, and any error messages.
		theStage.show();									// Show the stage to the user
		
		// With the window and its contents visible to the user, this thread of the execution ends.
	}
}