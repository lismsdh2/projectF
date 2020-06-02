package main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import util.Navigator;

/*
 * 작성자 : 도현호
 */
public class Main_Student_Controller extends Main_Master_Controller implements Initializable{
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		// 초기화면
		Navigator.loadSubMenu(Navigator.STUDENT_CLASS_MENU);
		Navigator.loadPages(Navigator.STUDENT_CURRENT_CLASS_LIST);

		// 버튼 누르면 inner fxml 바꾸기
		//강의버튼
		btn1.setOnAction(e -> {
			Navigator.loadSubMenu(Navigator.STUDENT_CLASS_MENU);
			Navigator.loadPages(Navigator.STUDENT_CURRENT_CLASS_LIST);
		});
		
		
		//과제버튼
		btn2.setOnAction(e->{
			Navigator.loadSubMenu(Navigator.STUDENT_TASK_MENU);
			Navigator.loadPages(Navigator.STUDENT_CLASSES_TASK_LIST);
		});
	}
}