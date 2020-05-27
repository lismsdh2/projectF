/*
 * 작성자 : 도현호
 * 최종 작성일 : 2020-05-15
 */
package Enrollment.EnrollmentPopup;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import DTO.EnrollmentDto;
import DTO.EnrollmentPopupDto;
import Enrollment.EnrollmentController1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EnrollmentPopupController1 implements Initializable {

	@FXML private Label lblClassname;
	@FXML private Label lblTeachername;
	@FXML private Label lblClassdescription;
	@FXML private Label lblClassdate;
	@FXML private Label lblLimitclass;
	@FXML private Button btnSubmit;
	@FXML private Button btnCancle;

	private int c_no;
	private Stage popupStage;
	private EnrollmentController1 enrollmentController;
	private EnrollmentDto enroll = new EnrollmentDto();
	
	public EnrollmentPopupController1(EnrollmentController1 enrollmentController) {
		
		this.enrollmentController = enrollmentController;
		//수강신청 상세화면 설정 
		popupStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/student/enrollment/enrollment_popup.fxml"));
			loader.setController(this);
			popupStage.setScene(new Scene(loader.load()));
			popupStage.setTitle("수강 신청");
			System.out.println("수강신청 화면 열기 성공");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("수강신청 화면 열기 실패");
		}
	}

	//수강신청 상세화면 띄우기
	public void showStage() {
		
		enrollView(this.c_no);
		popupStage.showAndWait();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnSubmit.setOnAction(e -> { handleBtnSubmit(); });
		btnCancle.setOnAction(e -> { handleBtnCancle(); });
	}

	// 클래스번호 받기
	public void setClassno(int c_no) {
		
		this.c_no = c_no;
	}
	
	// 화면에 정보 뿌리기
	public void enrollView(int c_no) {
		enroll = enrollmentController.eDao.enrollment_selectOne(c_no);
		lblClassname.setText(enroll.getClassname());	
		lblTeachername.setText(enroll.getTeachername());
		lblClassdescription.setText(enroll.getClassdescription());
		lblClassdate.setText(enroll.getStartdate().toString() + " ~ " + enroll.getEnddate().toString());
		lblLimitclass.setText(Integer.toString(enroll.getLimitstudent()));
	}

	// 신청 버튼
	public void handleBtnSubmit() {
		
		try {
			Alert alert = alertConfirmation("신청하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
            // OK누르면 신청 후 알림창 출력
            if (result.get().equals(ButtonType.OK)) {
            	
            	String stu_id = "abcabc"; 				// 학생ID연동해야됨
            	int class_no = enroll.getClassno();
            	EnrollmentPopupDto rCls = new EnrollmentPopupDto(stu_id, class_no);
            	enrollmentController.eDao.submit_Enrollment(rCls);
            	alertInformation("신청되었습니다");
            }
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("강의 신청 실패");
			System.out.println("중복 신청 ");
			alertError("이미 신청되어 있습니다.");
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
		alert.setTitle("수강 신청 실패");
		alert.setContentText(msg);
		alert.showAndWait();
	}
	
	//신청확인 메세지 창
	private Alert alertConfirmation(String msg) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("수강 신청 확인");
		alert.setContentText(msg);
		return alert;
	}
	
	//신청확인 메세지 창
	public void alertInformation(String msg) {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("수강 신청 완료");
		alert.setContentText(msg);
		Optional<ButtonType> result = alert.showAndWait();
		// OK누르면 창 종료
		if (result.get().equals(ButtonType.OK)) {
			popupStage.close();
		}
	}
}

