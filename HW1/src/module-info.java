module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens hw2 to javafx.graphics, javafx.fxml;
	exports hw2;
}
