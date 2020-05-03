package beans;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Message;
import models.User;
import ws.WSEndPoint;

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
	
	@EJB
	private WSEndPoint ws;

	private HashMap<String, Session> sessions = new HashMap<>();

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
	@Path("/chat/post/{text}")
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
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRegisteredUseres() {
		
		List<String> usernames = new ArrayList<String>();
		
		for(User u : users) {
			usernames.add(u.getUsername());
		}
		
		System.out.println(usernames);
		
		return usernames;
	}
	
	
	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getLoggedInUsers() {
		
		List<String> usernames = new ArrayList<>();
		System.out.println("Logged in users: ");
		
		for (User u : loggedIn) {
			usernames.add(u.getUsername());
			
		}
		
		
		return usernames;
		
	}
	

	@POST
	@Path("/users/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		System.out.println(user.getUsername() + "  " + user.getPassword());
		
		for(User u : users) {
			if(user.getUsername().equals(u.getUsername())) {
				System.out.println("User already registered");
				return Response.status(400).entity("Registration failed").build();
			}
		}
		
		this.users.add(user);
		//System.out.println("User registered");
		//System.out.println("Number of registered users " + this.users.size());
		
		
		return Response.status(200).entity("Registration successful").build();
	}


	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response login(User user,@Context HttpServletRequest request, Session session) {
		System.out.println(user.getUsername() + "  " + user.getPassword());
	
		for(User u : this.users) {
				if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
					request.getSession().setAttribute("username", user.getUsername());
					this.loggedIn.add(user);
					sessions.put(user.getUsername(),session);
					System.out.println("User logged in");
					return Response.status(200).entity(user.getUsername()).build();
				}
			}
		
		//System.out.println("Invalid username or password");
		return Response.status(400).entity("Incorrect credentials").build();
	}
	
	
	@DELETE
	@Path("/users/loggedIn/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam ("username") String username,@Context HttpServletRequest request) {
		
		for(User u : loggedIn) {
			if(u.getUsername().equals(username)) {
				loggedIn.remove(u);
				sessions.remove(u);
				request.getSession().invalidate();
				
				System.out.println("User succssfully logged out");
				
				return Response.status(200).entity("User successfully logged out").build();
			}
			
		}
		
		
		return Response.status(400).entity("Logout failed").build();
	}
	
	// -----MESSAGE deo funkcionalnosti

	@POST
	@Path("/messages/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageToAllUsers(Message msg,@Context HttpServletRequest request) throws IOException {
 		ArrayList<Message> mssgs = new ArrayList<Message>();
		
		for(User u : users) {
			Message mssg = new Message();
			String sender = (String)request.getSession().getAttribute("username");
			mssg.setSender(sender);
			mssg.setReciever(u.getUsername());
			mssg.setContent(msg.getContent());
			mssgs = u.getMessages();
			mssgs.add(mssg);

		
			u.setMessages(mssgs);
			System.out.println("Number of mssgs that the user recieved(after): " + mssgs.size());
			ws.sendToAll(sender, mssg.getContent());
		
		}
		
		return Response.status(200).entity("Messages sent").build();
	}
	
	@GET
	@Path("/messages/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Message> getMessagesForUser(@PathParam("user") String username) throws IOException {
		
		ArrayList<Message> mssgs = new ArrayList<Message>();
		
	


		for(User u : users) {
			if(u.getUsername().equals(username)) {
				mssgs = u.getMessages();
			}
		}
		
		if(mssgs.size() == 0) {
			System.out.println("This user didn't recieve any messages");
		}
		
		
	
		return mssgs;
	}
	
	
	//slanje poruke pojedinacnom korisniku
	@POST
	@Path("/messages/{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageUser(Message msg,@Context HttpServletRequest request) throws IOException {
	
		ArrayList<Message> msgs = new ArrayList<Message>();
		
		
		for(User u : users) {
			if(msg.getReciever().equals(u.getUsername())) {
				System.out.println("Found user with added username");
				Message mssg = new Message();
				String sender = (String)request.getSession().getAttribute("username");
				mssg.setSender(sender);
				mssg.setReciever(msg.getReciever());
				mssg.setContent(msg.getContent());
				msgs = u.getMessages();
				msgs.add(mssg);
				
			
				u.setMessages(msgs);
				ws.sendOneMessage(sender, mssg.getContent());

				
				
			}
		}
		//ukoliko nije nasao usera nece addovati u listu
		if(msgs.size() == 0) {
			System.out.println("Unable to send message, user with that username is not in the system");
			return Response.status(400).build();
		}
		
		System.out.println("Message sent");
	
		return Response.status(200).build();
	}
		
	
}
