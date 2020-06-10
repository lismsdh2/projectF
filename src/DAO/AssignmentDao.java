package DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DTO.AssignmentDto;
import DTO.AssignmentPopupDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.JdbcUtil;

public class AssignmentDao {
	
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private JdbcUtil ju;

	//DB연결
	private void connectionJDBC() {
		
		try {
			
			ju = new JdbcUtil();
			connection = ju.getConnection();
			System.out.println("드라이버 로딩 성공 : AssignmentDao");
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("드라이버 로딩 실패 : AssignmentDao");
		}
	}

	//과제 제출
	public void submit_Assignment(AssignmentPopupDto sTask) throws SQLException{
	
		//DB연결
		connectionJDBC();
	
		try {
			//강의번호, 과제번호, 학생ID, 과제제출여부, 과제제출일시, 문의사항, 과제파일, 과제파일명
			String sql = "insert into submission_task("
					+ "class_no,task_no, student_id, tasksubmit, tasksubmit_date,"
					+ "taskquestion, taskfile, taskfile_name)"
					+ "values (?, ?, ?, 'Y' ,sysdate(),? ,?, ?);";
			pstmt = connection.prepareStatement(sql);
			System.out.println(sTask.getClass_no());
			System.out.println(sTask.getTask_no());
			System.out.println(sTask.getStudent_id());
			System.out.println(sTask.getTaskQuestion());
			System.out.println(sTask.getTaskFile());
			System.out.println(sTask.getTaskFile_name());

			pstmt.setInt(1, sTask.getClass_no());
			pstmt.setInt(2, sTask.getTask_no());
			pstmt.setString(3, sTask.getStudent_id());
			pstmt.setString(4, sTask.getTaskQuestion());
			pstmt.setBytes(5, sTask.getTaskFile());
			pstmt.setString(6, sTask.getTaskFile_name());
			pstmt.executeUpdate();
			System.out.println("과제 제출 성공");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}
	
	//과제 재 제출
	public void resubmit_Assignment(AssignmentPopupDto sTask) throws SQLException{
	
		//DB연결
		connectionJDBC();

		try {
			//과제제출일시, 문의사항, 과제파일, 과제파일명, 과제번호, 학생ID
			String sql = "update submission_task"
							+ " set tasksubmit_date = sysdate(),"
							+ " taskquestion = ?, "
							+ " taskfile = ?, "
							+ " taskfile_name=? "
							+ " where class_no = ? and task_no = ? and student_id = ?;";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, sTask.getTaskQuestion());
			pstmt.setBytes(2, sTask.getTaskFile());
			pstmt.setString(3, sTask.getTaskFile_name());
			pstmt.setInt(4, sTask.getClass_no());
			pstmt.setInt(5, sTask.getTask_no());
			pstmt.setString(6, sTask.getStudent_id());
			pstmt.executeUpdate();
			System.out.println("과제 제출 성공");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
	}
	
	//과제 조회-현재
	public ObservableList<AssignmentDto> assignment_selectAll(String stu_id){
		
		//DB연결
		connectionJDBC();
	
		ObservableList<AssignmentDto> list = FXCollections.observableArrayList();
		String sql = "select t.task_no, t.task_name, ts.tasksubmit, ts.tasksubmit_date, t.expire_date, ts.taskscore, t.perfect_score, c.class_name, c.class_no"
						+ " from task t"
						+ " inner join class c"
						+ " on c.class_no = t.class_no"
						+ " left outer join submission_task ts"
						+ " on t.task_no = ts.task_no"
						+ " and ts.student_id = ?"
						+ " where t.expire_date >= curdate()"
						+ " order by t.expire_date, t.class_no;";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
			int i = 0;										//연번을 나타내기 위한 변수
		
			while(rs.next()) {
				i++;
				AssignmentDto assign = new AssignmentDto();			//DTO객체가 밖에 있는 경우 출력값이 통일됨
				//제출여부 표시(null이거나 N 이면 N표시, 아니면 Y표시)
				String yorn = "";
				if(rs.getString(3)==null ||rs.getString(3).equals("N")) {
					yorn = "N";
				} else {
					yorn = "Y";
				}
				
				assign.setTaskList_no(i);
				assign.setTask_no(rs.getInt(1));
				assign.setTask_name(rs.getString(2));
				assign.setSubmitornot(yorn);
				assign.setReg_date(rs.getDate(4));
				assign.setExpire_date(rs.getDate(5));
				if(rs.getInt(6)>=0) {								//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setScore(rs.getInt(6)+" / "+rs.getInt(7));//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
				} else {
					assign.setScore(0+" / "+rs.getInt(7));
				}
				assign.setClass_name(rs.getString(8));
				assign.setClass_no(rs.getInt(9));
				list.add(assign);
			}
			System.out.println("전체 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("전체 과제 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	//강의별 과제 조회-현재
	public ObservableList<AssignmentDto> assignment_selectAll(int class_no, String stu_id){
		
		//DB연결
		connectionJDBC();
	
		ObservableList<AssignmentDto> list = FXCollections.observableArrayList();
		String sql = "select t.task_no, t.task_name, ts.tasksubmit, ts.tasksubmit_date, t.expire_date, ts.taskscore, t.perfect_score, c.class_name, c.class_no"
						+ " from task t"
						+ " inner join class c"
						+ " on c.class_no = t.class_no"
						+ " left outer join submission_task ts"
						+ " on t.task_no = ts.task_no"
						+ " and ts.student_id = ?"
						+"where t.class_no = ?"
						+"  and t.expire_date >= curdate();";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			pstmt.setInt(2, class_no);
			rs = pstmt.executeQuery();
			int i = 0;										//연번을 나타내기 위한 변수
		
			while(rs.next()) {
				i++;
				AssignmentDto assign = new AssignmentDto();			//DTO객체가 밖에 있는 경우 출력값이 통일됨
				//제출여부 표시(null이거나 N 이면 N표시, 아니면 Y표시)
				String yorn = "";
				if(rs.getString(3)==null ||rs.getString(3).equals("N")) {
					yorn = "N";
				} else {
					yorn = "Y";
				}
				
				assign.setTaskList_no(i);
				assign.setTask_no(rs.getInt(1));
				assign.setTask_name(rs.getString(2));
				assign.setSubmitornot(yorn);
				assign.setReg_date(rs.getDate(4));
				assign.setExpire_date(rs.getDate(5));
				if(rs.getInt(6)>=0) {								//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setScore(rs.getInt(6)+" / "+rs.getInt(7));//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
				} else {
					assign.setScore(0+" / "+rs.getInt(7));
				}
				assign.setClass_name(rs.getString(8));
				assign.setClass_no(rs.getInt(9));
				list.add(assign);
			}
			System.out.println("전체 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("전체 과제 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	
	//과제 조회-전체
	public ObservableList<AssignmentDto> assignment_selectAll_Full(String stu_id){
		
		//DB연결
		connectionJDBC();
	
		ObservableList<AssignmentDto> list = FXCollections.observableArrayList();
		String sql = "select t.task_no, t.task_name, ts.tasksubmit, ts.tasksubmit_date, t.expire_date, ts.taskscore, t.perfect_score, c.class_name"
						+ " from task t"
						+ " inner join class c"
						+ " on c.class_no = t.class_no"
						+ " left outer join submission_task ts"
						+ " on t.task_no = ts.task_no"
						+ " and ts.student_id = ?"
						+ " order by t.expire_date asc, t.class_no asc;";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
			int i = 0;										//연번을 나타내기 위한 변수
		
			while(rs.next()) {
				i++;
				AssignmentDto assign = new AssignmentDto();			//DTO객체가 밖에 있는 경우 출력값이 통일됨
				//제출여부 표시(null이거나 N 이면 N표시, 아니면 Y표시)
				String yorn = "";
				if(rs.getString(3)==null ||rs.getString(3).equals("N")) {
					yorn = "N";
				} else {
					yorn = "Y";
				}
				
				assign.setTaskList_no(i);
				assign.setTask_no(rs.getInt(1));
				assign.setTask_name(rs.getString(2));
				assign.setSubmitornot(yorn);
				assign.setReg_date(rs.getDate(4));
				assign.setExpire_date(rs.getDate(5));
				if(rs.getInt(6)>=0) {								//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setScore(rs.getInt(6)+" / "+rs.getInt(7));//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
				} else {
					assign.setScore(0+" / "+rs.getInt(7));
				}
				assign.setClass_name(rs.getString(8));
				list.add(assign);
			}
			System.out.println("전체 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("전체 과제 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	//강의별 과제 조회-현재
	public ObservableList<AssignmentDto> assignment_selectAll_Full(int class_no, String stu_id){
		
		//DB연결
		connectionJDBC();
	
		ObservableList<AssignmentDto> list = FXCollections.observableArrayList();
		String sql = "select t.task_no, t.task_name, ts.tasksubmit, ts.tasksubmit_date, t.expire_date, ts.taskscore, t.perfect_score, c.class_name"
						+ " from task t"
						+ " inner join class c"
						+ " on c.class_no = t.class_no"
						+ " left outer join submission_task ts"
						+ " on t.task_no = ts.task_no"
						+ " and ts.student_id = ?"
						+"where t.class_no = ?;";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			pstmt.setInt(2, class_no);
			rs = pstmt.executeQuery();
			int i = 0;										//연번을 나타내기 위한 변수
		
			while(rs.next()) {
				i++;
				AssignmentDto assign = new AssignmentDto();			//DTO객체가 밖에 있는 경우 출력값이 통일됨
				//제출여부 표시(null이거나 N 이면 N표시, 아니면 Y표시)
				String yorn = "";
				if(rs.getString(3)==null ||rs.getString(3).equals("N")) {
					yorn = "N";
				} else {
					yorn = "Y";
				}
				
				assign.setTaskList_no(i);
				assign.setTask_no(rs.getInt(1));
				assign.setTask_name(rs.getString(2));
				assign.setSubmitornot(yorn);
				assign.setReg_date(rs.getDate(4));
				assign.setExpire_date(rs.getDate(5));
				if(rs.getInt(6)>=0) {								//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setScore(rs.getInt(6)+" / "+rs.getInt(7));//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
				} else {
					assign.setScore(0+" / "+rs.getInt(7));
				}
				assign.setClass_name(rs.getString(8));
				list.add(assign);
			}
			System.out.println("전체 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("전체 과제 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	
	//제출한 적이 있는 학생 데이터 조회
	public AssignmentDto assignment_selectOne(int task_no, String stu_id) {
	
		//DB연결
		connectionJDBC();
		
		AssignmentDto assign = new AssignmentDto();
		//과제번호, 과제명, 과제설명, 과제 나의점수, 과제 만점점수, 제출기한, 첨부파일, 첨부파일명, 문의사항, 답변, 제출파일
		String sql = "select t.task_no, t.task_name, t.task_desc, st.taskscore, t.perfect_score, t.expire_date, t.attachedfile, t.attachedfile_name, st.taskquestion, st.taskanswer, st.taskfile, st.taskfile_name"
				  	 + " from task t"
				  	 + " left outer join submission_task st"
				     + " on t.task_no = st.task_no"
				     + " where t.task_no = ?"
				     + " and st.student_id = ?;";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, task_no);
			pstmt.setString(2, stu_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//1)t.task_no, 2)t.task_name, 3)t.task_desc, 4)ts.taskscore, 5)t.perfect_score, 6)t.expire_date,
				//7)t.attachedfile, 8)t.attachedfile_name, 9)ts.taskquestion, 10)ts.taskanswer, 11)ts.taskfile, 12) ts.taskfile_name
				assign.setTask_name(rs.getString(2));					// 과제명
				assign.setTask_desc(rs.getString(3));					// 과제설명

				if(rs.getInt(4)>=0) {									//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setCheckTask(true);							//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
					assign.setMyScore(rs.getInt(4));
				} else {
					assign.setCheckTask(false);
					assign.setMyScore(0);
				}
				assign.setPerfect_score(rs.getInt(5));					// 만점
				assign.setScore(rs.getInt(4)+" / "+rs.getInt(5));		// 과제 점수 / 만점
				assign.setExpire_date(rs.getDate(6));					// 제출기한
				assign.setAttachedFile(rs.getBinaryStream(11));			// 과제 참고 파일		-- 추후 7로 변경해야됨
				assign.setAttachedFile_name(rs.getString(12));			// 과제 참고 파일명		-- 추후 8로 변경해야됨
				assign.setTaskQuestion(rs.getString(9));				// 문의사항
				assign.setTaskAnswer(rs.getString(10));					// 답변
				assign.setTaskFile(rs.getBytes(11));					// 과제제출파일
				assign.setTaskFile_name(rs.getString(12));				// 과제제출파일명
			}
			System.out.println("과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("과제 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return assign;
	}

	//제출한적 없는 학생이 과제 조회할 때
	public AssignmentDto assignment_selectOne(int class_no, int task_no) {
	
		//DB연결
		connectionJDBC();
		
		AssignmentDto assign = new AssignmentDto();
		ResultSet rs = null;
		//과제번호, 과제명, 과제설명, 과제 나의점수, 과제 만점점수, 제출기한, 첨부파일, 첨부파일명, 문의사항, 답변, 제출파일
		String sql = "select t.task_no, t.task_name, t.task_desc, st.taskscore, t.perfect_score, t.expire_date, t.attachedfile, t.attachedfile_name, st.taskquestion, st.taskanswer, st.taskfile"
				  	 + " from task t"
				  	 + " left outer join submission_task st"
				     + " on t.task_no = st.task_no"
				     + " where t.task_no = ?"
				     + " and t.class_no = ?";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, task_no);
			pstmt.setInt(2, class_no);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//1)t.task_no, 2)t.task_name, 3)t.task_desc, 4)st.taskscore, 5)t.perfect_score,
				//6)t.expire_date, 7)t.attachedfile, 8)t.attachedfile_name, 9)st.taskquestion, 10)st.taskanswer, 11)st.taskfile
				assign.setTask_name(rs.getString(2));					// 과제명
				assign.setTask_desc(rs.getString(3));					// 과제설명
				if(rs.getInt(4)>=0) {									//내 점수를 확인해서 -1이면 점수책정여부(CheckTask값) false, 0이상이면 true
					assign.setCheckTask(true);							//그리고 -1이면 점수를 0점으로 변환하여 화면에 출력
					assign.setMyScore(rs.getInt(4));
				} else {
					assign.setCheckTask(false);
					assign.setMyScore(0);
				}
				assign.setPerfect_score(rs.getInt(5));					// 만점
				assign.setScore(rs.getInt(4)+" / "+rs.getInt(5));		// 과제 점수 / 만점
				assign.setExpire_date(rs.getDate(6));					// 제출기한
				assign.setAttachedFile(rs.getBinaryStream(7));			// 과제 참고 파일
				assign.setAttachedFile_name(rs.getString(8));			// 과제 참고 파일명
				assign.setTaskQuestion(rs.getString(9));				// 문의사항
				assign.setTaskAnswer(rs.getString(10));					// 답변
				assign.setTaskFile(rs.getBytes(11));					// 과제제출
			}
			System.out.println("과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("과제 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return assign;
	}

	//과제 점수 조회
	public AssignmentDto score_select(int class_no, String stu_id) {
		
		//DB연결
		connectionJDBC();
		
		AssignmentDto assign = new AssignmentDto();
		String sql = "select sum(taskscore), (select sum(perfect_Score) from task where class_no = ?)"
						+ " from submission_task"
					    + " where class_no = ?"
					    + " and student_id = ?;";
			
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, class_no);
			pstmt.setInt(2, class_no);
			pstmt.setString(3, stu_id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
	
				//내 점수가 -1이면 0으로 표시, 아니면 해당점수로 표시
				if(rs.getInt(1)>=0) {
					
					assign.setSumMyScore(rs.getInt(1));
				} else {
					
					assign.setSumMyScore(0);
				}
				assign.setSumPerfectScore(rs.getInt(2));
				System.out.println("점수합계 조회 완료");
			}
			
		} catch (SQLException e) {
//			e.printStackTrace();
				System.out.println("점수합계 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return assign;
	}
	
	//과제 제출 갯수 조회
	public AssignmentDto myCount_select(int class_no, String stu_id) {
		
		//DB연결
		connectionJDBC();
		AssignmentDto assign = new AssignmentDto();
		String sql = "select count(*), (select count(*) from task where class_no = ?)"
						+ " from submission_task"
					    + " where class_no = ?"
					    + " and student_id = ?"
					    + " and tasksubmit = 'Y';";
			
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, class_no);
			pstmt.setInt(2, class_no);
			pstmt.setString(3, stu_id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				assign.setCntMyAssign(rs.getInt(1));
				assign.setCntTotalAssign(rs.getInt(2));
				System.out.println("과제 개수 조회 완료");
			}
			
		} catch (SQLException e) {
//			e.printStackTrace();
				System.out.println("과제 개수 조회 실패");
		} finally {
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return assign;
	}
	
	//콤보박스 과제명 출력-현재
	public ObservableList<String> current_className_selectAll(String stu_id) {
	
		//DB연결
		connectionJDBC();
		
		ObservableList<String> list = FXCollections.observableArrayList();
		String sql = "select c.class_no, c.class_name"
					+"  from request_class rc"
					+" inner join class c"
					+"    on rc.class_no = c.class_no"
					+" where rc.student_id = ?"
					+"   and c.end_date >= curdate();";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
		
			int class_No = 0;
			String class_Name = null;
			String class_Info = null;
			while(rs.next()) {
				
				class_No = rs.getInt(1);							//강의번호
				class_Name = rs.getString(2);						//강의명
				class_Info = "[" + class_No + "] " + class_Name;	//강의정보
				
				list.add(class_Info);
			}
			System.out.println("현재 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("현재 과제 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
	
	//콤보박스 과제명 출력-전체
	public ObservableList<String> full_className_selectAll(String stu_id) {
	
		//DB연결
		connectionJDBC();
		
		ObservableList<String> list = FXCollections.observableArrayList();
		String sql = "select c.class_no, c.class_name"
					+"  from request_class rc"
					+" inner join class c"
					+"    on rc.class_no = c.class_no"
					+" where rc.student_id = ?;";
		
		try {
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, stu_id);
			rs = pstmt.executeQuery();
		
			int class_No = 0;
			String class_Name = null;
			String class_Info = null;
			while(rs.next()) {
				
				class_No = rs.getInt(1);							//강의번호
				class_Name = rs.getString(2);						//강의명
				class_Info = "[" + class_No + "] " + class_Name;	//강의정보
				
				list.add(class_Info);
			}
			System.out.println("현재 과제 조회 성공");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[SQL Error : " + e.getMessage() + "]");
			System.out.println("현재 과제 조회 실패");
		} finally {
			
			//접속종료
			ju.disconnect(connection, pstmt, rs);
		}
		return list;
	}
}