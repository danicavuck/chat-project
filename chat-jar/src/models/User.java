package models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	
	private String username;
	private String password;
	private ArrayList<Message> messages = new ArrayList<Message>();
	
	public User() {}
	
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	public ArrayList<Message> getMessages() {
		return messages;
	}


	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "User: " + this.username;
	}
	
	
}
