﻿/* 작성자 : 이성원
 * 설명 : 선생님메인컨트롤러(강의리스트출력, 강의만들기, 강의수정및삭제, 강의검색)
 */

package teacher.classes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import DAO.ClassDao;
import DTO.ClassDto;
import DTO.UserDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launch.AppMain;
import main.Main_Master_Controller;
import util.Navigator;

public class TeacherMainController extends Main_Master_Controller implements Initializable {
	
	UserDto user = AppMain.app.getUser();
	//LoginUser user = Information.info.getUser();
	String userid = user.getId();
	String username = user.getName();

	@FXML
	private AnchorPane teacherMain;
	@FXML
	private TextField txtSearch;
	@FXML
	private TableView<ClassDto> classTableView;
	ObservableList<ClassDto> classList = FXCollections.observableArrayList();
	ClassDao cDao = new ClassDao();
	@FXML
	private TableColumn<ClassDto, ?> tcClassNo;
	@FXML
	private TableColumn<ClassDto, ?> tcClassName;
	@FXML
	private TableColumn<ClassDto, ?> tcTeacherName;
	@FXML
	private TableColumn<ClassDto, ?> tcStartDate;
	@FXML
	private TableColumn<ClassDto, ?> tcEndDate;
	@FXML
	private TableColumn<ClassDto, ?> tcLimitStudent;
	@FXML
	private TableColumn<ClassDto, Boolean> modify;
	@FXML 
	private TableColumn<ClassDto, Boolean> detail;

	Stage classListStage;

	public void setPrimaryStage(Stage classListStage) {
		this.classListStage = classListStage;
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		viewClassList();

		// 테이블뷰 클릭시 이벤트
		classTableView.setOnMouseClicked(e -> handleClicked(e));

	}

	// 테이블뷰 더블클릭시 이벤트
	private void handleClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			System.out.println("과제리스트로 이동");
			//loadPage("../tasks/TaskList.fxml");
			ClassDto selectedRowClass = classTableView.getSelectionModel().getSelectedItem();
			//AppMain.app.setClass1(selectedRowClass);
			if (selectedRowClass != null) {
				AppMain.app.setClassno(selectedRowClass.getClassNo());
				//Information.info.setClassNo(selectedRowClass.getClassNo());
				Navigator.loadPages("../fxml/tasks/TaskList.fxml");
			}
		}
	}

	// 강의리스트불러오기
	public void viewClassList() {

		tcClassNo.setCellValueFactory(new PropertyValueFactory<>("classNo"));
		tcClassName.setCellValueFactory(new PropertyValueFactory<>("className"));
		tcTeacherName.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
		tcStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		tcEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
		tcLimitStudent.setCellValueFactory(new PropertyValueFactory<>("limitStudent"));
		modify.setCellValueFactory(new PropertyValueFactory<>("dateCheck"));
		detail.setCellValueFactory(new PropertyValueFactory<>("dateCheck"));
		
		// 리스트뷰에 수정,삭제버튼생성
		modify.setCellFactory(item -> new TableCell<ClassDto, Boolean>() {

			private final Button editButton = new Button("수정");
			private final Button deleteButton = new Button("삭제");

			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null);
				} else {
					// 버튼2개를 넣기 위해 HBox 생성 후 버튼들 넣어줌
					HBox hBox = new HBox(editButton, deleteButton);
					editButton.setPrefWidth(50);
					deleteButton.setPrefWidth(50);
					hBox.setSpacing(5);
					hBox.setAlignment(Pos.CENTER);

					// 선택된 과제의 마감 날짜와 현재날짜를 비교해서
					ClassDto selectedRowClass = getTableView().getItems().get(getIndex());
					LocalDate c = LocalDate.now();
					LocalDate e = selectedRowClass.getEndDate();
					
					long diff = ChronoUnit.DAYS.between(c,e);
					
					String teacherId = selectedRowClass.getTeacherId();
					
					// 마감날짜 지나면 수정.삭제 불가능하게 함
					if (diff < 0 || !teacherId.equals(userid)) {
						editButton.setDisable(!item);
						deleteButton.setDisable(!item);
					} else {
						editButton.setDisable(item);
						deleteButton.setDisable(item);
					}
					setGraphic(hBox);

					// 수정 버튼 클릭 시 과제 수정
					editButton.setOnAction(event -> {
						int selectedClassNo = selectedRowClass.getClassNo();
						Stage dialog = new Stage(StageStyle.UTILITY);
						
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(teacherMain.getScene().getWindow());
						dialog.setTitle("강의수정");
										
						try {
							Parent parent = FXMLLoader.load(getClass().getResource("../../fxml/classes/update_class.fxml"));
							ClassDto class1 = cDao.selectClassOne(selectedClassNo);
							
							TextField txtClassName = (TextField) parent.lookup("#className");
							Label txtTeacherName = (Label) parent.lookup("#teacherName");
							txtTeacherName.setText(username);
							TextArea txtDescription = (TextArea) parent.lookup("#description");
							DatePicker localDateStartDate = (DatePicker) parent.lookup("#startDate");
							DatePicker localDateEndDate = (DatePicker) parent.lookup("#endDate");
							TextField txtLimitStudent = (TextField) parent.lookup("#limitStudent");
							Button btnUpdate = (Button) parent.lookup("#update");
							Button btnCancel = (Button) parent.lookup("#cancel");
							
							txtLimitStudent.textProperty().addListener(new ChangeListener<String>() {
								@Override
								public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
									if (!newValue.matches("\\d*")) {
										txtLimitStudent.setText(newValue.replaceAll("[^\\d]", ""));
									}
								}
							});
							
							//기존에 저장되어있던 강의정보 가져오기
							String className = class1.getClassName();
							String description = class1.getClassDescription();
							LocalDate startDate = class1.getStartDate();
							LocalDate endDate = class1.getEndDate();
							int limitStudent = class1.getLimitStudent();
							
							txtClassName.setText(className);
							txtDescription.setText(description);
							localDateStartDate.setValue(startDate);
							localDateEndDate.setValue(endDate);
							txtLimitStudent.setText(limitStudent + "");
							
							//수정버튼눌렀을시 수정이벤트발생
							btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent event) {
									//수정할 값 입력
									boolean flag = true;
									String className = txtClassName.getText();
									String description = txtDescription.getText();
									LocalDate startDate = localDateStartDate.getValue();
									LocalDate endDate = localDateEndDate.getValue();
									int limitStudent = 0;
									long day1 = 0;
									long day2 = 0;
									
									try {
										limitStudent = Integer.parseInt(txtLimitStudent.getText());										
									} catch (Exception e) {}
									
									//시작일과 종료일을 비교
									if(startDate!=null && endDate!=null) {
										day1 = ChronoUnit.DAYS.between(startDate,endDate);					
									}
									//종료일과 오늘날짜를 비교
									LocalDate today = LocalDate.now();
									if(endDate!=null) {
										day2 = ChronoUnit.DAYS.between(today,endDate);
									}
									
									Alert alert = new Alert(AlertType.INFORMATION);
									if(className.length()==0) {
										alert.setContentText("강의명을 입력하세요");
										flag = false;
									} else if(description.length()==0) {
										alert.setContentText("강의설명을 입력하세요");
										flag = false;
									} else if(startDate==null) {
										alert.setContentText("시작일을 입력하세요");
										flag = false;
									} else if(endDate==null) {
										alert.setContentText("종료일을 입력하세요");
										flag = false;
									} else if(day1<0) {
										alert.setContentText("강의종료일은 강의시작일보다 먼저일 수 없습니다.");
										flag = false;
									} else if(day2<0) {
										alert.setContentText("강의종료일은 오늘보다 늦어야 합니다.");
										flag = false;
									} else if(limitStudent<=0) {
										alert.setContentText("수강인원수(자연수)를 입력하세요");
										flag = false;
									}
									
									//값이 입력불가능하면 alert창을 보여준다
									if(!flag) {
										alert.show();
									} else {
										//값이 입력가능하면 수정한다
										cDao.updateClass(className, description, startDate, endDate, limitStudent, selectedClassNo);
										refreshTable();
										dialog.close();										
									}
								}
							});
							
							//취소하기
							btnCancel.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									dialog.close();
								}
							});
							
							Scene scene = new Scene(parent);
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});

					// 삭제 버튼 클릭 시 과제 삭제
					deleteButton.setOnAction(event -> {
						int selectedReportNo = selectedRowClass.getClassNo();

						// 진짜 삭제할건지 확인
						event.consume();
						Alert deleteConfiguration = new Alert(Alert.AlertType.CONFIRMATION);
						deleteConfiguration.setTitle("삭제 확인");
						deleteConfiguration.setHeaderText(selectedRowClass.getClassNo() + "\n" + selectedRowClass.getClassName());
						deleteConfiguration.setContentText("정말로 강의를 삭제하시겠습니까?");
						Optional<ButtonType> result = deleteConfiguration.showAndWait();
						if (result.get().equals(ButtonType.OK)) {
							cDao.deleteClass(selectedReportNo);
							refreshTable();
						}
					});
				}
			}
		});
		
		//리스트뷰에 상세보기 버튼생성
		detail.setCellFactory(item -> new TableCell<ClassDto, Boolean>(){
				
			private final Button detailButton = new Button("상세보기");
					
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
						
				if (empty || item == null) {
					setGraphic(null);
				} else {
							
					HBox hBox = new HBox(detailButton);
					detailButton.setPrefWidth(80);
					hBox.setAlignment(Pos.CENTER);
							
					setGraphic(hBox);
							
					ClassDto selectedRowClass = getTableView().getItems().get(getIndex());
							
					detailButton.setOnAction(event -> {
						int selectedClassNo = selectedRowClass.getClassNo();
						//강의상세보기 stage생성
						Stage dialog = new Stage(StageStyle.UTILITY);
						
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(teacherMain.getScene().getWindow());
						dialog.setTitle("강의상세보기");
						
						try {
							Parent parent = FXMLLoader.load(getClass().getResource("../../fxml/classes/detail_class.fxml"));
							ClassDto classDto = cDao.selectClassOne(selectedClassNo);
									
							Label txtClassName = (Label) parent.lookup("#className");
							Label txtTeacherName = (Label) parent.lookup("#teacherName");
							TextArea txtDescription = (TextArea) parent.lookup("#description");
							Label localDateStartDate = (Label) parent.lookup("#startDate");
							Label localDateEndDate = (Label) parent.lookup("#endDate");
							Label txtLimitStudent = (Label) parent.lookup("#limitStudent");
									
							//기존에 저장되어있던 강의정보 가져오기
							String className = classDto.getClassName();
							String description = classDto.getClassDescription();
							String teachername = classDto.getTeacherName();
							LocalDate startDate = classDto.getStartDate();
							LocalDate endDate = classDto.getEndDate();
							int limitStudent = classDto.getLimitStudent();
									
							txtTeacherName.setText(teachername);
							txtClassName.setText(className);
							txtDescription.setText(description);
							localDateStartDate.setText(startDate + "");
							localDateEndDate.setText(endDate + "");
							txtLimitStudent.setText(limitStudent + "");
									
							Scene scene = new Scene(parent);
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
				}
			}
		});		
		
		refreshTable();
	}
	
	public void refreshTable() {
		classList = cDao.selectMyClassList(userid);
		classTableView.setItems(classList);
	}
	
	//강의추가버튼
	public void btnNewClass(ActionEvent event) throws Exception {
		//강의추가 Stage생성
		Stage dialog = new Stage(StageStyle.UTILITY);
						
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(teacherMain.getScene().getWindow());
		dialog.setTitle("강의추가");
						
		Parent parent = FXMLLoader.load(getClass().getResource("../../fxml/classes/add_class.fxml"));
		
		TextField txtClassName = (TextField) parent.lookup("#className");
		Label txtTeacherName = (Label) parent.lookup("#teacherName");
		txtTeacherName.setText(username);
		TextArea txtDescription = (TextArea) parent.lookup("#description");
		DatePicker localDateStartDate = (DatePicker) parent.lookup("#startDate");
		DatePicker localDateEndDate = (DatePicker) parent.lookup("#endDate");
		TextField txtLimitStudent = (TextField) parent.lookup("#limitStudent");
		Button btnSave = (Button) parent.lookup("#save");
		
		txtLimitStudent.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (!newValue.matches("\\d*")) {
					txtLimitStudent.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		
		//강의만들기 
		btnSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				boolean flag = true;
				String className = txtClassName.getText();
				String description = txtDescription.getText();
				LocalDate startDate = localDateStartDate.getValue();
				LocalDate endDate = localDateEndDate.getValue();				
				int limitStudent = 0;
				long day1 = 0;
				long day2 = 0;
				
				try {
					limitStudent = Integer.parseInt(txtLimitStudent.getText());
				} catch (Exception e) {}
				
				String teacherid = userid;
				
				//시작일과 종료일을 비교
				if(startDate!=null && endDate!=null) {
					day1 = ChronoUnit.DAYS.between(startDate,endDate);					
				}
				//종료일과 오늘날짜를 비교
				LocalDate today = LocalDate.now();
				if(endDate!=null) {
					day2 = ChronoUnit.DAYS.between(today,endDate);
				}
				
				Alert alert = new Alert(AlertType.INFORMATION);
				if(className.length()==0) {
					alert.setContentText("강의명을 입력하세요");
					flag = false;
				} else if(description.length()==0) {
					alert.setContentText("강의설명을 입력하세요");
					flag = false;
				} else if(startDate==null) {
					alert.setContentText("시작일을 입력하세요");
					flag = false;
				} else if(endDate==null) {
					alert.setContentText("종료일을 입력하세요");
					flag = false;
				} else if(day1<0) {
					alert.setContentText("강의종료일은 강의시작일보다 먼저일 수 없습니다.");
					flag = false;
				} else if(day2<0) {
					alert.setContentText("강의종료일은 오늘보다 늦어야 합니다.");
					flag = false;
				} else if(limitStudent<=0) {
					alert.setContentText("수강인원수(자연수)를 입력하세요");
					flag = false;
				}
				
				//값이 입력불가능하면 alert창을 보여준다
				if(!flag) {
					alert.show();					
				} else {
					//값이 입력가능하면 DB에값을 입력한다
					cDao.insertBoard(new ClassDto(className, teacherid, teacherid, description, startDate, endDate, limitStudent));					
					refreshTable();
					dialog.close();
				}
			}
		});
		
		//취소
		Button btnCancel = (Button) parent.lookup("#cancel");
		btnCancel.setOnAction(e -> dialog.close());
		
		Scene scene = new Scene(parent);	//Scene생성
		dialog.setScene(scene);
		dialog.setResizable(false);	//사용자가 크기조절을 하지 못하게 함
		dialog.show();
		
	}
	
	//검색버튼 이벤트
	public void btnSearch(ActionEvent event) {
		String search = txtSearch.getText();
		classList = cDao.searchClassList(search);
		classTableView.setItems(classList);
	}

}