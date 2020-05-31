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
	
		try {
			
			String sql = "insert into request_class values (?,?);";
			pstmt = connection.prepareStatement(sql);
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
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select * from class;";
		
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
			}
			System.out.println("수강신청 전체 강좌 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("수간신청 전체 강좌 조회 실패");
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
	
	//현재 내 강좌 리스트 조회
	public ObservableList<EnrollmentDto> current_selectAll(String stu_id) {
	
		//DB연결
		connectionJDBC();
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select rc.class_no, c.class_name, c.start_date, c.end_date, c.teacher_name, c.limitstudent"
					+" from request_class rc"
					+" inner join class c"
					+" on rc.class_no = c.class_no"
					+" where rc.student_id = ?"
					+" and c.end_date >= curdate();";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				EnrollmentDto enroll = new EnrollmentDto();					//DTO객체가 밖에 있는 경우 출력값이 통일됨
				enroll.setClassno(rs.getInt(1));							//강의번호
				enroll.setClassname(rs.getString(2));						//강의명
				enroll.setStartdate(rs.getDate(3));							//강의 시작일
				enroll.setEnddate(rs.getDate(4));							//강의 마지막일
				enroll.setStr(rs.getDate(3)+" ~ "+rs.getDate(4));			//강의 기간
				enroll.setTeachername(rs.getString(5));						//강사명
				enroll.setLimitstudent(rs.getInt(6));						//수강가능인원
				enroll.setStatus("진행 중");
				
				list.add(enroll);
			}
			System.out.println("현재 수강 중인 강좌 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("현재 수강 중인 강좌 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	
	//지난 내 강좌 리스트 조회
	public ObservableList<EnrollmentDto> past_selectAll(String stu_id) {
	
		//DB연결
		connectionJDBC();
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select rc.class_no, c.class_name, c.start_date, c.end_date, c.teacher_name, c.limitstudent"
					+" from request_class rc"
					+" inner join class c"
					+" on rc.class_no = c.class_no"
					+" where rc.student_id = ?"
					+" and c.end_date < curdate();";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				EnrollmentDto enroll = new EnrollmentDto();					//DTO객체가 밖에 있는 경우 출력값이 통일됨
				enroll.setClassno(rs.getInt(1));							//강의번호
				enroll.setClassname(rs.getString(2));						//강의명
				enroll.setStartdate(rs.getDate(3));							//강의 시작일
				enroll.setEnddate(rs.getDate(4));							//강의 마지막일
				enroll.setStr(rs.getDate(3)+" ~ "+rs.getDate(4));			//강의 기간
				enroll.setTeachername(rs.getString(5));						//강사명
				enroll.setLimitstudent(rs.getInt(6));						//수강가능인원
				enroll.setStatus("강의 종료");
				
				list.add(enroll);
			}
			System.out.println("지난 강좌 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("지난 강좌 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	
	//검색(강의 또는 강사명)
	public ObservableList<EnrollmentDto> search_selectAll(String search_word){
	
		//DB연결
		connectionJDBC();
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select * from class"
				   + " where class_name like ?"
				   + "    or  teacher_name like ?;";
		String like_word = "%"+search_word+"%";
	
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, like_word);
			pstmt.setString(2, like_word);
			rs = pstmt.executeQuery();
		
			while(rs.next()) {
				
				EnrollmentDto enroll = new EnrollmentDto();			//DTO객체가 밖에 있는 경우 출력값이 통일됨
				enroll.setClassno(rs.getInt(1));					//강의번호
				enroll.setClassname(rs.getString(2));				//강의명
				enroll.setTeachername(rs.getString(3));				//강사명
				enroll.setTeacherid(rs.getString(4));				//강사ID
				enroll.setClassdescription(rs.getString(5));		//수업설명
				enroll.setStr(rs.getDate(6)+" ~ "+rs.getDate(7));	//강의기간
				enroll.setLimitstudent(rs.getInt(8));				//수업인원
				list.add(enroll);
			}
			System.out.println("수강신청 전체 강좌 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("수간신청 전체 강좌 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		
		return list;
	}
	
	//검색(강의 또는 강사명)_현재강의 페이지
	public ObservableList<EnrollmentDto> search_current_selectAll(String stu_id, String search_word){
	
		//DB연결
		connectionJDBC();
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select rc.class_no, c.class_name, c.start_date, c.end_date, c.teacher_name, c.limitstudent"
				   + "  from request_class rc"
				   + " inner join class c"
				   + "    on rc.class_no = c.class_no"
				   + " where (rc.student_id = ?"
				   + "   and c.end_date >= curdate())"
				   + "   and (c.class_name like ?"
				   + "    or c.teacher_name like ?)";
		String like_word = "%"+search_word+"%";
	
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			pstmt.setString(2, like_word);
			pstmt.setString(3, like_word);
			rs = pstmt.executeQuery();
		
			while(rs.next()) {
				
				EnrollmentDto enroll = new EnrollmentDto();					//DTO객체가 밖에 있는 경우 출력값이 통일됨
				enroll.setClassno(rs.getInt(1));							//강의번호
				enroll.setClassname(rs.getString(2));						//강의명
				enroll.setStartdate(rs.getDate(3));							//강의 시작일
				enroll.setEnddate(rs.getDate(4));							//강의 마지막일
				enroll.setStr(rs.getDate(3)+" ~ "+rs.getDate(4));			//강의 기간
				enroll.setTeachername(rs.getString(5));						//강사명
				enroll.setLimitstudent(rs.getInt(6));						//수강가능인원
				enroll.setStatus("진행 중");
				
				list.add(enroll);
			}
			System.out.println("현재강좌 전체 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("현재강좌 전체 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		
		return list;
	}
	
	//검색(강의 또는 강사명)_지난강의 페이지
	public ObservableList<EnrollmentDto> search_past_selectAll(String stu_id, String search_word){
	
		//DB연결
		connectionJDBC();
		
		ObservableList<EnrollmentDto> list = FXCollections.observableArrayList();
		String sql = "select rc.class_no, c.class_name, c.start_date, c.end_date, c.teacher_name, c.limitstudent"
				   + "  from request_class rc"
				   + " inner join class c"
				   + "    on rc.class_no = c.class_no"
				   + " where (rc.student_id = ?"
				   + "   and c.end_date < curdate())"
				   + "   and (c.class_name like ?"
				   + "    or c.teacher_name like ?)";
		String like_word = "%"+search_word+"%";
	
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			pstmt.setString(2, like_word);
			pstmt.setString(3, like_word);
			rs = pstmt.executeQuery();
		
			while(rs.next()) {
				
				EnrollmentDto enroll = new EnrollmentDto();					//DTO객체가 밖에 있는 경우 출력값이 통일됨
				enroll.setClassno(rs.getInt(1));							//강의번호
				enroll.setClassname(rs.getString(2));						//강의명
				enroll.setStartdate(rs.getDate(3));							//강의 시작일
				enroll.setEnddate(rs.getDate(4));							//강의 마지막일
				enroll.setStr(rs.getDate(3)+" ~ "+rs.getDate(4));			//강의 기간
				enroll.setTeachername(rs.getString(5));						//강사명
				enroll.setLimitstudent(rs.getInt(6));						//수강가능인원
				enroll.setStatus("진행 중");
				
				list.add(enroll);
			}
			System.out.println("지난강좌 전체 조회 성공");
		} catch (SQLException e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("지난강좌 전체 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		
		return list;
	}
}