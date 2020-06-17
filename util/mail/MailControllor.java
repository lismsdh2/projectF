package util.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
;

public class MailControllor implements Initializable {

	@FXML private TextField receive; 
	@FXML private TextField title;
	@FXML private Button btnFile;
	@FXML private Button btnSend;
	@FXML private HTMLEditor contents;
	private MimeBodyPart messageBodyPart;
	private File filename;
	private static String filePath;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		btnFile.setOnAction(e -> importFile());
		btnSend.setOnAction(e -> mailSend(e));		
	}
	
	
	//메일 보내기
	public void mailSend(ActionEvent e) { 
		
		String host = "smtp.gmail.com"; 						// 네이버일 경우 네이버 계정, gmail경우 gmail 계정 
		String user = "projectfexam@gmail.com";  				// 보내는 사람의 메일 계정
		String username = "과제제출프로그램";					// 보내는 이
		String password = "rhjhilsijzuyzune";   				// 패스워드 - 지메일은 access용 비밀번호 입력해야됨
		String to_user = "";
		
		//세션생성
		//SMTP 서버 정보를 설정한다. 
		Properties props = new Properties(); 
		props.put("mail.smtp.host", host); 
		props.put("mail.smtp.port", 587); 
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");			//지메일 사용시 추가 필요
 
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() { 
					return new PasswordAuthentication(user, password); 
				} 
			}); 
		
		try { 
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user, username));										// 보내는 사람 설정
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receive.getText()));		// 받는 사람 설정			
			message.setSubject(title.getText());														// 메일 제목
			this.contents = new HTMLEditor();															// 메일 내용 
			message.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
			message.setContent("aaaaaaaaaa111", "text/html;charset=utf-8");
		
			Multipart multipart = new MimeMultipart();
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("aaaaaaaaaa", "text/html;charset=utf-8");
			
			if(filePath == null) {
				
			}
			else {
				filename = new File(filePath);
				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(filename.getPath());
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
			}
				
			//메일 내용 보내기 
			Transport.send(message); 
			System.out.println("메일 보내기 성공"); 
		} catch (Exception e1) { e1.printStackTrace(); } 
	}
	//파일 추출 기능
	public String importFile() {
		FileChooser fileChooser = new FileChooser();
		//경로 지정
		fileChooser.setInitialDirectory(new File("C:/"));
		fileChooser.getExtensionFilters().addAll(
				//필터링의 제목 : Txt Files(*.txt), 필터링 형식 : *.txt
				new ExtensionFilter("Txt Files(*.txt)", "*.txt"),
				//필터링의 제목 : Image Files(*.png, *.jpg, *.gif), 필터링 형식 : *.png, *.gif, *jpg
				new ExtensionFilter("Image Files(*.png, *.jpg, *.gif)", "*.png","*.jpg","*.gif"),
				//필터링의 제목 : Audio Files(*.mp3,*.wav,*.aac), 필터링 형식 : *.mp3,*.wav,*.aac
				new ExtensionFilter("Audio Files(*.mp3, *.wav, *.aac)", "*.mp3","*.wav","*.aac"),
				//필터링의 제목 : All Files(*.*), 필터링 형식 : *.*
				new ExtensionFilter("All Files(*.*)", "*.*")				
		);
		filename = fileChooser.showOpenDialog(receive.getScene().getWindow());
		System.out.println("파일 경로 : " + filename.getPath());
		filePath = filename.getPath();
		return filePath;
	}
}