package main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import launch.AppMain;
import util.Navigator;

/*
 * 작성자 : 도현호
 */
public class Main_Student_Controller extends Main_Master_Controller implements Initializable{
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		// 초기화면
		Navigator.loadPages(Navigator.STUDENT_CLASS_LIST);

		// 버튼 누르면 inner fxml 바꾸기
		btn1.setOnAction(e -> Navigator.loadPages(Navigator.STUDENT_CLASS_LIST));
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				AppMain.app.setClassno(0);
				Navigator.loadPages(Navigator.STUDENT_TASK_LIST);
			}
		});
	}
}