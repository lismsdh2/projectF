package student.classes;
/*
 * 작성자 : 도현호
 * 최종 작성일 : 2020-05-15
 */
import java.net.URL;
import java.util.ResourceBundle;

import DAO.EnrollmentDao;
import DTO.EnrollmentDto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import student.classes.popup.EnrollmentPopupController;

public class Enrollment_Controller implements Initializable{

	@FXML private Button currentClass;
	@FXML private Button lastClass;
	@FXML private Button allClass;
	@FXML private TableView<EnrollmentDto> tableView;
	@FXML private TableColumn<EnrollmentDto, Integer> colClassNo;
	@FXML private TableColumn<EnrollmentDto, String> colClassName;
	@FXML private TableColumn<EnrollmentDto, String> colClassDate;
	@FXML private TableColumn<EnrollmentDto, String> colClassTeacher;
	@FXML private TableColumn<EnrollmentDto, String> colClassPeople;
	@FXML private TableColumn<EnrollmentDto, Void> colClassEnroll;
	@FXML private TableColumn<EnrollmentDto, Void> colClassButton;
	@FXML private ComboBox<String> choiceField;
	@FXML private TextField txtSearch;
	@FXML private Button btnSearch;
	
	private ObservableList<EnrollmentDto> list;
	private EnrollmentDao eDao = new EnrollmentDao();
	private EnrollmentPopupController popupController = new EnrollmentPopupController(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		enrollTableView();												//DB내용 출력
		tableView.setOnMouseClicked(e -> handleDoubleClicked(e));		//더블클릭
		btnSearch.setOnAction(e-> handleBtnSearch());					//검색단추 클릭
		//검색 시 엔터 효과
		txtSearch.setOnKeyPressed(e ->{ 
			if(e.getCode() == KeyCode.ENTER) {
				
				handleBtnSearch();
			}
		});
	}
	
	//table에 내용 출력하기
	private void enrollTableView() {

		list = eDao.enrollment_selectAll();
		colClassNo.setCellValueFactory(new PropertyValueFactory<>("classno"));
		colClassName.setCellValueFactory(new PropertyValueFactory<>("classname"));
		colClassDate.setCellValueFactory(new PropertyValueFactory<>("period"));
		colClassTeacher.setCellValueFactory(new PropertyValueFactory<>("teachername"));
		colClassPeople.setCellValueFactory(new PropertyValueFactory<>("str"));
		addButton();													//버튼 추가
		tableView.setItems(list);
	}
	
	
	//상세정보 버튼 만들기
	private void addButton() {
		
		Callback<TableColumn<EnrollmentDto, Void>, TableCell<EnrollmentDto, Void>> cellFactory
				= new Callback<TableColumn<EnrollmentDto, Void>, TableCell<EnrollmentDto, Void>>(){
	
			@Override
			public TableCell<EnrollmentDto, Void> call(final TableColumn<EnrollmentDto, Void> param){
				
				final TableCell<EnrollmentDto, Void> cell = new TableCell<EnrollmentDto, Void>(){
					
					private final Button btn = new Button("상세정보");
					
					{
						btn.setOnAction((event)->{
							
							EnrollmentDto enroll = getTableView().getItems().get(getIndex());
							System.out.println("selectedData(버튼) : " + enroll);
							//클래스번호 생성
							int c_no = enroll.getClassno();
							System.out.println("enroll : " + c_no);
							popupController.setClassno(c_no);
							openPopupWindow();
						});
					}
					
					@Override
					public void updateItem(Void item, boolean empty) {
						
						super.updateItem(item, empty);
						if(empty) {
							setGraphic(null);
						} else {
							setGraphic(btn);
						}
					}
				};
				return cell;
			}
		};
		colClassButton.setCellFactory(cellFactory);
	}

	//버튼 클릭시 수강신청 상세화면 띄우기
	private void openPopupWindow() {
		
		popupController.showStage();
		refreshTable();									//저장 후 리스트 새로 고침
	}
	
	//더블클릭 핸들러
	private void handleDoubleClicked(MouseEvent event) {

		EnrollmentDto enroll = tableView.getSelectionModel().getSelectedItem();
		if(enroll == null) {
			//빈화면일 때 더블클릭 시 아무것도 안하기
		} else {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				
				if (event.getClickCount() == 2) {
					
					System.out.println("selectedData(더블) : " + enroll.toString());
					//클래스번호 생성
					int c_no = enroll.getClassno();
					System.out.println("enroll : " + c_no);
					popupController.setClassno(c_no);
					openPopupWindow();
				}
			}
		}
	}

	//검색단추
	private void handleBtnSearch() {
	
		String search_word = txtSearch.getText();
		list = eDao.search_selectAll(search_word);
		tableView.setItems(list);
	}
	
	//데이터 저장 후 리스트 새로고침
	public void refreshTable() {
		
		list = eDao.enrollment_selectAll();
		tableView.setItems(list);
	}

}