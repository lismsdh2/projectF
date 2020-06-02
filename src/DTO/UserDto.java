﻿package DTO;

public class UserDto {

	private String id;						//아이디
	private String password;				//비밀번호
	private String name;					//이름
	private boolean type2;					//신분(false-학생/true-강사)
	private int class_no;					//강의번호
	private int task_no;					//과제번호
	
	public UserDto() {
		
	}
	public UserDto(String id, String password, boolean type2) {
		this.id=id;
		this.password=password;
		this.type2=type2;
	}
	public boolean gettype2() {
		return type2;
	}
	public void settype2(boolean type2) {
		this.type2 = type2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getName() {
		return name;
	};
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getClass_no() {
		return this.class_no;
	}
	
	public void setClass_no(int class_no) {
		this.class_no = class_no;
	}
	
	public int getTask_no() {
		return this.task_no;
	}
	
	public void setTask_no(int task_no) {
		this.task_no = task_no;
	}
}
