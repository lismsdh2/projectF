package util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class Util {

	// 팝업창
	public static void showAlert(String title, String msg, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.show();
	}
	
	//TextInputControl에 숫자만 오도록
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
	
	//TextInputControl에 알파벳과 숫자만 오도록
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
	
	//TextInputControl에 알파벳,숫자,특수문자만 오도록
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

	//TextInputControl에 글자 입력수 제한
	public static ChangeListener<String> textCountLimit(TextInputControl textInputControl, int count){
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.length() > oldValue.length()) {		             
					if(textInputControl.getText().length() > count) {		                
						textInputControl.setText(textInputControl.getText().substring(0, count));
					}
				}
			}
		};
	}

}