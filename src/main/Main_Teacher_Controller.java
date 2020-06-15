package main;

import java.net.URL;
import java.util.ResourceBundle;

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

		// 초기화면(강의 화면, 강의서브메뉴)
		Navigator.loadSubMenu(Navigator.TEACHER_CLASS_MENU);
		Navigator.loadPages(Navigator.TEACHER_CLASS_LIST);

		// 버튼 누르면 inner fxml, subMenu Change

		// btn1 : 강의
		btn1.setOnAction(e -> {
			Navigator.loadSubMenu(Navigator.TEACHER_CLASS_MENU);
			Navigator.loadPages(Navigator.TEACHER_CLASS_LIST);
		});

		// btn2: 과제
		btn2.setOnAction(e -> {
			AppMain.app.getBasic().setClass_no(0);
			Navigator.loadSubMenu(Navigator.TEACHER_TASK_MENU);
			Navigator.loadPages(Navigator.TEACHER_TASK_LIST);
		});
	}
}