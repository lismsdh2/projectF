package student.classes;
/*
 * 작성자 : 도현호
 */
import java.net.URL;
import java.util.ResourceBundle;

import DAO.EnrollmentDao;
import DTO.EnrollmentDto;
import DTO.UserDto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import launch.AppMain;

public class Past_Class_Controller implements Initializable{
	
	@FXML private TableView<EnrollmentDto> tableView;
	@FXML private TableColumn<EnrollmentDto, Integer> colClassNo;
	@FXML private TableColumn<EnrollmentDto, String> colClassName;
	@FXML private TableColumn<EnrollmentDto, String> colClassDate;
	@FXML private TableColumn<EnrollmentDto, String> colClassTeacher;
	@FXML private TableColumn<EnrollmentDto, Integer> colClassPeople;
	@FXML private TableColumn<EnrollmentDto, String> colClassStatus;	
    @FXML private ComboBox<String> choiceField;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
   
    private UserDto uDto = AppMain.app.getUser();
    private ObservableList<EnrollmentDto> list;
    private EnrollmentDao eDao = new EnrollmentDao();
    private String stu_id = uDto.getId();
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		pastTableView();								//리스트 출력
		btnSearch.setOnAction(e-> handleBtnSearch());	//검색단추 클릭
		//검색 시 엔터 효과
		txtSearch.setOnKeyPressed(e ->{ 
			if(e.getCode() == KeyCode.ENTER) {
				
				handleBtnSearch();
			}
		});
	}
	
	//table에 내용 출력하기
	private void pastTableView() {

		list = eDao.past_selectAll(stu_id);
		colClassNo.setCellValueFactory(new PropertyValueFactory<>("classno"));
		colClassName.setCellValueFactory(new PropertyValueFactory<>("classname"));
		colClassDate.setCellValueFactory(new PropertyValueFactory<>("str"));
		colClassTeacher.setCellValueFactory(new PropertyValueFactory<>("teachername"));
		colClassPeople.setCellValueFactory(new PropertyValueFactory<>("limitstudent"));
		colClassStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tableView.setItems(list);
	}

	//검색단추
	private void handleBtnSearch() {
	
		String search_word = txtSearch.getText();
		System.out.println(search_word);
		list = eDao.search_past_selectAll(stu_id, search_word);
		tableView.setItems(list);
	}
}