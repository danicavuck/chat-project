package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Message implements Serializable{
	
	private UUID uuid;
	private String sender;
	private String reciever;
	private String content;
	private LocalDateTime time;
	
	
	public Message() {
		this.setUuid(UUID.randomUUID());
	}

	

	public LocalDateTime getTime() {
		return time;
	}



	public void setTime(LocalDateTime time) {
		this.time = time;
	}



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



	public UUID getUuid() {
		return uuid;
	}



	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	
}
