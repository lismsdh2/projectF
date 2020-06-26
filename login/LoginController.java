package login;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.BasicDao;
import DTO.BasicDto;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import launch.AppMain;

public class LoginController implements Initializable {

	@FXML private AnchorPane anchorPane;
	@FXML private TextField id;
	@FXML private PasswordField password;
	@FXML private Button idsearch;
	@FXML private Button passsearch;
	@FXML private Button signup;
	@FXML private Button login;
	@FXML private Button student;
	@FXML private Button teacher;
	private boolean type;
	BasicDao bDao = new BasicDao();
	
	public void initialize(URL location, ResourceBundle resources) {
	
		handleStudent();
		login.setOnAction(e -> handleLogin(e));
		signup.setOnAction(e->popupWindow(e));
		student.setOnAction(e->handleStudent());
		teacher.setOnAction(e->handleTeacher());
		idsearch.setOnAction(e->handleidsearch(e));
		passsearch.setOnAction(e->handlepasssearch(e));
		
		id.setOnKeyPressed(e->{
			
			if(e.getCode() == KeyCode.F2) {				//F2 학생선택
				handleStudent();
			}else if(e.getCode() == KeyCode.F3) {		//F3 교사선택
				handleTeacher();
			}else if(e.getCode() == KeyCode.ENTER) {	//로그인 시 엔터 효과
				handleLogin(e);
			}
		});
		
		password.setOnKeyPressed(e->{
			
			if(e.getCode() == KeyCode.F2) {				//F2 학생선택
				handleStudent();
			}else if(e.getCode() == KeyCode.F3) {		//F3 교사선택
				handleTeacher();
			}else if(e.getCode() == KeyCode.ENTER) {	//로그인 시 엔터 효과
				handleLogin(e);
			}
		});
		
	}

	//교사버튼
	public void handleTeacher() {
		this.type=true;
		System.out.println("교사로 전환");
	}

	//학생버튼
	public void handleStudent() {
		this.type=false;
		System.out.println("학생으로 전환");
	}
	
	//로그인버튼
	public void handleLogin(Event event) {

		String loginid = id.getText();
		String loginpas = password.getText();
		boolean logintype = type;

		BasicDto user = bDao.selectOne(new BasicDto(loginid, loginpas, logintype));

		if (user == null) {
				Alert alert =new Alert(AlertType.INFORMATION);
				alert.setContentText("로그인 실패: 아이디 혹은 비밀번호가 틀립니다.");
			    alert.show();
			return;
		} 
		else { // user != null
			// 로그인한 유저정보 넘겨주기
			AppMain.app.setBasic(user);
			Parent root = null;
			Stage stage = new Stage();

			if (user.gettype2()) { // 선생님일때 main_teacher.fxml
				try {
//					root = FXMLLoader.load(getClass().getResource("../fxml/main/main_teacher.fxml"));
					root = FXMLLoader.load(Class.forName("login.LoginController").getResource("/fxml/main/main_teacher.fxml"));
					stage.setTitle("teacher_main");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else { // 학생일때 main_student.fxml
				try {
//					root = FXMLLoader.load(getClass().getResource("../fxml/main/main_student.fxml"));
					root = FXMLLoader.load(Class.forName("login.LoginController").getResource("/fxml/main/main_student.fxml"));
					stage.setTitle("student_main");
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}

			try {
				root.getStylesheets().add(Class.forName("login.LoginController").getResource("/css/main/main.css").toExternalForm());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.show();

			// 로그인창 숨기기
			((Node) (event.getSource())).getScene().getWindow().hide();
		}
	}
	
	public void popupWindow(ActionEvent event) {

	      try {
//	         Parent parent = FXMLLoader.load(getClass().getResource("../fxml/login/signup.fxml"));
	         Parent parent = FXMLLoader.load(Class.forName("login.LoginController").getResource("/fxml/login/signup.fxml"));
	         Scene scene = new Scene(parent);
	         Stage stage = new Stage();
	         stage.setScene(scene);
	         stage.show();
	         
	         System.out.println("팝업호출 성공");
	      } catch (Exception e) {
	         
	    	  System.out.println(e.getMessage());
	    	  System.out.println("팝업호출 실패");
	      }
	   }
	
	public void handleidsearch(ActionEvent e) {
		try {
//	         Parent parent = FXMLLoader.load(getClass().getResource("../fxml/login/search_id.fxml"));
	         Parent parent = FXMLLoader.load(Class.forName("login.LoginController").getResource("/fxml/login/search_id.fxml"));
	         Scene scene = new Scene(parent);
	         Stage stage = new Stage();
	         stage.setScene(scene);
	         stage.show();
	         
	         System.out.println("아이디 찾기 호출");
	      } catch (Exception e1) {
	         
	    	  System.out.println(e1.getMessage());
	    	  System.out.println("아이디 찾기 호출 실패");
	      }
		
	}
	public void handlepasssearch(ActionEvent e) {
		
		try {
//			Parent parent = FXMLLoader.load(getClass().getResource("../fxml/login/search_password.fxml"));
	        Parent parent = FXMLLoader.load(Class.forName("login.LoginController").getResource("/fxml/login/search_password.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();

			System.out.println("비밀번호 찾기 호출");
		} catch (Exception e1) {

			System.out.println(e1.getMessage());
			System.out.println("비밀번호 찾기 호출 실패");
		}
	}
}