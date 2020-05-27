package teacher.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author 김지현
 *
 */
public class TaskModifyController implements Initializable {
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

	Stage modifyStage;
	TaskListController rController;

	int selectedReportNo;

	public void setCreateStage(Stage modifyStage) {
		this.modifyStage = modifyStage;
	}

	public void setReportNo(int selectedReportNo) {
		this.selectedReportNo = selectedReportNo;
	}

	public TaskModifyController(TaskListController rController) {
		this.rController = rController;

		try {
			modifyStage = new Stage();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/teacher/tasks/TaskModify.fxml"));
			loader.setController(this);
			modifyStage.initOwner(rController.reportListStage);
			modifyStage.initModality(Modality.WINDOW_MODAL); // 모달이 안되는거같은데;
			modifyStage.setTitle("과제 수정");
			modifyStage.setScene(new Scene(loader.load()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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

		TaskDto selectedReport = rController.tDao.selectTask(selectedReportNo);

		try {
			String name = txtTitle.getText();
			String desc = txtDesc.getText();
			int perfectScore = Integer.parseInt(txtPerfectScore.getText());

			LocalDate expireDate = dateExpire.getValue();
			LocalDate cDate = selectedReport.getTcRegdate();

			// 과제명은 비울 수 없다
			if (name.equals("")) {
				// 팝업 넣기
				Alert titleAlert = new Alert(Alert.AlertType.ERROR);
				titleAlert.setTitle("과제명 없음");
				titleAlert.setContentText("과제명이 입력되지 않았습니다.");
				titleAlert.showAndWait();
//						throw new Exception("과제명이 입력되지 않았습니다.");
				return;
			}

			// 과제 제출 날짜>현재 날짜 제한
			Period period = Period.between(cDate, expireDate);
			int diff = period.getDays();
			if (diff < 0) {
				// 팝업 넣기
				Alert expireDateAlert = new Alert(Alert.AlertType.ERROR);
				expireDateAlert.setTitle("날짜 오류");
				expireDateAlert.setContentText("오늘 이전 날짜로는 설정 할 수 없습니다.");
				expireDateAlert.showAndWait();
//						throw new Exception("오늘 이전 날짜로는 설정 불가");
				return;
			}

			String filename = txtFile.getText();

			System.out.println("**과제수정버튼 클릭");
//					System.out.println(name);
//					System.out.println(desc);
//					System.out.println(cDate);
//					System.out.println(expireDate);
//					System.out.println(filename);
//					System.out.println(perfectScore);

			TaskDto modifiedReport = new TaskDto(selectedReportNo, name, desc, cDate, expireDate, filename, perfectScore);

			// db에 데이터 저장하고, 불러와서 테이블에 보여주기
			rController.tDao.updateReport(modifiedReport);
			rController.refreshTable();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	public void showStage() {
		System.out.println("sh" + selectedReportNo);
		TaskDto selectedReport = rController.tDao.selectTask(selectedReportNo);

		// 선택된 값 불러오기
		txtTitle.setText(selectedReport.getTcTitle());
		txtDesc.setText(selectedReport.getTcDesc());
		txtPerfectScore.setText(selectedReport.getPerfectScore().toString());
		dateExpire.setValue(selectedReport.getTcExpireDate());
		txtFile.setText(selectedReport.getTcFile());

		modifyStage.show();
	}

}
