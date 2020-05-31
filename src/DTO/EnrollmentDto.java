package DTO;
/*
 * 작성자 : 도현호
 * 최종 작성일 : 2020-05-15
 */
import java.sql.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EnrollmentDto {
	
	private final IntegerProperty classno = new SimpleIntegerProperty();
	private final StringProperty classname = new SimpleStringProperty();
	private final StringProperty teachername = new SimpleStringProperty();
	private final StringProperty teacherid = new SimpleStringProperty();
	private final StringProperty classdescription = new SimpleStringProperty();
	private final ObjectProperty<Date> startdate = new SimpleObjectProperty<Date>();
	private final ObjectProperty<Date> enddate = new SimpleObjectProperty<Date>();
	private final IntegerProperty limitstudent = new SimpleIntegerProperty();
	private final StringProperty str = new SimpleStringProperty();
	private final StringProperty status = new SimpleStringProperty(); 
	
	public String getStr() {
		return str.get();
	}
	
	public void setStr(String value) {
		
		this.str.set(value);
	}

	public StringProperty strProperty() {
		
		return this.str;
	}
	public int getClassno() {
		
		return this.classno.get();
	}
	
	public void setClassno(int value) {
		
		this.classno.set(value);
	}
	
	public IntegerProperty classnoProperty() {
		
		return this.classno;
	}
	
	public String getClassname() {
		
		return this.classname.get();
	}
	
	public void setClassname(String value) {
		
		this.classname.set(value);
	}
	
	public StringProperty classnameProperty() {
		
		return this.classname;
	}
	public String getTeachername() {
		
		return this.teachername.get();
	}
	
	public void setTeachername(String value) {
		
		this.teachername.set(value);
	}
	
	public StringProperty teachernameProperty() {
		
		return this.teachername;
	}
	public String getTeacherid() {
		
		return this.teacherid.get();
	}
	
	public void setTeacherid(String value) {
		
		this.teacherid.set(value);
	}
	
	public StringProperty teacheridProperty() {
		
		return this.teacherid;
	}
	
	public String getClassdescription() {
		
		return this.classdescription.get();
	}
	
	public void setClassdescription(String value) {
		
		this.classdescription.set(value);
	}
	
	public StringProperty classdescriptionProperty() {
		
		return this.classdescription;
	}
	
	public Date getStartdate () {
		
		return this.startdate.get();
	}
	
	public void setStartdate (Date value) {
		
		this.startdate.set(value);
	}
	
	public ObjectProperty<Date> startdateProperty() {
		
		return this.startdate;
	}
	
	public Date getEnddate () {
		
		return this.enddate.get();
	}
	
	public void setEnddate (Date value) {
		
		this.enddate.set(value);
	}
	
	public ObjectProperty<Date> enddateProperty() {
		
		return this.enddate;
	}
	
	public int getLimitstudent() {
		
		return this.limitstudent.get();
	}
	
	public void setLimitstudent(int value) {
		
		this.limitstudent.set(value);
	}
	
	public IntegerProperty limitstudentProperty() {
		
		return this.limitstudent;
	}
	
	public String getStatus() {
		
		return this.status.get();
	}
	
	public void setStatus(String value) {
		
		this.status.set(value);
	}
	
	public StringProperty statusProperty() {
		
		return this.status;
	}
}