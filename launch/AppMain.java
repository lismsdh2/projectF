package launch;
/*
 * 작성자 : 
 */
import java.io.IOException;

import DTO.BasicDto;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application {
	
	public static AppMain app;
	BasicDto user;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;
		showLogin(primaryStage);
	}

	public void showLogin(Stage primaryStage) throws IOException {

//		Parent root = FXMLLoader.load(getClass().getResource("../fxml/login/login.fxml"));
		Parent root = null;
		try {
			root = FXMLLoader.load(Class.forName("launch.AppMain").getResource("/fxml/login/login.fxml"));
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} 
		Scene scene = new Scene(root);
		primaryStage.setTitle("LOGIN");

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	//BasicDTO Getter/Setter
	public void setBasic(BasicDto user) {
		this.user = user;
	}

	public BasicDto getBasic() {
		return user;
	}
}