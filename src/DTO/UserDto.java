package DTO;

public class UserDto {

	private String id;
	private String password;
	private String name;
	
	private boolean type2;
	
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
	
}
