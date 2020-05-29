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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import student.classes.popup.EnrollmentPopupController;

public class EnrollmentController implements Initializable{

	@FXML private Button currentClass;
	@FXML private Button lastClass;
	@FXML private Button allClass;
	@FXML private TableView<EnrollmentDto> tableView;
	@FXML private TableColumn<EnrollmentDto, Integer> colClassNo;
	@FXML private TableColumn<EnrollmentDto, String> colClassName;
	@FXML private TableColumn<EnrollmentDto, String> colClassDate;
	@FXML private TableColumn<EnrollmentDto, String> colClassTeacher;
	@FXML private TableColumn<EnrollmentDto, Integer> colClassPeople;
	@FXML private TableColumn<EnrollmentDto, Void> colClassEnroll;
	@FXML private TableColumn<EnrollmentDto, Void> colClassButton;
	@FXML private ComboBox<String> choiceField;
	@FXML private TextField txtSearch;
	@FXML private Button btnSearch;
	
	private ObservableList<EnrollmentDto> list;
//	private Stage enrollmentStage;				//현재로서는 필요 없는 부분이지만, 전체 연동 시 필요 함
	private EnrollmentDao eDao = new EnrollmentDao();
	private EnrollmentPopupController popupController = new EnrollmentPopupController(this);

	//생성자
	public EnrollmentController() {

		//현재로서는 필요 없는 부분이지만, 전체 연동 시 필요 함
//		//수강신청 화면 설정
//		enrollmentStage = new Stage();
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/student/enrollment/enrollment.fxml"));
//			loader.setController(this);
//			enrollmentStage.setScene(new Scene(loader.load()));
//			enrollmentStage.setTitle("수강 신청");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	//현재로서는 필요 없는 부분이지만, 전체 연동 시 필요 함
//	//수강신청 화면 호출
//	public void showStage() {
//		
//		enrollmentStage.showAndWait();
//	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		enrollTableView();												//DB내용 출력
		tableView.setOnMouseClicked(e -> handleDoubleClicked(e));		//더블클릭
		btnSearch.setOnAction(e-> handleBtnSearch());
	}
	
	//table에 내용 출력하기
	private void enrollTableView() {

		list = eDao.enrollment_selectAll();
		colClassNo.setCellValueFactory(new PropertyValueFactory<>("classno"));
		colClassName.setCellValueFactory(new PropertyValueFactory<>("classname"));
		colClassDate.setCellValueFactory(new PropertyValueFactory<>("str"));
		colClassTeacher.setCellValueFactory(new PropertyValueFactory<>("teachername"));
		colClassPeople.setCellValueFactory(new PropertyValueFactory<>("limitstudent"));
		addButton();							//버튼 추가
		tableView.setItems(list);
	}
	
	//상세정보 버튼 만들기
	private void addButton() {
		
//		colClassEnroll = new TableColumn<>("상세정보");
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
//		tableView.getColumns().add(colClassEnroll);
	}

	//버튼 클릭시 수강신청 상세화면 띄우기
	private void openPopupWindow() {
		
		popupController.showStage();
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
		
		System.out.println(choiceField.getValue());
		//Dao 추가하기
	}
}

