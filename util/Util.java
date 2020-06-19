package util;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Popup;
import javafx.stage.Window;

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
	
	public static void waitPopup(String msg, Window window) {
		
		Popup popup = new Popup();
		System.out.println("1");
		try {
//			Parent parent  = FXMLLoader.load(Parent.class.getResource("../fxml/util/waitAlert.fxml"));
			Parent parent  = FXMLLoader.load(Util.class.getResource("../fxml/util/waitAlert.fxml"));
			
		System.out.println("2");
			Label label = (Label) parent.lookup("#lblMessage");
			label.setText(msg);
		System.out.println(label.getText());
		System.out.println("3");
			popup.getContent().add(parent);
			popup.setAutoHide(true);
		System.out.println("4");
			popup.show(window);
		System.out.println("5");
			
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("팝업생성 오류");
		}
	}
	
	public static String fileSizeCheck(File file) {
	
		long restrictSize = 1024 * 1024 * 10;				//10MB
		long fileSize = file.length();
		double size = 0.0;
		
		String sizeUnit = "";								//단위표기
		String strSize = "";								//나고 난 뒤 값
	
		if(fileSize <= restrictSize ) {
			
			if(fileSize >= 1024*1024) {
				sizeUnit = "MB";
				size = ((double)fileSize/(double)(1024*1024));
				strSize = String.format("%,.3f", size) + sizeUnit;
				System.out.println(strSize);
				
			} else {
				sizeUnit = "KB";
				size = ((double)fileSize/(double)(1024));
				strSize = String.format("%,.3f", size) + sizeUnit;
				System.out.println(strSize);
			}
		} else {
			
			strSize = null;
			Util.showAlert("", "10MB 이하만 등록 가능합니다.", AlertType.INFORMATION);
		}
		
		return strSize;
	}

}