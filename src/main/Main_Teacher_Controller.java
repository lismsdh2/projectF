package main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import launch.AppMain;
import util.Navigator;

/**
 * @author 김지현
 */
public class Main_Teacher_Controller extends Main_Master_Controller implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		// 초기화면
		Navigator.loadSubMenu(Navigator.TEACHER_CLASS_MENU);
		Navigator.loadPages(Navigator.TEACHER_CLASS_LIST);

		// 버튼 누르면 inner fxml 바꾸기
		//btn1.setOnAction(e -> Navigator.loadPages(Navigator.TEACHER_CLASS_LIST));
		btn1.setOnAction(e -> {
			Navigator.loadSubMenu(Navigator.TEACHER_CLASS_MENU);
			Navigator.loadPages(Navigator.TEACHER_CLASS_LIST);
		});
		
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				AppMain.app.setClassno(0);
				Navigator.loadPages(Navigator.TEACHER_TASK_LIST);
			}
		});

	}

}
