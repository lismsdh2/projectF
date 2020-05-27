/*
 * 작성자 : 도현호
 * 최종 작성일 : 2020-05-15
 */
package DTO;

//수강신청 클래스
public class EnrollmentPopupDto {
	
	private String stu_id;
	private int class_no;
	
	public EnrollmentPopupDto(String stu_id, int class_no) {
		
		this.stu_id = stu_id;
		this.class_no = class_no;
	}
	
	public String getStu_id() {
		return stu_id;
	}
	
	public void setStu_id(String stu_id) {
		this.stu_id = stu_id;
	}
	
	public int getClass_no() {
		return class_no;
	}
	
	public void setClass_no(int class_no) {
		this.class_no = class_no;
	}
}
