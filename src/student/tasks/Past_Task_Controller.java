package student.tasks;
/*
 * 작성자 : 도현호
 */
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import DAO.AssignmentDao;
import DTO.AssignmentDto;
import DTO.UserDto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import launch.AppMain;

public class Past_Task_Controller implements Initializable{

	@FXML private AnchorPane assignPane;
	@FXML private TableView<AssignmentDto> tableView;
	@FXML private TableColumn<AssignmentDto, Integer> colTaskNo;
	@FXML private TableColumn<AssignmentDto, String> colTaskName;
    @FXML private TableColumn<AssignmentDto, String> colTaskSubmit;
    @FXML private TableColumn<AssignmentDto, Date> colTaskSubmissionDate;
    @FXML private TableColumn<AssignmentDto, Date> colTaskDeadlineDate;
    @FXML private TableColumn<AssignmentDto, Integer> colTaskScore;	
    @FXML private TableColumn<AssignmentDto, Void> colTaskAssign;

    private ObservableList<AssignmentDto> list;
	private Stage assignmentStage;				//현재로서는 필요 없는 부분이지만, 전체 연동 시 필요 함
    private AssignmentDto assign = new AssignmentDto();
	private AssignmentDao aDao = new AssignmentDao();
	private UserDto uDto = AppMain.app.getUser();
//	private AssignmentPopupController_no popupController_no = new AssignmentPopupController_no(this);
//	private AssignmentPopupController_yes popupController_yes = new AssignmentPopupController_yes(this); 
	private int class_no = 1001;						//강의번호 - 추후 연동 필요
//    private int task_no = 10001;						//과제번호 - 추후 연동 필요
	private String student_id = uDto.getId();				//학생ID - 추후 연동 필요
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		assignTableView();												//DB값 불러오기
		tableView.setOnMouseClicked(e -> handleDoubleClicked(e));		//더블클릭 event 처리
		
	}
	
	public void setAssignmentStage(Stage assignmentStage) {
		
		this.assignmentStage = assignmentStage;
	}
	
	//테이블 내용 출력
	private void assignTableView() {
		
		list = aDao.assignment_selectAll(this.class_no, this.student_id);
		colTaskNo.setCellValueFactory(new PropertyValueFactory<>("taskList_no"));
		colTaskName.setCellValueFactory(new PropertyValueFactory<>("task_name"));
		colTaskSubmit.setCellValueFactory(new PropertyValueFactory<>("submitornot"));
		colTaskSubmissionDate.setCellValueFactory(new PropertyValueFactory<>("reg_date"));		//수정필요
		colTaskDeadlineDate.setCellValueFactory(new PropertyValueFactory<>("expire_date"));
		colTaskScore.setCellValueFactory(new PropertyValueFactory<>("Score"));
		addButton();								//버튼생성
		tableView.setItems(list);
	}
	
	//상세정보 버튼 만들기
	private void addButton() {
		
//		colTaskAssign = new TableColumn<>("제출");					//필드를 fxml에서 생성할 때는 열면 안 됨
		Callback<TableColumn<AssignmentDto, Void>, TableCell<AssignmentDto, Void>> cellFactory
				= new Callback<TableColumn<AssignmentDto, Void>, TableCell<AssignmentDto, Void>>(){
	
			@Override
			public TableCell<AssignmentDto, Void> call(final TableColumn<AssignmentDto, Void> param){
				
				final TableCell<AssignmentDto, Void> cell = new TableCell<AssignmentDto, Void>(){
					
					private final Button btn = new Button("제출하기");
					
					{
						btn.setOnAction((event)->{
							
							assign = getTableView().getItems().get(getIndex());
							System.out.println("selectedData(버튼) : " + assign.getSubmitornot());
							
							//과제번호 생성
							int c_no = 1001;					//연동해야됨
							int t_no = assign.getTask_no();		//테이블에서 과제번호 연동 완료
							if(assign.getSubmitornot().equals("N")) {
								
								System.out.println("처음 제출");
								System.out.println("assign : " + t_no);
//								popupController_no.setClassno(c_no);
//								popupController_no.setTaskno(t_no);
								openPopupWindow_no();							//처음 제출시 상세화면창 열기
							} else if(assign.getSubmitornot().equals("Y")) {
								
								System.out.println("수정 제출");
								System.out.println("assign : " + t_no);
//								popupController_yes.setClassno(c_no);
//								popupController_yes.setTaskno(t_no);
								openPopupWindow_yes();							//재 제출시 상세화면창 열기
							}
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
		colTaskAssign.setCellFactory(cellFactory);
//		tableView.getColumns().add(colTaskAssign);				//필드를 fxml에서 생성할 때는 열면 안 됨
	}
	
	//버튼 클릭시 수강신청 상세화면 띄우기
	private void openPopupWindow_no() {
		
//		popupController_no.showStage();
		refreshTable();											//저장 후 리스트 새로 고침
	}
	
	//버튼 클릭시 수강신청 상세화면 띄우기
	private void openPopupWindow_yes() {
		
//		popupController_yes.showStage();
		refreshTable();											//저장 후 리스트 새로 고침
	}
	
	//더블클릭 핸들러
	private void handleDoubleClicked(MouseEvent event) {
		
		AssignmentDto assign = tableView.getSelectionModel().getSelectedItem();
		if(assign == null) { 
			//아무것도 없을 때 더블클릭 하면 아무것도 안하기
		} else {
		
			int c_no = 1001;					//연동해야됨
			int t_no = assign.getTask_no();		//연동해야됨
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				
				if (event.getClickCount() == 2) {
					
					System.out.println("selectedData(버튼) : " + assign.getSubmitornot());
					if(assign.getSubmitornot().equals("N")) {
						
						System.out.println("처음 제출");
						System.out.println("assign : " + t_no);
//						popupController_no.setClassno(c_no);
//						popupController_no.setTaskno(t_no);
						openPopupWindow_no();							//처음 제출시 상세화면창 열기
						refreshTable();									//저장 후 리스트 새로 고침
					} else if(assign.getSubmitornot().equals("Y")){
						
						System.out.println("수정 제출");
						System.out.println("assign : " + t_no);
//						popupController_yes.setClassno(c_no);
//						popupController_yes.setTaskno(t_no);
						openPopupWindow_yes();							//재 제출시 상세화면창 열기
						refreshTable();									//저장 후 리스트 새로 고침
						
					}
				}
			}
		}
	}
	

	//데이터 저장 후 리스트 새로고침
	public void refreshTable() {
		
		list = aDao.assignment_selectAll(this.class_no, this.student_id);
		tableView.setItems(list);
	}
	
}