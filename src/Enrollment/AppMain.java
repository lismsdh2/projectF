package Enrollment;
/*
 * 작성자 : 도현호
 * 최종 작성일 : 2020-05-15
 * (결합시 제거 될 페이지)
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends Application{
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println("start메서드 호출");
		Parent root = FXMLLoader.load(getClass().getResource("../fxml/student/enrollment/enrollment.fxml"));
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("AppMain");
		primaryStage.show();
	}

	public static void main(String[] args) {
		
		launch(args);
	}

}

