package login;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class signupController implements Initializable {
	
	@FXML private TextField idfield;
	@FXML private TextField namefield;
	@FXML private TextField passfield;
	@FXML private TextField repassfield;
	@FXML private TextField emailfield;
	@FXML private ComboBox combobox;
	@FXML private ComboBox email2;
	@FXML private TextField phonefield1;
	@FXML private TextField phonefield2;
	@FXML private Button done;
	@FXML private Button cancle; 
	@FXML private Button student;
	@FXML private Button teacher;
	@FXML private Label idlabel;
	@FXML private Label namelabel;
	@FXML private Label emaillabel;
	@FXML private Label passlabel;
	@FXML private Label repasslabel;
	@FXML private Label numberlabel;
	@FXML private Label passchecklabel;
	
	private Stage pop;
	private boolean type;
	UserDao bDao = new UserDao();
	
	public void initialize(URL location, ResourceBundle resources) {
		
		idlabel.setVisible(false);
		namelabel.setVisible(false);
		emaillabel.setVisible(false);
		passlabel.setVisible(false);
		repasslabel.setVisible(false);
		numberlabel.setVisible(false);
		passchecklabel.setVisible(false);
		
		done.setOnAction(e->handledone(e));
		cancle.setOnAction(e->handlecancle(e));
		student.setOnAction(e->handlestudent(e));
		teacher.setOnAction(e->handleteacher(e));
	}

	//교사신분 선택
	public void handleteacher(ActionEvent e) {
		System.out.println("교사로 전환");
		type=true;
	}

	//학생신분 선택
	public void handlestudent(ActionEvent e) {
		System.out.println("학생으로 전환");
		type=false;
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
			
			bDao.insertBoard(id, pass, name, email, phone ,type);	
			Alert alert =new Alert(AlertType.INFORMATION);
			alert.setContentText("회원가입완료");
			alert.show();
			
			idfield.setText(null);
			namefield.setText(null);
			passfield.setText(null);
			emailfield.setText(null);
			phonefield1.setText(null);
			phonefield2.setText(null);
		}
		catch (Exception ex){
			System.out.println("회원가입 실패");
			ex.printStackTrace();
		}
	}

	//취소버튼 
	public void handlecancle(ActionEvent e) {
		pop = (Stage)cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
        pop.close();
	}
}