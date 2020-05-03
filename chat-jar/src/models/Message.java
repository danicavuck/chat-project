package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Message implements Serializable{

	private String sender;
	private String reciever;
	private String content;
	
	
	public Message() {}

	

	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getReciever() {
		return reciever;
	}


	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String mssg) {
		this.content = mssg;
	}
	
	
	
}
