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

public class search_id_Controller implements Initializable {

	@FXML
	private TextField namefield;
	@FXML
	private TextField emailfield;
	@FXML
	private TextField phonefield;
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
	private Label lblNameWarning;
	@FXML
	private Label lblMailWarning;
	@FXML
	private Label lblPhoneWarning;

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
		Util.showWarningLabel(namefield, lblNameWarning);
		Util.showWarningLabel(phonefield, lblPhoneWarning);

		BooleanBinding isEmailEmpty = Bindings.createBooleanBinding(
				// 이메일 텍스트필드 비었거나 / 콤보박스 선택 안되면 true 리턴
				() -> (emailfield.getText().length() == 0 || email2.getValue() == null), emailfield.textProperty(),
				email2.valueProperty());

		lblMailWarning.visibleProperty().bind(isEmailEmpty); // true면 보이고 false면 lblWarning 안보이게

		// 전화번호 필드에 숫자만 오도록 제한
		phonefield.textProperty().addListener(Util.numberOnlyListener(phonefield));

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
			String name = namefield.getText().trim();
			String phone = phonefield.getText().trim();
			String email = emailfield.getText().trim() + "@" + (String) email2.getValue();

			System.out.println(email);

			if (name.length() == 0 || emailfield.getLength() == 0 || phone.length() == 0 || email2.getValue() == null) {
				System.out.println("입력 안 된 칸이 있음.");
				throw new Exception();
			}

			String success = bDao.search_id(name, email, phone, type);

			System.out.println("완료");

			if (success.equals("fail")) {
				alert.setContentText("일치하는 회원 정보가 없습니다.");
				alert.show();
				return;
			}

			pop = (Stage) cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
			pop.close();

		} catch (Exception ex) {
			System.out.println("아이디 찾기 실패");
			Util.showAlert("아이디 찾기 실패", "아이디 찾기를 실패하였습니다.", AlertType.ERROR);
//         ex.printStackTrace();
		}
	}

	// 취소버튼
	public void handlecancle(ActionEvent e) {
		pop = (Stage) cancle.getScene().getWindow(); // 버튼을 통해서 현재 스테이지를 알아냄
		pop.close();
	}
}