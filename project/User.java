package project;

public abstract class User {
	private String id;
	//private String name;
	//private String email;
	private String password;
	
	public User(String id, String password)
	{
		this.id=id;
		this.password=password;
	}
	
	//check if the user login successful;
	public boolean login(String inputPassword)
	{
		return this.password.equals(inputPassword);
	}
	
	public void logout()
	{
		System.out.println("You have logged out.");
	}
	
	//getters and setters
	public String getId() {return id;}
	public String getPassword() {return password;}
	
	public void setId(String id)
	{
		this.id=id;
	}
	
	public void setPassword(String password)
	{
		this.password=password;
	}
	public abstract String getRole();
}
