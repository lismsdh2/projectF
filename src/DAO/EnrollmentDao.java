package DAO;
/*
 * 작성자 : 도현호
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DTO.EnrollmentDto;
import DTO.EnrollmentPopupDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.JdbcUtil;

public class EnrollmentDao {
	
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private JdbcUtil ju;

	//DB연결
	private void connectionJDBC() {
		
		try {
			
			ju = new JdbcUtil();
			connection = ju.getConnection();
			System.out.println("드라이버 로딩 성공 : EnrollentDao");
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("드라이버 로딩 실패 : EnrollentDao");
		}
	}

	//강의신청
	public void submit_Enrollment(EnrollmentPopupDto rCls) throws SQLException{
	
		//DB연결
		connectionJDBC();
	
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		try {
			
			String sql = "insert into request_class values (?,?);";
			pstmt = connection.prepareStatement(sql);
			
			System.out.println(rCls.getStu_id());
			System.out.println(rCls.getClass_no());
			pstmt.setString(1, rCls.getStu_id());			//학생 ID
			pstmt.setInt(2, rCls.getClass_no());			//강의 no
			pstmt.executeUpdate();
			System.out.println("강의 신청 성공");
			//PK, FK 설정으로 학생테이블이나 강의테이블에 값이 없으면 들어가지 않음
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}

	//전체조회
	public ObservableList<EnrollmentDto> enrollment_selectAll() {
		
		//DB연결
		connectionJDBC();
		
		String sql = "select * from class;";
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		
		try {
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
		
			while(rs.next()) {
				
				EnrollmentDto enroll = new EnrollmentDto();
				enroll.setClassno(rs.getInt(1));
				enroll.setClassname(rs.getString(2));
				enroll.setTeachername(rs.getString(3));
				enroll.setTeacherid(rs.getString(4));
				enroll.setClassdescription(rs.getString(5));
				enroll.setStr(rs.getDate(6)+" ~ "+rs.getDate(7));
				enroll.setLimitstudent(rs.getInt(8));
				list.add(enroll);
				System.out.println("전체 강좌 조회 성공");
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("전체 강좌 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	
		return list;
	}
	
	//한 가지만 조회
	public EnrollmentDto enrollment_selectOne(int classno) {
	
		//DB연결
		connectionJDBC();
		
		EnrollmentDto enroll = new EnrollmentDto();
		String sql = "select * from class where class_no = ?";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, classno);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				enroll.setClassno(rs.getInt(1));
				enroll.setClassname(rs.getString(2));
				enroll.setTeachername(rs.getString(3));
				enroll.setTeacherid(rs.getString(4));
				enroll.setClassdescription(rs.getString(5));
				enroll.setStartdate(rs.getDate(6));
				enroll.setEnddate(rs.getDate(7));
				enroll.setLimitstudent(rs.getInt(8));
			}
			System.out.println("강좌 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("강좌 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return enroll;
	}
}