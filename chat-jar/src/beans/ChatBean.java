package beans;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import models.User;


//local bean znaci da metode koje hocemo da pogodimo restom
//ne moraju da se navode u remote interfejs
@Stateless
@Path("")
@LocalBean
public class ChatBean{
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
	private Queue queue;
	
	private List<User> users = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
	//pravi rest metodu
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Ok";
	}

	
	@POST
	@Path("post/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String post(@PathParam("text") String text) {
		System.out.println("Recieved message: "+ text);
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest","guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(queue);
			//create and publish a message
			
			TextMessage message = session.createTextMessage();
			message.setText(text);
			sender.send(message);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	
		return "OK";
	}
	
	
	@GET
	@Path("users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRegisteredUseres() {
		
		List<String> usernames = new ArrayList<String>();
		
		for(User u : users) {
			System.out.println(u.getUsername() + " " + u.getPassword());
			usernames.add(u.getUsername());
		}
		
		return usernames;
	}
	
	
	@GET
	@Path("users/loggedin")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getLoggedInUsers() {
		
		List<String> usernames = new ArrayList<>();
	
		for (User u : loggedIn) {
			System.out.println("Username: " + u.getUsername());
			usernames.add(u.getUsername());
		}
		
		
		return usernames;
		
	}
	

	@POST
	@Path("users/register")
	//@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(User user) {
		
		for(User u : users) {
			if(user.getUsername().equals(u.getUsername())) {
				System.out.println("User already registered");
				return "User with this username already exists";
			}
		}
		
		this.users.add(user);
		System.out.println("User registered");
		
		
		return "User registered successfully";
	}


	@POST
	@Path("users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.TEXT_PLAIN)
	public String login(User user) {
		for(User u : this.users) {

				if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
					this.loggedIn.add(user);
					System.out.println("User logged in");
					return "User logged in successfully";
				}
			}
		
		System.out.println("Invalid username or password");
		return "Invalid username or password";
	}
	
	
	@DELETE
	@Path("users/loggedin/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String logout(User user) {
		
		for(User u : loggedIn) {
			if(u.getUsername().equals(user.getUsername())) {
				loggedIn.remove(u);
				System.out.println("User succssfully logged out");
				return "User succssfully logged out" + "\t" + "No of users logged in:" + loggedIn.size() ;
			}
			
		}
		
		
		return "Logging out failed";
	}
	

	
}
