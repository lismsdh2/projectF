package main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import launch.AppMain;
import teacher.classes.Submenu_Controller;
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
//		Navigator.loadPages("/teacher/classes/teacher_main.fxml");

		// 버튼 누르면 inner fxml 바꾸기
		btn1.setOnAction(e -> {
			Submenu_Controller.btnNo=0;
			Navigator.loadSubMenu(Navigator.TEACHER_CLASS_MENU);
			Navigator.loadPages(Navigator.TEACHER_CLASS_LIST);
		});
		//btn2.setOnAction(e -> Navigator.loadPages(Navigator.TASK_LIST));
		// btn2: 과제
		
		btn2.setOnAction(e -> {
			AppMain.app.getBasic().setClass_no(0);
			Navigator.loadSubMenu(Navigator.TEACHER_TASK_MENU);
			Navigator.loadPages(Navigator.TEACHER_TASK_LIST);
		});
	}
}