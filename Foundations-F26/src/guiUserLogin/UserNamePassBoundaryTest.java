package guiUserLogin;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

class UserNamePassBoundaryTest {

	@Test
	void test(Stage ts) {
		ControllerUserLogin.doLogin(ts);
		
	}

}
