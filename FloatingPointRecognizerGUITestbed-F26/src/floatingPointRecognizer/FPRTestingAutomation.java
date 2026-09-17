package floatingPointRecognizer;

import fprWindow.Model;

public class FPRTestingAutomation {

/*******
 * <p> Title: FloatingPointRecognizerTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	2022-02-25 A set of semi-automated test cases
 * @version 2.00	2024-09-22 Updated for use at ASU
 * @version 3.00	2024-08-01 Updated for Fall 2025
 * 
 */	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the test cases **************/
		
		// This is a properly written positive test
		performTestCase(1, "3.675E+5", true);
		
		// This is a properly written negative test
		performTestCase(2, "15B12", false);
		
		// This is an improperly written negative test, because the floating point number is valid,
		// but the second parameter asserts that it is not valid
		performTestCase(3, "12", false);
		
		// These are improperly written positive test, because the floating point number 
		// is not valid, but the second parameter asserts that it is valid
		performTestCase(4, "+5", true);
		performTestCase(5, "198", true);
		// Add more test cases here
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/*
	 * This private method sets up the input value for the test from the input parameters, displays
	 * the test execution information, invokes precisely the same recognizer that the interactive
	 * JavaFX GUI mainline uses, interprets the returned value, displays the interpreted result to
	 * the console.
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
				
		// Display an individual test case header
		System.out.println(
				"____________________________________________________________________________" +
				"\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		// Call the recognizer to process the input
		String resultText= Model.checkForValidFoatingPointNumber(inputText, false);
						
		// Interpret the result and display that interpreted information
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println(
						"***Failure*** The floating point number <" + inputText + "> is invalid." +
						"\nBut it was supposed to be valid, so this is a failure!\n");
				displayError(inputText, resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The floating point number <" + inputText + 
						"> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				displayError(inputText, resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The floating point number <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The floating point number <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
	}
	
	/*
	 * This private method displays the input on one line and then shows an up arrow in the next
	 * line that point at the character that caused the finite state machine to halt and issue an
	 * error message.  The provided error information is very specific as we know where the FSM
	 * was when it failed to find what it was can accept.
	 */
	private static void displayError(String theInput, String theErrorMessage) {
		System.out.println(theInput);
		String blanks = "";
		int numBlanks = fprWindow.Model.getIndexofError();
		for (int ndx = 0; ndx < numBlanks; ndx++) blanks += " ";
		System.out.println(blanks + "\u21EB");
		System.out.println(theErrorMessage);
	}
}

