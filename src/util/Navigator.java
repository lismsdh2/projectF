package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import main.Main_Master_Controller;

/**
 * @author 김지현
 */
public class Navigator {

	public static final String TEACHER_CLASS_LIST = "../fxml/teacher/classes/teacher_main.fxml";
	public static final String TEACHER_TASK_LIST = "../fxml/teacher/tasks/TaskList.fxml";
	public static final String STUDENT_CLASS_LIST = "../fxml/student/enrollment/enrollment.fxml";
	public static final String STUDENT_TASK_LIST = "../fxml/student/assignment/assignment.fxml";

	public static void loadPages(String fxml) {
		try {
			Node node = FXMLLoader.load(Navigator.class.getResource(fxml));
			Main_Master_Controller.mmc.setPage(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}