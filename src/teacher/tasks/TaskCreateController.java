package teacher.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import DTO.ClassDto;
import DTO.TaskDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author 김지현
 *
 */
public class TaskCreateController implements Initializable {
	@FXML
	private TextField txtTitle;

	@FXML
	private TextField txtFile;

	@FXML
	private DatePicker dateExpire;

	@FXML
	private TextArea txtDesc;

	@FXML
	private Button btnFile;

	@FXML
	private Button btnSubmit;

	@FXML
	private TextField txtPerfectScore;

	@FXML
	private Label txtClassName;
	Stage createStage;
	TaskListController rController;
	ClassDto nowClass;

	public void setCreateStage(Stage createStage) {
		this.createStage = createStage;
	}

	public TaskCreateController(TaskListController rController) {
		this.rController = rController;

		try {
			createStage = new Stage();
			createStage.initModality(Modality.WINDOW_MODAL); // 모달이 안되는거같은데;
			createStage.initOwner(rController.reportListStage);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/teacher/tasks/TaskCreate.fxml"));
			loader.setController(this);
			createStage.setScene(new Scene(loader.load()));
			createStage.setTitle("과제생성");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nowClass = rController.nowClass;
		
		txtClassName.setText(nowClass.getClassName());
		btnSubmit.setOnAction(e -> handleSubmitBtn());
		
		// 숫자만 입력할 수 있도록 제한
		txtPerfectScore.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					txtPerfectScore.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		// 첨부파일 버튼
		btnFile.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("첨부파일 등록");

			Scene scene = btnSubmit.getScene();
			File file = fc.showOpenDialog(scene.getWindow());
			if (file != null) {
				txtFile.setText(file.getPath());
			}
		});
	}

	private void handleSubmitBtn() {

		try {
			String name = txtTitle.getText();
			String desc = txtDesc.getText();
			String strPftScore = txtPerfectScore.getText();
			Integer perfectScore = 0;
//			Integer perfectScore = Integer.parseInt(txtPerfectScore.getText());
			LocalDate expireDate = null;
			LocalDate cDate = LocalDate.now();

			if (txtPerfectScore.getText().isEmpty()) {
				System.out.println("점수가없어!");
			} else {
				perfectScore = Integer.parseInt(strPftScore);
			}

			if (dateExpire.getValue() != null) {
				expireDate = dateExpire.getValue();
				// 과제 제출 날짜>현재 날짜 제한
				Period period = Period.between(cDate, expireDate);
				int diff = period.getDays();
				if (diff < 0) {
					// 팝업 넣기
					Alert expireDateAlert = new Alert(Alert.AlertType.ERROR);
					expireDateAlert.setTitle("날짜 오류");
					expireDateAlert.setContentText("오늘 이전 날짜로는 설정 할 수 없습니다.");
					expireDateAlert.showAndWait();
//					throw new Exception("오늘 이전 날짜로는 설정 불가");
					return;
				}
			}

			// 과제명은 비울 수 없다
			if (name.equals("")) {
				// 팝업 넣기
				Alert titleAlert = new Alert(Alert.AlertType.ERROR);
				titleAlert.setTitle("과제명 없음");
				titleAlert.setContentText("과제명이 입력되지 않았습니다.");
				titleAlert.showAndWait();
//				throw new Exception("과제명이 입력되지 않았습니다.");
				return;
			}

			String filename = txtFile.getText();

			System.out.println("**과제생성버튼 클릭");
//			System.out.println(name);
//			System.out.println(desc);
//			System.out.println(cDate);
//			System.out.println(expireDate);
//			System.out.println(filename);
//			System.out.println(perfectScore);

			//첨부파일 내용 받아와야함 - 일단 null로 설정
			TaskDto report = new TaskDto(name, desc, cDate, expireDate, null, filename, perfectScore);
			System.out.println(report.toString());

			// db에 데이터 저장하고, 불러와서 테이블에 보여주기
			rController.tDao.insertTask(report, nowClass.getClassNo());
			rController.refreshTable();

			// 입력창 클리어
			txtTitle.clear();
			txtDesc.clear();
			txtPerfectScore.clear();
			dateExpire.setValue(null);
			txtFile.clear();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	public void showStage() {
		createStage.show();
	}

}