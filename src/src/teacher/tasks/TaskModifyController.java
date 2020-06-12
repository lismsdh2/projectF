package teacher.tasks;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import DAO.TaskDao;
import DTO.TaskDto;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import util.Util;

/**
 * @author 김지현
 *
 */
public class TaskModifyController implements Initializable {
	@FXML
	private TextField txtFile;

	@FXML
	private Button btnFileDel;

	@FXML
	private Button btnDateDel;

	@FXML
	private DatePicker dateExpire;

	@FXML
	private TextArea txtDesc;

	@FXML
	private Button btnFile;

	@FXML
	private Button btnSubmit;

	@FXML
	private TextField txtTitle;

	@FXML
	private TextField txtPerfectScore;

	TaskDao tDao = new TaskDao();
	int selectedTaskNo;
	byte[] arr;

	public TaskModifyController(int selectedTaskNo) {
		this.selectedTaskNo = selectedTaskNo;
		setContents();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
		btnFile.setOnAction(e -> handleAttachedFileBtn());

		// 수정버튼
		btnSubmit.setOnAction(e -> handleSubmitBtn());

		// 날짜 삭제버튼
		btnDateDel.setOnAction(e -> dateExpire.setValue(null));

		// 첨부파일 삭제 버튼
		btnFileDel.setOnAction(e -> {
			arr = null;
			txtFile.clear();
			if (arr == null) {
				System.out.println("arr remove");
			}
		});
	}

	// 첨부파일 버튼
	private void handleAttachedFileBtn() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("AllFiles", "*.*"));
		fc.setTitle("첨부파일 등록");

		Scene scene = btnSubmit.getScene();
		File file = fc.showOpenDialog(scene.getWindow());

		// 선택된 파일이 있을 경우
		if (file != null) {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int data = 0;
				byte[] readData = new byte[10000];
				while ((data = bis.read(readData)) != -1) {
					baos.write(readData, 0, data);
				}

				arr = baos.toByteArray();

				String filePath = file.getPath();
				System.out.println("selected file path : " + filePath);

				String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
				System.out.println("selected file name : " + fileName);

				txtFile.setText(fileName);

				baos.close();
				bis.close();
				fis.close();
				System.out.println("file write 성공");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("파일 선택 오류");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("file write 실패");
			}
		}

	}

	// 과제 수정 버튼
	private void handleSubmitBtn() {
		System.out.println("**과제수정버튼 클릭!");
		TaskDto selectedReport = tDao.selectTask(selectedTaskNo);

		try {
			// 각 필드에서 값 읽기
			String name = txtTitle.getText();
			String desc = txtDesc.getText();
			String strPftScore = txtPerfectScore.getText();
			Integer perfectScore = null;
			LocalDate expireDate = null;
			LocalDate regDate = selectedReport.getTcRegdate();
			String filename = txtFile.getText();

			// 과제명 is not null
			if (name.equals("")) {
				// 팝업
				Util.showAlert("과제명 오류", "과제명이 입력되지 않았습니다.", AlertType.ERROR);
				return;
			}

			// 점수입력
			if (!strPftScore.isEmpty()) {
				perfectScore = Integer.parseInt(strPftScore);
			}

			// 과제 제출 날짜 검사
			if (dateExpire.getValue() != null) {
				expireDate = dateExpire.getValue();

				// 과제 제출 날짜>현재 날짜 제한
				Period period = Period.between(LocalDate.now(), expireDate);
				int diff = period.getDays();
				if (diff < 0) {
					// 팝업
					Util.showAlert("날짜 오류", "오늘 이전 날짜로는 설정할 수 없습니다.", AlertType.ERROR);
					return;
				}
			}

			// DTO에 수정된 값들 저장
			System.out.println("score- " + perfectScore);
			TaskDto modifiedReport = new TaskDto(selectedTaskNo, name, desc, regDate, expireDate, arr, filename,
					perfectScore);

			// db에 데이터 저장
			tDao.updateReport(modifiedReport);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	// 선택된 과제 정보 입력
	private void setContents() {
		System.out.println("selected Task : " + selectedTaskNo);
		TaskDto selectedReport = tDao.selectTask(selectedTaskNo);

		Platform.runLater(() -> {
			txtTitle.setText(selectedReport.getTcTitle());
			txtDesc.setText(selectedReport.getTcDesc());

			if (selectedReport.getPerfectScore() != null) {
				txtPerfectScore.setText(selectedReport.getPerfectScore().toString());
			} else {
				txtPerfectScore.setText("");
			}

			dateExpire.setValue(selectedReport.getTcExpireDate());
			txtFile.setText(selectedReport.getTcFile());
		});

	}
}
