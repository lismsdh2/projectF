package student.tasks;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.EnrollmentDao;
import DTO.AssignmentDto;
import DTO.EnrollmentDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import launch.AppMain;
import main.Main_Master_Controller;
import util.Navigator;

public class Submenu2_Controller implements Initializable{
	
	@FXML private Button btn2_1;
	@FXML private Button btn2_2;
	@FXML private ComboBox<String> combo;

	private EnrollmentDao eDao = new EnrollmentDao();
	private ObservableList<String> enrollNameList = FXCollections.observableArrayList();
	public static Main_Master_Controller mmc;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		//현재과제 버튼
		btn2_1.setOnAction(e ->{
			
			Navigator.loadPages(Navigator.STUDENT_CURRENT_TASK_LIST);
		});
		//지난과제 버튼
		btn2_2.setOnAction(e -> {
			
			Navigator.loadPages(Navigator.STUDENT_PAST_TASK_LIST);
		});
		//강의별 과제 콤보박스
		setCombo();
		combo.setOnAction(e -> {
			
			Navigator.loadPages(Navigator.STUDENT_PAST_TASK_LIST);
		});
	}
	

	//콤보박스 ITEM 설정
	private void setCombo() {
		
		enrollNameList = eDao.current_className_selectAll(AppMain.app.getUser().getId());
		combo.setItems(enrollNameList);
	}
}