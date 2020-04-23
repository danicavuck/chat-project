package beans;

import models.User;

public interface ChatRemote {
	public String post(String text);
	
	public String register(User user);
	
	public String login(String username,String password);
	
	
	
	
}
