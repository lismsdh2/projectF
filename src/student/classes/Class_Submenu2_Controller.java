package student.classes;

import java.net.URL;
import java.util.ResourceBundle;

import DTO.AssignmentDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import main.Main_Master_Controller;
import util.Navigator;

public class Class_Submenu2_Controller implements Initializable{
	
	@FXML private Button btn1_1;
	@FXML private Button btn1_2;
	@FXML private ComboBox<AssignmentDto> combo;

	public static Main_Master_Controller mmc;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		//현재강의 버튼
		btn1_1.setOnAction(e ->{
			
			Navigator.loadPages(Navigator.STUDENT_CURRENT_CLASS_LIST);
		});
		//지난강의 버튼
		btn1_2.setOnAction(e -> {
			
			Navigator.loadPages(Navigator.STUDENT_PAST_CLASS_LIST);
		});
		//수강신청 버튼
//		btn1_3.setOnAction(e -> {
//			
//			Navigator.loadPages(Navigator.STUDENT_FULL_CLASS_LIST);
//		});
	}
}