package student.classes.popup;
/*
 * 작성자 : 도현호
 */

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import DAO.EnrollmentDao;
import DTO.EnrollmentDto;
import DTO.UserDto;
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
import javafx.stage.Stage;
import launch.AppMain;
import student.classes.Enrollment_Controller;

public class EnrollmentPopupController implements Initializable {

	@FXML private Label lblClassname;
	@FXML private Label lblTeachername;
	@FXML private TextArea txtClassdescription;
	@FXML private Label lblClassdate;
	@FXML private Label lblLimitclass;
	@FXML private Button btnSubmit;
	@FXML private Button btnCancle;

	private Stage popupStage;
	private Enrollment_Controller enrollmentController;
	private EnrollmentDto enroll = new EnrollmentDto();
	private EnrollmentDao eDao = new EnrollmentDao();
	private UserDto uDto = AppMain.app.getUser();
	private	String stu_id = uDto.getId();					//학생ID
	private int c_no;										//강좌번호
	
	public EnrollmentPopupController(Enrollment_Controller enrollmentController) {
		
		this.enrollmentController = enrollmentController;
		//수강신청 상세화면 설정 
		popupStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../fxml/student/classes/enrollment_popup.fxml"));
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
		
		btnSubmit.setOnAction(e -> {
			enroll = eDao.check_requestClass(this.c_no, this.stu_id);
			System.out.println(enroll.getCheck());
			if(enroll.getCheck()){
				handleBtnDelete();						//수강취소
			} else {
				handleBtnSubmit();						//수강신청
			}
			
		});
		btnCancle.setOnAction(e -> { handleBtnCancle(); });
	}

	// 클래스번호 받기
	public void setClassno(int c_no) {
		
		this.c_no = c_no;
	}
	
	// 화면에 정보 뿌리기
	public void enrollView(int c_no) {
		enroll = eDao.enrollment_selectOne(c_no);
		lblClassname.setText(enroll.getClassname());	
		lblTeachername.setText(enroll.getTeachername());
		txtClassdescription.setText(enroll.getClassdescription());
		lblClassdate.setText(enroll.getStartdate().toString() + " ~ " + enroll.getEnddate().toString());
		int cnt = (enroll.getLimitstudent() - enroll.getCurrentstudent());		//수강신청 가능인원수
		lblLimitclass.setText(cnt+"명");
		
		//강좌설명 자동줄바꿈
		txtClassdescription.setWrapText(true);
		//강좌설명 입력제한
		txtClassdescription.setEditable(false);
		//수강신청 가능 인원이 0이면 버튼 비활성화
		enroll = eDao.check_requestClass(c_no, this.stu_id);
		if(cnt == 0 && !enroll.getCheck()) {
			
			btnSubmit.setDisable(true);
		}
		//버튼 글자바꾸기
		if(enroll.getCheck()) {
			btnSubmit.setText("수강취소");
		} else {
			btnSubmit.setText("등록");
		}
	}

	// 신청 버튼
	public void handleBtnSubmit() {
		
		try {
			Alert alert = alertConfirmation("신청하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
            // OK누르면 신청 후 알림창 출력
            if (result.get().equals(ButtonType.OK)) {
            	
            	eDao.submit_Enrollment(this.stu_id, this.c_no);
            	alertInformation("신청되었습니다");
            }
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("강의 신청 실패");
			alertError("이미 신청되어 있습니다.");
		}
	}
	
	//수강취소 버튼
	public void handleBtnDelete() {
		
		try {
			Alert alert = alertConfirmation("취소하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
            // OK누르면 신청 후 알림창 출력
            if (result.get().equals(ButtonType.OK)) {
            	
            	eDao.delete_Enrollment(this.stu_id, this.c_no);
            	alertInformation("취소되었습니다");
            }
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("수강취소 신청 실패");
			alertError("취소가 실패되었습니다.");
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