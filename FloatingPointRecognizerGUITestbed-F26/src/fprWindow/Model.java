package fprWindow;

public class Model {
	/**
	 * <p> Title: FSM-translated FloatringPointRecognizer. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of a Finite State Machine
	 * diagram into an executable Java program using the FloatingPointRecognizer  diagram. The
	 * detailed design is based on a while loop with a select list to direct execution to the
	 * appropriate state.</p>
	 * 
	 * <p>The is a static class that is not instantiated as there is only one caller and there are
	 * no multiple threads of execution calling this class.
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2025 </p>
	 * 
	 * @author Lynn Robert Carter
	 * 
	 * @version 1.00		202-09-01	Initial baseline derived from UserNameRecognizer
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String fprErrorMessage = "";			// The error message text
	public static String fpRecognizerInput = "";		// The input being processed
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// Specifies if the FSM is running
	private static int floatingPointNumberSize = 0;		// The size of the number
	private static boolean updateView;					// True when the GUI View should be updated

	// This attribute is static as it is needed by the static testing automation mainline.
	public static int floatingPointRecognizerIndexofError = -1;		// The index of error location

	
	/*******
	 * <p> Title: inputUpdated - A method to address the impact of changes to the input</p>
	 * 
	 * <p> Description: This protected method was written to allow the View to request the Model
	 * to process the input and update the model, the View, and the console output as required.
	 * The checkForValidFoatingPointNumber method sets output fields in the View, based on what it
	 * finds, and the display will be updated.</p>
	 */

	protected static void inputUpdated() {
		View.noInputFound.setText("");
		String theInput = View.text_Number.getText();
		// Check to ensure that there is input to process
		if(theInput.length() <= 0) {
			View.noInputFound.setText("No input text found!");
			View.inputErrorPart1.setText("");			// Reset the detailed error message parts
			View.inputErrorPart2.setText("");
			View.inputErrorPart3.setText("");
			floatingPointRecognizerIndexofError = 0;	// Error at first character;
			View.validNumber.setText("");				// It can't be a valid number
			return;
		}
		String theError = checkForValidFoatingPointNumber(theInput, true);
		System.out.println(theError);
	}
	
	/*******
	 * <p> Title: getIndexofError - A method to obtain the location of an input error</p>
	 * 
	 * <p> Description: This method is provided to support the testing automation version of the
	 * application so it can provide a better error message on the console output.  This method is
	 * static, because it is call from the static testing automation mainline.</p>
	 */

	public static int getIndexofError() {
		return floatingPointRecognizerIndexofError;
	}


	/*******
	 * <p> Title: checkForValidFoatingPointNumber - A method to see if a string is a valid number
	 * </p>
	 * 
	 * <p> Description: This public method was written to allow the View in t he GUI application
	 * to request the Model to process the input and update the model and the console output as 
	 * required.  It was also written to allow the testing automation console application to check
	 * the input, but it does not use the View.  To support this, a second parameter is used to
	 * signal whether or not the View should be updated. If the second parameter is true, the method 
	 * updates the View to support the GUI version of the application.  If the second parameter is
	 * false, this is being called by the testing automation, so it does not update the View.</p>
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @param uV		A Boolean flag that indicates if the View should be updated
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkForValidFoatingPointNumber(String input, boolean uV) {

		// Set the flag to specify if the results should update the View.  It must be true for the
		// the GUI application and it must be false for the testing automation application.
		updateView = uV;					// Set this flag so the two private display methods
											// know whether or not to update the View

		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position
		fpRecognizerInput = input;			// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state		
		floatingPointNumberSize = 0;		// Initialize the UserName size

		// Display the input to the console
		System.out.println("\nThe input to be checked\n" + input);
		
		// Display a title to the console to support understanding the FSM trace output 
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state.  The
		// flag running controls that.
		while (running) {
			
			// The switch statement takes the execution of the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 must find a numeric digit and transition to state 1 or a decimal point
				// and transition to state 3.  If the character is not one of those, there is an
				// error and the FSM halts, as state 0 is not a final state.
				
				// 0-9 -> State 1
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 1;
					floatingPointNumberSize++;
				}
				// . -> State 3
				else if (currentChar == '.' ) {
					nextState = 3;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = false;			// State 0 is not a final state
			
				// The execution of this state is finished
				break;
			
			case 1:
				// State 1 recognizes 3 kinds of characters.  The first is a numeric digit and
				// the FSM stays in State 1.  The second is a decimal point and the FSM transitions
				// to state 2.  The third is either an upper or lower case "e" and the FSM
				// transitions to state 5.  If there is a next character and it is not one of 
				// those kinds of digits are found, there is an error and the FSM halts.  If there 
				// are no additional input characters, the FSM halts and accepts the input as a 
				// floating point number, as state 1 is a final state.
				
				// 0-9 -> State 1
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 1;
					floatingPointNumberSize++;
				}
				// . -> State 2
				else if (currentChar == '.' ) {
					nextState = 2;
					floatingPointNumberSize++;
				}
				// E or e -> State 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = true;			// State 1 is a final state
			
				// The execution of this state is finished
				break;
			
			case 2:
				// State 2 recognizes 2 kinds of characters.  The first is a numeric digit and
				// the FSM stays in State 2.  The second is either an upper or lower case "e" and 
				// the FSM transitions to state 5.  If there is a next character and it is not one 
				// of those, there is an error and the FSM halts.  If there are no additional input
				// characters, the FSM halts and accepts the input as a floating point number, as 
				// state 2 is a final state.
				
				// 0-9 -> State 2
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 2;
					floatingPointNumberSize++;
				}
				// E or e -> State 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = true;			// State 2 is a final state
			
				// The execution of this state is finished
				break;
			
			case 3:
				// State 3 recognize only 1 kind of character, a numeric digit and the FSM 
				// transitions to state 4 when one is found.  If there is a next character and it
				// is not numeric, there is an error and the FSM halts.  If there are no additional
				// input, the FSM halts with an error, as State 3 is not a final state.
				
				// 0-9 -> State 4
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 4;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = false;			// State 3 is not a final state
			
				// The execution of this state is finished
				break;
			
			case 4:
				// State 4 recognizes 2 kinds of characters.  The first is a numeric digit and
				// the FSM stays in State 4.  The second is either an upper or lower case "e" and 
				// the FSM transitions to state 5.  If there is a next character and it is not one 
				// of those, there is an error and the FSM halts.  If there are no additional input
				// characters, the FSM halts and accepts the input as a floating point number, as 
				// state 4 is a final state.
				
				// 0-9 -> State 4
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 4;
					floatingPointNumberSize++;
				}
				// E or e -> State 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = true;			// State 4 is a final state
			
				// The execution of this state is finished
				break;
		
			case 5:
				// State 5 recognizes 2 kinds of characters.  The first is a numeric digit and
				// the FSM transitions to State 7.  The second is either a "+" or a "-" character,
				// and the FSM transitions to state 6.  If there is a next character and it is not
				// one of those, there is an error and the FSM halts.  If there are no additional
				// input characters, the FSM halts with an error, as State 5 is not a final state.
				
				// 0-9 -> State 7
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 7;
					floatingPointNumberSize++;
				}
				// + or - -> State 5
				else if (currentChar == '+' || currentChar == '-') {
					nextState = 6;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = false;			// State 5 is not a final state
			
				// The execution of this state is finished
				break;
			
			case 6:
				// State 6 recognize only 1 kind of character, a numeric digit and the FSM 
				// transitions to state 7 when one is found.  If there is a next character and it
				// is not numeric, there is an error and the FSM halts.  If there are no additional
				// input, the FSM halts with an error, as State 6 is not a final state.
				
				// 0-9 -> State 7
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 7;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = false;			// State 6 is not a final state
			
				// The execution of this state is finished
				break;
			
			case 7:
				// State 7 recognize only 1 kind of character, a numeric digit and the FSM 
				// stays in state 7 when one is found.  If there is a next character and it
				// is not numeric, there is an error and the FSM halts.  If there are no additional
				// input, the FSM halts and accepts the input as a floating point number, as state
				// 7 is a final state.
				
				// 0-9 -> State 7
				if (currentChar >= '0' && currentChar <= '9' ) {
					nextState = 7;
					floatingPointNumberSize++;
				}
				// Halt the FSM
				else
					running = false;
				
				finalState = true;			// State 7 is a final state
			
				// The execution of this state is finished
				break;
			
			}
			
			// The processing of a state has taken place.  If the FSM is still running, we move to
			// the next character in the input, move to the next state, and signal that we have not
			// yet identified a new next state.
			if (running) {
				displayConsoleInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		
		// The FSM has halted
		displayConsoleInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if there is an error or not.  That depends on the
		// current state of the FSM and whether or not the whole string has been consumed.  The
		// following switch directs the execution to separate code for each of the FSM states where
		// execution ended and that makes it possible for this code to display a very specific error
		// message to improve the user experience.
		floatingPointRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
		fprErrorMessage = "*** ERROR: ";						// Preload the common text
		
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			fprErrorMessage += "This must be a numeric digit or a decimal point.\n";
			displayErrorMessage(input, fprErrorMessage);
			return fprErrorMessage;

		case 1:
			// State 1 is a final state.
			// We must ensure the whole string has been consumed for it to be recognized.

			if (currentCharNdx < input.length()) {
				
				// There are characters remaining in the input, so the input is not valid
				fprErrorMessage += 
					"This must be a numeric digit, a decimal point, or \"E\"/\"e\".\n";
				displayErrorMessage(input, fprErrorMessage);
				return fprErrorMessage;
			}
			else {
				// The input is a valid floating point number
				displayValidMessage();
				return "";				// An empty string means no error
			}

		case 2:
			// State 2 is a final state.
			// We must ensure the whole string has been consumed.

			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				fprErrorMessage += 
					"This must be a numeric digit, or \"E\"/\"e\".\n";
				displayErrorMessage(input, fprErrorMessage);
				return fprErrorMessage;
			}
			else {
				displayValidMessage();
				return "";				// An empty string means no error
			}
			
		case 3:
			// State 3 is not a final state.

			// A required character was not found
			fprErrorMessage += "This must be a numeric digit.\n";
			displayErrorMessage(input, fprErrorMessage);
			return fprErrorMessage;

		case 4:
			// State 4 is a final state.
			// We must ensure the whole string has been consumed.

			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				fprErrorMessage += 
					"This must be a numeric digit, or \"E\"/\"e\".\n";
				displayErrorMessage(input, fprErrorMessage);
				return fprErrorMessage;
			}
			else {
				// The input is a valid floating point number
				displayValidMessage();
				return "";				// An empty string means no error
			}
			
		case 5:
			// State 5 is not a final state.

			// A required character was not found
			fprErrorMessage += 
				"This must be a numeric digit or \"+\"/\"-\".\n";
			displayErrorMessage(input, fprErrorMessage);
			return fprErrorMessage;
			
		case 6:
			// State 6 is not a final state.

			// A required character was not found
			fprErrorMessage += 
				"This must be a numeric digit.\n";
			displayErrorMessage(input, fprErrorMessage);
			return fprErrorMessage;

		case 7:
			// State 7 is a final state.
			// We must ensure the whole string has been consumed.

			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				fprErrorMessage += "This must be a numeric digit.\n";
				displayErrorMessage(input, fprErrorMessage);
				return fprErrorMessage;
			}
			else {
				// The input is a valid floating point number
				displayValidMessage();
				return "";				// An empty string means no error
			}

		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should never happen.
			return "Finite State Machine System Error";
		}
	}

	// Private method to display an error message to eliminate repeated code
	private static void displayErrorMessage(String input, String errMessage) {
		System.out.println("\n" + input);		
		String str = input.substring(0, currentCharNdx);
		System.out.println(str + "\u21EB");
		if (updateView == false) return;	// Do not update the View when using testing Automation
		View.inputErrorPart1.setText(str);
		View.inputErrorPart2.setText("\u21EB");	// Special up arrow
		View.inputErrorPart3.setText(errMessage);
		View.validNumber.setText("");
		View.button_Finish.setDisable(true);
	}
	
	// Private method to display a success message to eliminate repeated code
	private static void displayValidMessage() {
		String success = "The input is a valid floating point number!";
		System.out.println("\n*** SUCCESS: " + success);		
		floatingPointRecognizerIndexofError = -1;
		if (updateView == false) return;	// Do not update the View when using testing Automation
		View.inputErrorPart1.setText("");
		View.inputErrorPart2.setText("");
		View.inputErrorPart3.setText("");
		View.validNumber.setText(success);
		View.button_Finish.setDisable(false);
	}


	// Private method to display debugging data
	private static void displayConsoleInfo() {
		// Display the current state of the FSM as part of the console execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
				nextState + "     " + floatingPointNumberSize);
	}
	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;	// Move to the next character in input
		
		// See if there is a next character
		if (currentCharNdx < inputLine.length())
			
			// There is a next character, so return it
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			
			// There isn't a next character so clear currentChar and stop the FSM
			currentChar = ' ';
			running = false;
		}
	}
}
