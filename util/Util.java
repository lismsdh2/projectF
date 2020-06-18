package util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputControl;

public class Util {

	// 팝업창
	public static Alert showAlert(String title, String msg, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		if (type == AlertType.ERROR) {
			alert.showAndWait();
		}
		return alert;
	}

	// TextInputControl에 숫자만 오도록
	public static ChangeListener<String> numberOnlyListener(TextInputControl textInputControl) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					textInputControl.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		};
	}

	// TextInputControl에 알파벳과 숫자만 오도록
	public static ChangeListener<String> alphabetListener(TextInputControl textInputControl) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!(newValue.matches("\\d*") || newValue.matches("[a-z]") || newValue.matches("[A-Z]"))) {
					textInputControl.setText(newValue.replaceAll("[^\\d||a-z||A-Z]", ""));
				}
			}
		};
	}

	// TextInputControl에 알파벳,숫자,특수문자만 오도록
	public static ChangeListener<String> pwListener(TextInputControl textInputControl) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[!-z]")) {
					textInputControl.setText(newValue.replaceAll("[^!-z]", ""));
				}
			}
		};
	}

	// TextInputControl에 글자 입력수 제한
	public static ChangeListener<String> textCountLimit(TextInputControl textInputControl, int count) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() > oldValue.length()) {
					if (textInputControl.getText().length() > count) {
						textInputControl.setText(textInputControl.getText().substring(0, count));
					}
				}
			}
		};
	}

	// txtInputControl에 입력되는 글자 수가 label에 표시되고, 글자수 제한은 length만큼
	public static void textLengthLimit(TextInputControl txtInputControl, Label lbl, int length) {
		lbl.setText("( 0 / "+length+" )");
		
		txtInputControl.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				lbl.setText("( " + newValue.length() + " / " + length + " )");
				if (newValue.length() > length) {
					txtInputControl.setText(oldValue);
				}
			}
		});
	}
	
	// textfield 미입력 시 warning label 보이기
	public static void showWarningLabel(TextField txtfield, Label lbl) {

		txtfield.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() == 0) {
					lbl.setVisible(true);
				} else {
					lbl.setVisible(false);
				}
			}
		});
	}
}