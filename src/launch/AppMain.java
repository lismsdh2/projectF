
package launch;
/*
 * 작성자 : 
 */
import java.io.IOException;

import DTO.UserDto;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {
	public static AppMain app;
	UserDto user;
	int classno;
	
	@Override

	public void start(Stage primaryStage) throws Exception {
		app = this;
		showLogin(primaryStage);
	}

	public void showLogin(Stage primaryStage) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("../fxml/login/login.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("LOGIN");

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public UserDto getUser() {
		return user;
	}

	public void setClassno(int classno) {
		this.classno = classno;
	}

	public int getClassno() {
		return classno;
	}
}
