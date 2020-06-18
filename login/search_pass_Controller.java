package login;

import java.net.URL;
import java.util.ResourceBundle;

import DAO.BasicDao;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import util.Util;

public class search_pass_Controller implements Initializable {

	@FXML
	private TextField namefield;
	@FXML
	private TextField emailfield;
	@FXML
	private TextField idfield;
	@FXML
	private Button done;
	@FXML
	private Button cancle;
	@FXML
	private Button student;
	@FXML
	private Button teacher;
	@FXML
	private ComboBox<String> email2;

	@FXML
	private Label lblIdWarning;
	@FXML
	private Label lblNameWarning;
	@FXML
	private Label lblMailWarning;

	private boolean type;
	private Stage pop;
	BasicDao bDao = new BasicDao();
	Alert alert = new Alert(AlertType.INFORMATION);

	public void initialize(URL location, ResourceBundle resources) {

		done.setOnAction(e -> handledone(e));
		cancle.setOnAction(e -> handlecancle(e));
		student.setOnAction(e -> handlestudent(e));
		teacher.setOnAction(e -> handleteacher(e));

		// textfield warning label
		Util.showWarningLabel(idfield, lblIdWarning);
		Util.showWarningLabel(namefield, lblNameWarning);

		BooleanBinding isEmailEmpty = Bindings.createBooleanBinding(
				// 이메일 텍스트필드 비었거나 / 콤보박스 선택 안되면 true 리턴
				() -> (emailfield.getText().length() == 0 || email2.getValue() == null), emailfield.textProperty(),
				email2.valueProperty());

		lblMailWarning.visibleProperty().bind(isEmailEmpty); // true면 보이고 false면 lblWarning 안보이게

	}

	public void handleteacher(ActionEvent e) {
		System.out.println("교사로 전환");
		type = true;
	}

	public void handlestudent(ActionEvent e) {
		System.out.println("학생으로 전환");
		type = false;
	}

	public void handledone(ActionEvent e) {
		try {

			if (namefield.getText().length() == 0 || emailfield.getText().length() == 0
					|| idfield.getText().length() == 0 || email2.getValue() == null) {
				throw new Exception();
			}

			String id = idfield.getText().trim();
			String name = namefield.getText().trim();
			String email = emailfield.getText().trim() + "@" + (String) email2.getValue();

			String success = bDao.search_pass(id, name, email, this.type);

			System.out.println(email);

			if (success.equals("fail")) {
				alert.setContentText("일치하는 회원 정보가 없습니다.");
				alert.show();
				return;
			}

			pop = (Stage) cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
			pop.close();

		} catch (Exception ex) {
			System.out.println("비밀번호 찾기 실패");
			Util.showAlert("비밀번호 찾기 실패", "비밀번호 찾기를 실패하였습니다.", AlertType.ERROR);
			ex.printStackTrace();
		}
	}

	public void handlecancle(ActionEvent e) {
		pop = (Stage) cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
		pop.close();
	}
}
