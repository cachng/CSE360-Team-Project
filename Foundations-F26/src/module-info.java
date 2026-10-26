/**
 * The FoundationsF26 Module for the CSE360 Team-Project
 * 
 * This module provides the core application, including the JavaFX-based
 * user interface and database connectivity used by the application.
 * 
 * @since 1.0
 */

module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
