package fprWindow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import floatingPointRecognizer.FPRMain;

/*******
 * <p> Title: View Class - establishes the Graphics User interface, presents information to the
 * user, and accept information from the user.</p>
 *
 * <p> Description: This View class is a major component of a Model View Controller (MVC)
 * application design that provides the user with Graphical User Interface with JavaFX
 * widgets as opposed to a command line interface.
 * 
 * In this case the GUI consists of numerous widgets to show the user where to enter the password,
 * where any errors are located, and a set of requirements for a valid password and whether or not
 * they have been satisfied
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 3.00	2025-07-30 Rewrite of this application for the Fall 2025 offering of CSE 360
 * and other ASU courses.
 */

public class View {

	/*
	 * The following private static objects that enable the view to interact with the other two MVC
	 * singleton objects.
	 */
//	private static View theView = null;
//	private static Model theModel = null;
//	private static Controller theController = null;

	/*
	 * The following private objects are with GUI widgets that the view manages.
	 */
	private double windowWidth = FPRMain.WINDOW_WIDTH;
	
	/*
	 * A Label for the text input field and the text input field
	 * 
	 */
	private Label label_NumericInputField = new Label("Enter the numeric value here");
	protected static TextField text_Number = new TextField();

	/* 
	 * Feedback labels to show the user where the error is located.  For this application, the
	 * errors are ones of omission, so the "error" is always at the end of the input (e.g., a
	 * password must have an upper case letter.  If the current input does not include an upper 
	 * case character, this can be corrected by adding one.  We so this at the end, but in 
	 * reality, the upper case could be anywhere in the password.
	 * 
	 */
	protected static Label label_InputError = new Label();	// There error labels change based on the
	protected static Label noInputFound = new Label();			// user's input
	private static TextFlow inputError;
    protected static Text inputErrorPart1 = new Text();		// This contains the user's input
    protected static Text inputErrorPart2 = new Text();		// This is the up arrow
    protected static Label inputErrorPart3 = new Label();		// This is the concatenation of the two

    /* 
	 * Feedback labels with text and color to specify which of the requirements have been satisfied
	 * (using both text and the color green) and which have not (both with text and the color red).
	 * 
	 */
    protected static Label validNumber = new Label();		// This only appears with a valid password

    /* 
	 * Button to finish the process.  It only become active when all the requirements have been met
	 * 
	 */
    protected static Button button_Finish = new Button();

	
	/*******
	 * <p> Title: View - Default Constructor </p>
	 * 
	 * <p> Description: This constructor does not perform any special function for this
	 * application. </p>
	 */
	public View() {
		// No special actions required
	}

	
	/*******
	 * <p> Title: View - Constructor </p>
	 * 
	 * <p> Description: This is the constructor for this singleton class.  It creates the various
	 * GUI elements, specifies attributes for each (e.g., the location in the window and a call to
	 * a handler method to be used when the GUI element is used) and adds these elements to a window
	 * a window pane.
	 * 
	 * Some of these widgets change based on the input the user provides.  In those cases, the text
	 * and the color will be programmatically set within the Model component of the MVC and no text
	 * or color attribute will be set here.
	 * 
	 * This is NOT how one writes a singleton class in normal practice.  Typically, a singleton
	 * is written so it can be created by any number of ways, but once it is created there is no
	 * need to create it again.  If this code were to follow the typical pattern, the code would
	 * be as follows:
	 * 
	 * 		private View theView = null;
	 * 
	 * 		public View getView(Pane theRoot){
	 * 			if (theView == null) theView = new View(theRoot);
	 * 			return theView;
	 * 		}
	 * 
	 * @param theRoot Specifies the Pane on which the GUI widgets should be added.	 
	 */
	
	public View (Pane theRoot) {

		// Label the password input field with a title just above it, left aligned
		setupLabelWidget(label_NumericInputField, 10, 10, "Arial", 14, windowWidth-10, Pos.BASELINE_LEFT);
		
		// Establish the text input operand field and when anything changes in the password,
		// the code will process the entire input to ensure that it is valid or in error.
		setupTextWidget(text_Number, 10, 30, "Arial", 18, windowWidth-20, Pos.BASELINE_LEFT, 
				true);
		text_Number.textProperty().addListener((observable, oldValue, newValue) 
				-> {button_Finish.setDisable(true);		// Disable until a successful check
					Model.inputUpdated(); 			// Check the updated input
					});
		
		// Establish an error message for when there is no input
		setupLabelWidget(noInputFound, 10, 80, "Arial", 14, windowWidth-10, Pos.BASELINE_LEFT);	
		noInputFound.setText("No input text found!");	// This changes based on input, but we 
		noInputFound.setTextFill(Color.RED);			// want this to be shown at startup
		
		// Establish an error message for the password, left aligned
		label_InputError.setTextFill(Color.RED);
		label_InputError.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelWidget(label_InputError, 22, 96, "Arial", 14, windowWidth-150-10, 
				Pos.BASELINE_LEFT);		
				
		// Error Message components for the Password
		inputErrorPart1.setFill(Color.BLACK);		// The user input is copied for this part
	    inputErrorPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
	    
	    inputErrorPart2.setFill(Color.RED);		// A red up arrow is added next
	    inputErrorPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
		
	    inputError = new TextFlow(inputErrorPart1, inputErrorPart2);
		inputError.setMinWidth(windowWidth-10);	// The two parts are merged into one
		inputError.setLayoutX(22);					// and positioned directly below the text
		inputError.setLayoutY(70);					// input field
		
		setupLabelWidget(inputErrorPart3, 20, 95, "Arial", 14, 200, Pos.BASELINE_LEFT);
											// Position the composition object on the window
				
		// Setup the valid Password message, which is used when all the requirements have been met
		validNumber.setTextFill(Color.GREEN);
		setupLabelWidget(validNumber, 10, 125, "Arial", 18, windowWidth-150-10, 
				Pos.BASELINE_LEFT);
		
		// Setup the Finish and Save The Password button
		setupButtonWidget(button_Finish, "Finish and Save The Number", (windowWidth-250)/2,
				160, "Arial", 14, 250, Pos.BASELINE_CENTER);
		button_Finish.setDisable(true);						// It is initially disabled
		button_Finish.setOnAction(new EventHandler<>() {	// Specify the event handler to be
        	public void handle(ActionEvent event) {			// called when the button is pressed
        		Controller.handleButtonPress();
        	}
        });

		// Place the just-initialized GUI widgets into the pane, whether they have text or not
		// and or a fixed color
		theRoot.getChildren().addAll(label_NumericInputField, text_Number, noInputFound, 
				label_InputError, inputError, inputErrorPart3, validNumber,
				button_Finish);
	}
	
	/*******
	 * <p> Title: View - Public Method the returns a reference to the View singleton object</p>
	 * 
	 * <p> Description: This method return a reference the to singleton View.  The design the this
	 * application ensures the singleton has already been created, so there is no need to check to 
	 * see if the object is there. This is in conflict with the general notion of singletons, where
	 * the code must first see if the object has been instantiated and if not, instantiate it.
	 * 
	 * @return The method returns a reference to the View singleton object.
	 */
	
//	static public View getView() {
//		return theView;
//	}
	
	
	/*
	 * Private local method to initialize the standard attribute fields for a label
	 */
	private void setupLabelWidget(Label l, double x, double y, String ff, double f, double w, 
			Pos p){
		l.setLayoutX(x);
		l.setLayoutY(y);		
		l.setFont(Font.font(ff, f));	// The font face and the font size
		l.setMinWidth(w);
		l.setAlignment(p);
	}

	
	/*
	 * Private local method to initialize the standard attribute fields for a text field
	 */
	private void setupTextWidget(TextField t, double x, double y, String ff, double f, double w, 
			Pos p, boolean e){
		t.setFont(Font.font(ff, f));	// The font face and the font size
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}

	
	/*
	 * Private local method to initialize the standard fields for a button
	 */
	private void setupButtonWidget(Button b, String s, double x, double y, String ff, double f, double w, 
			Pos p){
		b.setText(s);
		b.setFont(Font.font(ff, f));	// The font face and the font size
		b.setMinWidth(w);
		b.setMaxWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
