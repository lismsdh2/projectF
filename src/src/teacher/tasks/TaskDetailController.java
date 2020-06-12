package teacher.tasks;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import main.Main_Master_Controller;

/**
 * @author 김지현
 *
 */
public class TaskDetailController extends Main_Master_Controller implements Initializable {

	@FXML
	private AnchorPane taskDetailListPane;

	@FXML
	private TableView<?> tblView;

	@FXML
	private TableColumn<?, ?> tcNo;

	@FXML
	private TableColumn<?, ?> tcName;

	@FXML
	private TableColumn<?, ?> tcSubmitStatus;

	@FXML
	private TableColumn<?, ?> tcSubmitDate;

	@FXML
	private TableColumn<?, ?> tcScore;

	@FXML
	private TableColumn<?, ?> btnDetail;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("TD controller");
	}
}