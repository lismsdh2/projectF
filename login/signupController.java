package login;

import java.net.URL;
import java.util.ResourceBundle;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import DAO.BasicDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Util;
import util.mail.Mail;

public class signupController implements Initializable {
	
	@FXML private TextField idfield;
	@FXML private TextField namefield;
	@FXML private PasswordField passfield;
	@FXML private PasswordField repassfield;
	@FXML private TextField emailfield;
	@FXML private TextField certificationfield;
	@FXML private ComboBox<String> combobox;
	@FXML private ComboBox<String> email2;
	@FXML private TextField phonefield1;
	@FXML private TextField phonefield2;
	@FXML private Button done;
	@FXML private Button cancle; 
	@FXML private Button student;
	@FXML private Button teacher;
	@FXML private Button check;
	@FXML private Button btnCertification;
	@FXML private Label idlabel;
	@FXML private Label namelabel;
	@FXML private Label emaillabel;
	@FXML private Label lblCertification;
	@FXML private Label passlabel;
	@FXML private Label repasslabel;
	@FXML private Label numberlabel;
	@FXML private Label passchecklabel;
	@FXML private Label label;
	
	private Stage pop;
	private int recheck;
	private boolean identityType;
	private boolean checkMail;
	private String certificationNum;
	private Mail mail;
	BasicDao bDao = new BasicDao();
	Alert alert =new Alert(AlertType.INFORMATION);
	
	public void initialize(URL location, ResourceBundle resources) {
		
		idlabel.setVisible(false);
		namelabel.setVisible(false);
		emaillabel.setVisible(false);
		passlabel.setVisible(false);
		repasslabel.setVisible(false);
		numberlabel.setVisible(false);
		passchecklabel.setVisible(false);
		
		//이메일 인증
		this.certificationfield.setVisible(false);
		this.lblCertification.setVisible(false);
		this.btnCertification.setOnAction(e -> {handleEmailCheck();});
		
		done.setOnAction(e->handledone(e));
		cancle.setOnAction(e->handlecancle(e));
		student.setOnAction(e->handlestudent(e));
		teacher.setOnAction(e->handleteacher(e));
		check.setOnAction(e->handlecheck(e));
		
		//입력양식 제한
		idfield.textProperty().addListener(Util.alphabetListener(idfield));
		passfield.textProperty().addListener(Util.pwListener(passfield));
		repassfield.textProperty().addListener(Util.pwListener(repassfield));
		emailfield.textProperty().addListener(Util.alphabetListener(emailfield));
		
		//숫자만 입력
		phonefield1.textProperty().addListener(Util.numberOnlyListener(phonefield1));
		phonefield2.textProperty().addListener(Util.numberOnlyListener(phonefield2));
		
		//글자수 제한
		phonefield1.textProperty().addListener(Util.textCountLimit(phonefield1, 4));
		phonefield2.textProperty().addListener(Util.textCountLimit(phonefield2, 4));
		idfield.textProperty().addListener(Util.textCountLimit(idfield, 20));
		namefield.textProperty().addListener(Util.textCountLimit(namefield, 20));
		emailfield.textProperty().addListener(Util.textCountLimit(emailfield, 30));
		passfield.textProperty().addListener(Util.textCountLimit(passfield, 20));
		repassfield.textProperty().addListener(Util.textCountLimit(repassfield, 20));
		
	}

	//교사신분 선택
	public void handleteacher(ActionEvent e) {
		System.out.println("교사로 전환");
		identityType=true;
	}

	//학생신분 선택
	public void handlestudent(ActionEvent e) {
		System.out.println("학생으로 전환");
		identityType=false;
	}

	//이메일 인증
	private void handleEmailCheck() {
	
		if(this.emailfield.getText().length() != 0 &&
		   this.email2.getSelectionModel().getSelectedItem() != null) {

			String address = emailfield.getText() + "@" + this.email2.getSelectionModel().getSelectedItem();
			//이메일 중복 체크
			if(bDao.checkDuplicateMail(address)) {
				
					Alert alert = Util.showAlert("", "가입된 이메일 주소가 있습니다.", AlertType.INFORMATION);
					alert.showAndWait();
				
			} else {
				
				//이메일 입력 라벨 숨기기
				this.emaillabel.setVisible(false);
				
				//인증번홉 입력칸 생성
				this.certificationfield.setVisible(true);
				
				//메일 값 넣기
				this.mail = new Mail(this.namefield.getText(), address);
				this.mail.mailSend();
				this.checkMail = this.mail.getCheckMail();
				if(this.checkMail) {
					Alert alert = Util.showAlert("", "인증메일이 발송되었습니다.", AlertType.INFORMATION);
					alert.showAndWait();
				}
				this.certificationNum = this.mail.getCertificationNum();
				System.out.println(this.certificationNum);
			}
			
		} else {
			
			this.emaillabel.setText("이메일을 입력해주세요.");
			this.emaillabel.setVisible(true);
		}
	}

	//완료버튼 선택
	public void handledone(ActionEvent e) {
		idlabel.setVisible(false);
		namelabel.setVisible(false);
		emaillabel.setVisible(false);
		passlabel.setVisible(false);
		repasslabel.setVisible(false);
		numberlabel.setVisible(false);
		passchecklabel.setVisible(false);

		
		try {
			//아이디 입력 확인
			if(idfield.getText().length()==0) {
				idlabel.setVisible(true);
			}
			//이름 입력 확인
			if(namefield.getText().length()==0) {
				namelabel.setVisible(true);
			}
			//비밀번호 입력 확인
			if(passfield.getText().length()==0) {
				passlabel.setVisible(true);
			}
			//비밀번호 확인 입력 확인
			if(repassfield.getText().length()==0) {
				repasslabel.setVisible(true);
			}
			//이메일ID 입력 확인
			if(email2.getValue()==null) {
				emaillabel.setVisible(true);
			}
			//이메일 주소 입력 확인 
			if(emailfield.getText().length()==0) {
				emaillabel.setVisible(true);
			}
			//이메일 인증 번호 입력 확인
			if(certificationfield.getText().length()==0) {
				lblCertification.setText("인증번호를 입력해주세요.");
				lblCertification.setVisible(true);
			}
			//휴대폰번호 가운데 4자리 입력 확인
			if(phonefield1.getText().length()==0) {
				numberlabel.setVisible(true);
			}
			//휴대폰번호 마지막 4자리 입력 확인
			if(phonefield2.getText().length()==0) {
				numberlabel.setVisible(true);
			}
			//콤보박스 선택 확인
			if(combobox.getValue()==null) {
				numberlabel.setVisible(true);
			}
		
			String id = idfield.getText();
			String name = namefield.getText();
			String pass1 = passfield.getText();
			String pass2 = repassfield.getText();
			String email = emailfield.getText()+"@"+(String)email2.getValue();
			String pass = passfield.getText();
			String phone1 = phonefield1.getText();
			String phone2 = phonefield2.getText();
			String combo = (String) combobox.getValue();
			String phone = combo+phone1+phone2;
			
			if(!(pass1.equals(pass2))) { 
				repasslabel.setVisible(false);
				passchecklabel.setVisible(true);
				throw new Exception();
			}
			
			if(id.length()==0 || name.length()==0 || pass1.length()==0 
				|| pass2.length()==0 || email.length()==0 || phone1.length()==0 
				  || phone2.length()==0 || combobox.getValue()==null || email2.getValue()==null){
				
			 System.out.println("데이터가 없습니다.");
			 throw new Exception();
			}
			
			if(!(phonefield1.getText().length()==4)) {
				System.out.println("핸드폰 번호 4자리를 입력해 주세요.");
				throw new Exception();
			}
			else if(!(phonefield2.getText().length()==4)) {
				System.out.println("핸드폰 번호 4자리를 입력해 주세요.");
				throw new Exception();
			}
			if(recheck==0) { //중복체크를 안 했을시
				alert.setContentText("중복체크를 해 주세요.");
			    alert.show();
			    throw new Exception();
			}
		
			//인증메일
			if(!checkMail) {
				
				alert.setContentText("인증메일발송버튼을 눌러 주세요.");
			    alert.show();
			    throw new Exception();
			} else if(!(this.certificationNum.equals(certificationfield.getText().toLowerCase()))){
			
				alert.setContentText("인증번호가 일치하지 않습니다.");
			    alert.show();
			    throw new Exception();
			}
			
			bDao.insertBoard(id, pass, name, email, phone ,identityType);	
	
			alert.setContentText("회원가입완료");
			alert.show();
			
			pop = (Stage)cancle.getScene().getWindow(); 
	        pop.close();
		}
		catch (MySQLIntegrityConstraintViolationException e1) {
			
			System.out.println("이메일중복");
		}
		catch (Exception ex){
			System.out.println("회원가입 실패");
//			ex.printStackTrace();
		}
	}

	//취소버튼 
	public void handlecancle(ActionEvent e) {
		pop = (Stage)cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
        pop.close();
	}
    public void handlecheck(ActionEvent e) {
		
	    try {
		String id = idfield.getText();
		if(id.length()==0) {
			 alert.setContentText("아이디를 입력해주세요.");
			 alert.show();
			 throw new Exception();
		}
		bDao.check(id);
		
		recheck=1;
	   }catch(Exception ex) {System.out.println("회원가입 중복체크 에러");}
     }
}
