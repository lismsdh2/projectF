package login;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.BasicDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class search_pass_Controller implements Initializable {
	
	@FXML private TextField namefield;
	@FXML private TextField emailfield;
	@FXML private TextField idfield;
	@FXML private Button done;
	@FXML private Button cancle; 
	@FXML private Button student;
	@FXML private Button teacher;
	@FXML private ComboBox<String> email2;
	
	private boolean type;
	private Stage pop;
	BasicDao bDao = new BasicDao();
	Alert alert =new Alert(AlertType.INFORMATION);
	
	public void initialize(URL location, ResourceBundle resources) {

		done.setOnAction(e->handledone(e));
		cancle.setOnAction(e->handlecancle(e));
		student.setOnAction(e->handlestudent(e));
		teacher.setOnAction(e->handleteacher(e));
	}

	public void handleteacher(ActionEvent e) {
		System.out.println("교사로 전환");
		type=true;
	}

	public void handlestudent(ActionEvent e) {
		System.out.println("학생으로 전환");
		type=false;
	}

	public void handledone(ActionEvent e) {
		try {
			if(namefield.getText().length()==0) {
				System.out.println("아이디를 입력해주세요");
			}
			
			if(emailfield.getText().length()==0) {
				System.out.println("이메일을 입력해주세요");
			}
			
			if(idfield.getText().length()==0) {
				System.out.println("아이디를 입력해주세요.");
			}
			if(email2.getValue()==null) {
				System.out.println("이메일을 입력해주세요.");
			}
			
			if(namefield.getText().length()==0||emailfield.getText().length()==0||
					idfield.getText().length()==0||email2.getValue()==null) {
				throw new Exception();
			}
			
			String name = namefield.getText();
			String id = idfield.getText();
			String email = emailfield.getText()+"@"+(String)email2.getValue();
			
			String success = bDao.search_pass(id,name,email,this.type);
			
			System.out.println(email);
			
			if(success.equals("fail")) {
				alert.setContentText("일치하는 회원 정보가 없습니다.");
				alert.show();
			}
		
			pop = (Stage)cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
	        pop.close();
	
		
		}
		catch (Exception ex){
			System.out.println("비밀번호 찾기 실패");
			ex.printStackTrace();
		}
	}
	
	public void handlecancle(ActionEvent e) {
		pop = (Stage)cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
        pop.close();
	}
}