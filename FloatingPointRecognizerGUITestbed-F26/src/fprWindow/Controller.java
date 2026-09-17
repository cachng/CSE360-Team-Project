package fprWindow;

public class Controller {

/*******
 * <p> Title: Controller Class - Based on the current state and user input invoke the right action.
 * </p>
 *
 * <p> Description: This Controller class is a major component of a Model View Controller (MVC)
 * application design that provides the user with Graphical User Interface with JavaFX
 * widgets as opposed to a command line interface.
 * 
 * In this case there is not much for the controller to do.  The user enters text into a text field
 * and presses a button.  The in the first case, the observer pattern is used to connect any change
 * in the input field directly to the code in the Model to evaluate the updated input without 
 * needing to involve the controller.  When the GUI's button is pressed, that event invokes the
 * handler in this class.
 * 
 * This class contains no state of it's own, so there is no need to instantiate it.  Therefore
 * there are no constructors.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 2.00	2025-09-02 Rewrite of this application for the Fall 2025 offering of CSE 360
 * and other ASU courses.
 */


	/*******
	 * <p> Title: handleButtonPress - Handle the user action of clicking on the GUI's button</p>
	 * 
	 * <p> Description: This method invoked when the user click on the GUI's button to hide the
	 * GUI's window and return the results.  In this case, it just prints them.  Access to this 
	 * method is constrained to just those classes in the fprWindow package.
	 * 
	 * The design of the application requires that the only time the GUI button that invokes this
	 * method is enabled is when the input has been evaluated and there are no errors (i.e, all of
	 * the requirements have been satisfied.  That is why this method does not need to check to see
	 * that the input is valid.</p>
	 */

	protected static void handleButtonPress() {
		floatingPointRecognizer.FPRMain.theStage.hide();
		String theNumber = View.text_Number.getText();
		System.out.println("The number returned was: "+ theNumber);
    }
}
