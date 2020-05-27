/* 작성자 : 이성원
 * 설명 : 강의클래스와 DB를 연결(DAO)
 */

package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import DTO.ClassDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.JdbcUtil;

public class ClassDao {
	
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private JdbcUtil ju;

	//DB연결
	private void connectionJDBC() {
		
		try {
			
			ju = new JdbcUtil();
			connection = ju.getConnection();
			System.out.println("드라이버 로딩 성공 : ClassDao");
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("드라이버 로딩 실패 : ClassDao");
		}
	
	}

	//강의생성
	public void insertBoard(ClassDto classDto) {
		
		//DB연결
		connectionJDBC();
		
		String sql = "insert into class values(null,?,(select user_name from teacher where user_id=?),?,?,?,?,?);";
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, classDto.getClassName());
			pstmt.setString(2, classDto.getTeacherId());
			pstmt.setString(3, classDto.getTeacherId());
			pstmt.setString(4, classDto.getClassDescription());
			pstmt.setObject(5, classDto.getStartDate());
			pstmt.setObject(6, classDto.getEndDate());
			pstmt.setInt(7, classDto.getLimitStudent());
			pstmt.executeUpdate();
			System.out.println("데이터 입력 성공");
		} catch (SQLException e) {
			System.out.println("데이터 입력 실패");
			e.printStackTrace();
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}

	// 강의 삭제
	public void deleteClass(int classno) {
		
		//DB연결
		connectionJDBC();
		
		String sql = "delete from class where class_no = ?;";
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, classno);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}

	// 강의 정보수정
	public void updateClass(String classname, String description, LocalDate startdate, LocalDate enddate,
			int limitstudent, int classno) {
		//DB연결
		connectionJDBC();
		
		String sql = "update class set class_name=?, class_desc=?, start_date=?, end_date=?, limitstudent=? where class_no = ?;";
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, classname);
			pstmt.setString(2, description);
			pstmt.setObject(3, startdate);
			pstmt.setObject(4, enddate);
			pstmt.setInt(5, limitstudent);
			pstmt.setInt(6, classno);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}

	// 강의 하나만 가져오기
	public ClassDto selectClassOne(int classno) {
		
		//DB연결
		connectionJDBC();
		
		String sql = "select * from class where class_no=?;";
		ClassDto classDto = new ClassDto();
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, classno);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				classDto.setClassNo(rs.getInt("class_no"));
				classDto.setClassName(rs.getString("class_name"));
				classDto.setTeacherName(rs.getString("teacher_name"));
				classDto.setTeacherId(rs.getString("teacher_id"));
				classDto.setClassDescription(rs.getString("class_desc"));
				classDto.setStartDate(LocalDate.parse(rs.getString("start_Date"), DateTimeFormatter.ISO_DATE));
				classDto.setEndDate(LocalDate.parse(rs.getString("end_Date"), DateTimeFormatter.ISO_DATE));
				classDto.setLimitStudent(rs.getInt("limitstudent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return classDto;
	}

	// 전체강의 조회
	public ObservableList<ClassDto> selectClassList() {
		//DB연결
		connectionJDBC();
		
		ObservableList<ClassDto> list = FXCollections.observableArrayList();
		try {
			String sql = "select * from class;";
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ClassDto classDto = new ClassDto();
				classDto.setClassNo(rs.getInt("class_no"));
				classDto.setClassName(rs.getString("class_name"));
				classDto.setTeacherName(rs.getString("teacher_name"));
				classDto.setTeacherId(rs.getString("teacher_id"));
				classDto.setClassDescription(rs.getString("class_desc"));
				classDto.setStartDate(LocalDate.parse(rs.getString("start_Date"), DateTimeFormatter.ISO_DATE));
				classDto.setEndDate(LocalDate.parse(rs.getString("end_Date"), DateTimeFormatter.ISO_DATE));
				classDto.setLimitStudent(rs.getInt("limitstudent"));
				list.add(classDto);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}

	// 본인강의만 조회(id로 조회)
	public ObservableList<ClassDto> selectMyClassList(String teacherid) {
		//DB연결
		connectionJDBC();
		
		ObservableList<ClassDto> list = FXCollections.observableArrayList();
		try {
			String sql = "select * from class where teacher_id=?;";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, teacherid);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ClassDto class1 = new ClassDto();

				class1.setClassNo(rs.getInt("class_no"));
				class1.setClassName(rs.getString("class_name"));
				class1.setTeacherName(rs.getString("teacher_name"));
				class1.setTeacherId(rs.getString("teacher_id"));
				class1.setClassDescription(rs.getString("class_desc"));
				class1.setStartDate(LocalDate.parse(rs.getString("start_date"), DateTimeFormatter.ISO_DATE));
				class1.setEndDate(LocalDate.parse(rs.getString("end_date"), DateTimeFormatter.ISO_DATE));
				class1.setLimitStudent(rs.getInt("limitstudent"));

				list.add(class1);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
	
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}

	// 강의검색하기(강의명 or 선생님명)
	public ObservableList<ClassDto> searchClassList(String str) {
		//DB연결
		connectionJDBC();
		
		ObservableList<ClassDto> list = FXCollections.observableArrayList();
		
		try {
			String sql = "select * from class where class_name like ? or teacher_name like ?;";
			pstmt = connection.prepareStatement(sql);
			String temp = "%" + str + "%";
			pstmt.setString(1, temp);
			pstmt.setString(2, temp);
			ResultSet re = pstmt.executeQuery();

			while (re.next()) {
				ClassDto classDto = new ClassDto();

				classDto.setClassNo(re.getInt("class_no"));
				classDto.setClassName(re.getString("class_name"));
				classDto.setTeacherName(re.getString("teacher_name"));
				classDto.setTeacherId(re.getString("teacher_id"));
				classDto.setClassDescription(re.getString("class_desc"));
				classDto.setStartDate(LocalDate.parse(re.getString("start_date"), DateTimeFormatter.ISO_DATE));
				classDto.setEndDate(LocalDate.parse(re.getString("end_date"), DateTimeFormatter.ISO_DATE));
				classDto.setLimitStudent(re.getInt("limitstudent"));

				list.add(classDto);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
}