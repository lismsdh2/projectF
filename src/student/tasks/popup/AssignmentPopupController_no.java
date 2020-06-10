/*
 * 작성자 : 도현호
 */
package student.tasks.popup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import DAO.AssignmentDao;
import DTO.AssignmentDto;
import DTO.AssignmentPopupDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launch.AppMain;

public class AssignmentPopupController_no implements Initializable {

	@FXML private Label lblTaskName;
	@FXML private TextArea txtTaskDesc;
	@FXML private Label lblMyScore;
	@FXML private Label lblPerfectScore;
	@FXML private Label lblExpireDate;
	@FXML private Label lblAttachedFilename;
	@FXML private TextArea txtQuestion;
	@FXML private Label lblAnswer;
	@FXML private Label lblTaskFilename;
	@FXML private Button btnDownload;
	@FXML private Button btnSubmitFile;
	@FXML private Button btnSubmit;
	@FXML private Button btnCancle;

	private AssignmentDao aDao = new AssignmentDao();
	private AssignmentDto assign = new AssignmentDto();
	private String student_id = AppMain.app.getBasic().getId();			//학생ID
	private int class_no = AppMain.app.getBasic().getClass_no();		//강의번호
	private int task_no;												//과제번호				
	
	private Stage popupStage;
	
	public AssignmentPopupController_no() {
		
		//과제제출 상세화면 설정 
		popupStage = new Stage(StageStyle.UTILITY);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../fxml/student/tasks/assignment_popup.fxml"));
			loader.setController(this);
			popupStage.setScene(new Scene(loader.load()));
			popupStage.setResizable(false);
			popupStage.setTitle("과제 제출");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("과제제출(처음) 생성자 생성 실패");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnDownload.setOnAction(e -> {handleBtnAttachFile();});
		btnSubmitFile.setOnAction(e -> {handleBtnSubmitFile();});
		btnSubmit.setOnAction(e -> { handleBtnSubmit(); });
		btnCancle.setOnAction(e -> { handleBtnCancle(); });
		//강좌설명 자동줄바꿈
		txtTaskDesc.setWrapText(true);
		//강좌설명 입력제한
		txtTaskDesc.setEditable(false);
	}
	
	public Stage getPopupStage() {
		
		return this.popupStage;
	}

	//과제제출 상세화면 띄우기
	public void showStage() {
		this.class_no = AppMain.app.getBasic().getClass_no();
		this.task_no = AppMain.app.getBasic().getTask_no();
		//과제번호는 받기전 인스턴스가 생성되기에 task_no은 0으로 초기화되어 있음, 별도로 받아야 함

		System.out.println("팝업:"+class_no);
		System.out.println("팝업:"+task_no);
		assignView(this.class_no, this.task_no);
		try {
			
			popupStage.showAndWait();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//화면에 정보 뿌리기
	private void assignView(int c_no, int t_no) {

		assign = aDao.assignment_selectOne(c_no, t_no);
		lblTaskName.setText(assign.getTask_name());								//과제명
		txtTaskDesc.setText(assign.getTask_desc());								//과제설명
		lblMyScore.setText(Integer.toString(assign.getMyScore()));				//나의 과제 점수
		lblPerfectScore.setText(Integer.toString(assign.getPerfect_score()));	//과제 만점 점수
		lblExpireDate.setText(assign.getExpire_date().toString());				//과제 마김일
		lblAttachedFilename.setText(assign.getAttachedFile_name());				//첨부파일명
		txtQuestion.setText("");												//문의사항
		lblAnswer.setText("");													//답변
		lblTaskFilename.setText(assign.getTaskFile_name());						//과제제출
		//과제설명 줄바꿈
		txtTaskDesc.setWrapText(true);
		//과제설명 입력 제한
		txtTaskDesc.setEditable(false);
		//질문사항 줄바꿈
		txtQuestion.setWrapText(true);
		//다운로드할 파일 없을 경우 버튼 비활성화
		if(assign.getAttachedFile() == null) {

			btnDownload.setDisable(true);
		}
	}
	
	//참고파일 다운로드 버튼
	public void handleBtnAttachFile() {
		
		//파일 확장자 찾기
		String fileName = assign.getAttachedFile_name();
		int extension_idx = fileName.lastIndexOf(".")+1;			//마지막 위치의 앞 인덱스 리턴하기에 +1
		String fileExtension = fileName.substring(extension_idx, fileName.length());
		
		//파일 저장DIALOG 띄우기 - 기존 파일 확장자로 바로 저장하기
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
			new ExtensionFilter(fileExtension,"*."+fileExtension)
		);

		try {
			//저장할 파일명 지정
			File file = fileChooser.showSaveDialog(this.popupStage);
			if(file != null) {
				
				InputStream is = assign.getAttachedFile();
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				byte[] readData = new byte[10000];
				int data = 0;
				while((data = is.read(readData)) != -1) {
					
					bos.write(readData,0,data);
				}
				bos.flush();
				bos.close();
				fos.close();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//첨부파일 업로드 버튼
	public void handleBtnSubmitFile() {
		
		//파일 열기DIALOG 띄우기
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("AllFiles","*.*")
		);
		
		try {
			
			//첨부할 파일 지정
			File file = fileChooser.showOpenDialog(this.popupStage);
		
			if(file != null) {//취소단추 누를 때 예외방지
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					
					int data = 0;
					byte[] readData = new byte[10000];
					while((data = bis.read(readData))!= -1 ) {
						
						baos.write(readData,0,data);
					}
					System.out.println("파일 읽기 성공");
				} catch (IOException e) {
					
					e.printStackTrace();
					System.out.println("파일 읽기 실패");
				}
				assign.setTaskFile(baos.toByteArray());				//property에 넣기
				String file_name = file.getName();					//파일명 출력
				assign.setTaskFile_name(file_name);
				lblTaskFilename.setText(file_name);
				System.out.println("파일명 : " + file_name);
			} 
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			System.out.println("파일 선택 오류");
		}
	}
	
	//제출 버튼
	public void handleBtnSubmit() {
		
		try {
			
			Alert alert = alertConfirmation("제출하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
            // OK누르면 신청 후 알림창 출력
            if (result.get().equals(ButtonType.OK)) {
          
            	String taskQuestion = txtQuestion.getText();
            	byte[] taskFile = assign.getTaskFile();
            	String taskFile_name = assign.getTaskFile_name();
            	System.out.println("taskFile_name : "+ taskFile_name);
            	
            	//submitTask DTO
            	AssignmentPopupDto sTask = new AssignmentPopupDto(this.class_no, this.task_no, this.student_id, taskQuestion, taskFile, taskFile_name);
            	aDao.submit_Assignment(sTask);
            	alertInformation("제출되었습니다");
            }
		} catch (SQLException e) {
			
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("과제 제출 실패");
			alertError("이미 제출했습니다.");
		}
	}
	
	// 취소 버튼
	public void handleBtnCancle() {

		popupStage.close();
		System.out.println("취소 버튼 클릭");
	}
	
	//에러 메세지 창
	private void alertError(String msg) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("과제 제출 실패");
		alert.setContentText(msg);
		alert.showAndWait();
	}
	
	//신청확인 메세지 창
	private Alert alertConfirmation(String msg) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("과제 제출 확인");
		alert.setContentText(msg);
		return alert;
	}
	
	//신청확인 메세지 창
	public void alertInformation(String msg) {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("과제 제출 완료");
		alert.setContentText(msg);
		Optional<ButtonType> result = alert.showAndWait();
		// OK누르면 창 종료
		if (result.get().equals(ButtonType.OK)) {
			popupStage.close();
		}
	}
}