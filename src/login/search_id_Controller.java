package login;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class search_id_Controller implements Initializable {
	
	@FXML private TextField namefield;
	@FXML private TextField emailfield;
	@FXML private TextField phonefield;
	@FXML private Button done;
	@FXML private Button cancle; 
	@FXML private Button student;
	@FXML private Button teacher;
	@FXML private ComboBox email2;
	
	private boolean type;
	private Stage pop;
	UserDao bDao = new UserDao();
	
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
			if(phonefield.getText().length()==0) {
				System.out.println("전화번호를 입력해주세요.");
			}
			if(email2.getValue()==null) {
				System.out.println("이메일을 입력해주세요.");
			}
			String name = namefield.getText();
			String phone = phonefield.getText();
			String email = emailfield.getText()+"@"+(String)email2.getValue();
			
			System.out.println(email);
			
			if(name.length()==0||emailfield.getLength()==0||phone.length()==0||email2.getValue()==null) {
			    
				System.out.println("입력 안 된 칸이 있음.");
				throw new Exception();
			}
			
			bDao.search_id(name,email,phone,type);
			
			System.out.println("완료");
		
		    namefield.setText(null);
		    emailfield.setText(null);
		    phonefield.setText(null);
	
		
		}
		catch (Exception ex){
			System.out.println("아이디 찾기 실패");
			ex.printStackTrace();
		}
	}

	//취소버튼
	public void handlecancle(ActionEvent e) {
		pop = (Stage)cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
        pop.close();
	}
}
