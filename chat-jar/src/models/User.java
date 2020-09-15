package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class User implements Serializable {
	
	private String username;
	private String password;
	private HashMap<UUID,Message> messages = new HashMap<UUID,Message>();
	
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
	
	
	public HashMap<UUID, Message> getMessages() {
		return messages;
	}


	public void setMessages(HashMap<UUID, Message> messages) {
		this.messages = messages;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "User: " + this.username;
	}
	
	
}
