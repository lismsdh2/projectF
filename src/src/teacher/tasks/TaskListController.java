﻿package teacher.tasks;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import DAO.ClassDao;
import DAO.TaskDao;
import DTO.BasicDto;
import DTO.ClassDto;
import DTO.TaskDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launch.AppMain;
import main.Main_Master_Controller;
import util.Navigator;

/**
 * @author 김지현
 */
public class TaskListController extends Main_Master_Controller implements Initializable {
	@FXML
	private StackPane stackPane;
	@FXML
	protected AnchorPane reportListPane;
	@FXML
	private TableView<TaskDto> tblViewReport;
	@FXML
	private TableColumn<TaskDto, ?> tcExpireDate;
	@FXML
	private TableColumn<TaskDto, ?> tcNo;
	@FXML
	private TableColumn<TaskDto, ?> perfectScore;
	@FXML
	private TableColumn<TaskDto, ?> tcRegDate;
	@FXML
	private TableColumn<TaskDto, ?> tcTitle;
	@FXML
	private TableColumn<TaskDto, ?> tcFile;
	@FXML
	private TableColumn<TaskDto, Boolean> modify;
	@FXML
	private Button btnReportCreate;
	@FXML
	private ComboBox<String> combo;

	BasicDto user = AppMain.app.getBasic();
	String userid = user.getId();
	int classno = AppMain.app.getBasic().getClass_no();
	ClassDao cDao = new ClassDao();
	ClassDto currentClass = cDao.selectClassOne(classno);
	TaskDao tDao = new TaskDao();

	ObservableList<TaskDto> taskList = FXCollections.observableArrayList();
	ObservableList<String> classNameList = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 콤보박스에 강의목록 표시
		setCombo();

		// 테이블에 과제 표시
		setTableColumns();

		// 과제 생성 버튼 -> 과제생성 창
		btnReportCreate.setOnAction(e -> handlebtnCreate(e));

		// 과제 더블클릭 -> 상세 페이지
		tblViewReport.setOnMouseClicked(e -> handleClicked(e));

	}

	// 과제 생성 버튼
	private void handlebtnCreate(ActionEvent event) {

		try {
			// 새로운 창
			Stage stage = new Stage(StageStyle.UTILITY);
			stage.initOwner(reportListPane.getScene().getWindow());
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("과제 생성");

			// fxml 및 컨트롤러 연결
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/teacher/tasks/TaskCreate.fxml"));
			loader.setController(new TaskCreateController(currentClass));
			Parent parent = loader.load();
			stage.setScene(new Scene(parent));
			stage.setResizable(false);
			stage.show();

			// Stage 내의 버튼이 클릭되면 tableView refresh
			Button btn = (Button) parent.lookup("#btnSubmit");
			btn.setOnMouseClicked(e -> refreshTable());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 현재 유저의 강의목록
	private void setCombo() {
		classNameList = tDao.selectUserClassList(userid);
		combo.setItems(classNameList);

		// 강의가 선택된채로 넘어오면 콤보박스에 강의명을 보여준다.
		if (classno != 0) {
			String className = currentClass.getClassName();
			String classInfo = "[" + classno + "] " + className;
			combo.getSelectionModel().select(classInfo);
		}

		// 콤보박스에서 강의명 선택 시 그 강의의 과제만 보여주기
		combo.setOnAction(e -> {
			String selectedCombo = combo.getSelectionModel().getSelectedItem();
			int selectedNo = Integer.valueOf(selectedCombo.substring(1, 5));
			// 선택된강의로 데이터변경
			AppMain.app.getBasic().setClass_no(selectedNo);
			classno = selectedNo;
			currentClass = cDao.selectClassOne(classno);
			refreshTable();
		});
	}

	// 과제 더블클릭 시 상세 페이지로 이동
	private void handleClicked(MouseEvent e) {

		// 더블클릭 시
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			if (e.getClickCount() == 2) {
				System.out.println("과제 더블클릭");

				// 과제 제출 현황 페이지로 이동
				TaskDto selected = tblViewReport.getSelectionModel().getSelectedItem();
				if (selected != null) { // 상단제목바 누르면 null
					System.out.println(selected.toString());
					int selectedNo = selected.getTcNo();
					Navigator.loadPages("../fxml/teacher/tasks/TaskListDetail.fxml");
				}
			}
		}
	}

	// DB에서 읽어온 데이터를 테이블에 표시
	private void setTableColumns() {
		// 컬럼 설정
		tcNo.setCellValueFactory(new PropertyValueFactory<>("tcNo"));
		tcTitle.setCellValueFactory(new PropertyValueFactory<>("tcTitle"));
		tcRegDate.setCellValueFactory(new PropertyValueFactory<>("tcRegdate"));
		tcExpireDate.setCellValueFactory(new PropertyValueFactory<>("tcExpireDate"));
		tcFile.setCellValueFactory(new PropertyValueFactory<>("tcFile"));
		perfectScore.setCellValueFactory(new PropertyValueFactory<>("perfectScore"));
		modify.setCellValueFactory(new PropertyValueFactory<>("dateCheck"));

		modify.setCellFactory(item -> new TableCell<TaskDto, Boolean>() {

			private final Button editButton = new Button("수정");
			private final Button deleteButton = new Button("삭제");

			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null);
				} else {
					// 버튼2개를 넣기 위해 HBox 생성 후 버튼들 생성
					HBox hBox = new HBox(editButton, deleteButton);
					editButton.setPrefWidth(50);
					deleteButton.setPrefWidth(50);
					hBox.setSpacing(5);
					hBox.setAlignment(Pos.CENTER);

					// 기본값은 버튼 활성화
					editButton.setDisable(item);
					deleteButton.setDisable(item);

					// 선택된 과제의 마감 날짜와 현재날짜를 비교하여 마감날짜가 지나면 버튼 비활성화
					TaskDto selectedRowTask = getTableView().getItems().get(getIndex());
					LocalDate c = LocalDate.now();
					LocalDate e = selectedRowTask.getTcExpireDate();
					if (e != null) {
						long diff = ChronoUnit.DAYS.between(c, e);
						if (diff < 0) {
							editButton.setDisable(!item);
							deleteButton.setDisable(!item);
						}
					}

					// 버튼넣기
					setGraphic(hBox);

					// 수정 버튼 클릭 시 과제 수정창
					editButton.setOnAction(event -> {
						handleEditBtn(selectedRowTask);
					});

					// 삭제 버튼 클릭 시 과제 삭제창
					deleteButton.setOnAction(event -> {
						handleDelBtn(selectedRowTask, event);
					});
				}
			}

			// 과제 수정 버튼
			private void handleEditBtn(TaskDto selectedRowTask) {
				int selectedReportNo = selectedRowTask.getTcNo();
				System.out.println("** 수정버튼 클릭 on " + selectedReportNo);

				try {
					// 새로운 스테이지로 수정창
					Stage stage = new Stage(StageStyle.UTILITY);
					stage.initOwner(reportListPane.getScene().getWindow());
					stage.initModality(Modality.WINDOW_MODAL);
					stage.setTitle("과제 수정");

					// fmxl, controller
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("../../fxml/teacher/tasks/TaskModify.fxml"));
					loader.setController(new TaskModifyController(selectedReportNo));
					Parent parent = loader.load();
					stage.setScene(new Scene(parent));
					stage.setResizable(false);
					stage.show();

					// 수정창 내에서 수정 버튼 클릭 시
					Button button = (Button) parent.lookup("#btnSubmit");
					button.setOnMouseClicked(mouseEvent -> {

						// tableView refresh
						refreshTable();

						// alert
						Alert alert = new Alert(AlertType.INFORMATION, "수정이 완료되었습니다.", ButtonType.CLOSE);
						alert.setTitle("수정 완료");

						// 닫기버튼 누르면 과제수정 창 닫힘
						Optional<ButtonType> btn = alert.showAndWait();
						if (btn.get().equals(ButtonType.CLOSE)) {
							stage.close();
						}

					});

				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}

			// 과제 삭제 버튼
			private void handleDelBtn(TaskDto selectedRowTask, ActionEvent event) {
				int selectedReportNo = selectedRowTask.getTcNo();
				System.out.println("** 삭제버튼 클릭 on " + selectedReportNo);

				// 진짜 삭제할건지 확인하는 확인창
				event.consume();

				Alert deleteConfiguration = new Alert(Alert.AlertType.CONFIRMATION);
				deleteConfiguration.setTitle("삭제 확인");
				deleteConfiguration.setHeaderText("정말로 과제를 삭제하시겠습니까?");
				deleteConfiguration.setContentText(selectedRowTask.getTcNo() + "\n" + selectedRowTask.getTcTitle());
				Optional<ButtonType> result = deleteConfiguration.showAndWait();

				// OK누르면 삭제
				if (result.get().equals(ButtonType.OK)) {
					tDao.deleteTask(selectedReportNo);
					refreshTable();
				}
			}
		});

		// 테이블 채우기
		refreshTable();
	}

	// db에서 저장된 데이터 불러와서 테이블에 넣기
	public void refreshTable() {
		System.out.println("cNo" + classno);

		if (classno == 0) { // 선택된 강의 없음
			System.out.println("classno not exists");
			taskList = tDao.selectUserTaskList(userid);
			btnReportCreate.setVisible(false);
		} else { // 선택된 강의 있음
			System.out.println("classno exists");
			taskList = tDao.selectUserClassTaskList(userid, classno);
			btnReportCreate.setVisible(true);
		}
		tblViewReport.setItems(taskList);
	}
}
