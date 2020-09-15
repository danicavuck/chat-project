package beans;

import java.util.HashMap;
import java.util.UUID;

import javax.ejb.Singleton;

import models.User;
import models.Message;

@Singleton
public class DBBean {
	
	private HashMap<String,User> registered = new HashMap<String,User>();
	private HashMap<String,User> loggedIn = new HashMap<String,User>();
	
	private HashMap<UUID,Message> allMessages = new HashMap<UUID,Message>();
	
	
	public DBBean() {
//		
//		registered.put("admin", new User("admin","admin"));
//		registered.put("admin", new User("user","user"));
//		registered.put("admin", new User("guest","guest"));
		
	}
	
	
	public DBBean(HashMap<String, User> registered, HashMap<String, User> loggedIn) {
		super();
		this.registered = registered;
		this.loggedIn = loggedIn;
	}


	public HashMap<String, User> getRegistered() {
		return registered;
	}


	public void setRegistered(HashMap<String, User> registered) {
		this.registered = registered;
	}


	public HashMap<String, User> getLoggedIn() {
		return loggedIn;
	}


	public void setLoggedIn(HashMap<String, User> loggedIn) {
		this.loggedIn = loggedIn;
	}




	public HashMap<UUID,Message> getAllMessages() {
		return allMessages;
	}


	public void setAllMessages(HashMap<UUID,Message> allMessages) {
		this.allMessages = allMessages;
	}
	
	
	
}
