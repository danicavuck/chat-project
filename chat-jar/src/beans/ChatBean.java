package beans;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
import ws.WSLogin;
import ws.WSLogout;

//local bean znaci da metode koje hocemo da pogodimo restom
//ne moraju da se navode u remote interfejs
@Stateless
@Path("")
@LocalBean
public class ChatBean{
	
	private HashMap<String, Session> sessions = new HashMap<>();
	
	@EJB
	private WSEndPoint ws;
	
	@EJB
	private WSLogin wslogin;
	
	@EJB
	private WSLogout wslogout;

	@EJB
	private DBBean database;
	
	
	//pravi rest metodu
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Ok";
	}

	
	@GET
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getRegisteredUseres() {
		return database.getRegistered().keySet();
	}
	
	
	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getLoggedInUsers() {
		return database.getLoggedIn().keySet();
	}
	

	@POST
	@Path("/users/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		System.out.println(user.getUsername() + "  " + user.getPassword());
		
		for(User u : database.getRegistered().values()) {
			if(user.getUsername().equals(u.getUsername())) {
				System.out.println("User already registered");
				return Response.status(400).entity("Registration failed").build();
			}
		}
		
		database.getRegistered().put(user.getUsername(), user);
	
		
		return Response.status(200).entity("Registration successful").build();
	}


	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response login(User user,@Context HttpServletRequest request, Session session) {
		System.out.println(user.getUsername() + "  " + user.getPassword());
	
		for(User u : database.getRegistered().values()) {
				if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
					request.getSession().setAttribute("username", user.getUsername());
					database.getLoggedIn().put(user.getUsername(), user);
					sessions.put(user.getUsername(),session);
					wslogin.newLogin(user.getUsername());
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
		
		for(User u : database.getLoggedIn().values()) {
			if(u.getUsername().equals(username)) {
				database.getLoggedIn().remove(u.getUsername(), u);
				request.getSession().removeAttribute(username);
				request.getSession().invalidate();
				wslogout.newLogout(username);
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
		
		for(User u : database.getRegistered().values()) {
			Message mssg = new Message();
			String sender = (String)request.getSession().getAttribute("username");
			mssg.setSender(sender);
			mssg.setReciever(u.getUsername());
			mssg.setContent(msg.getContent());
			mssg.setTime(LocalDateTime.now());
			u.getMessages().put(mssg.getUuid(),mssg);
			
			database.getAllMessages().put(mssg.getUuid(), mssg);
			
			ws.sendToAll(sender, mssg.getContent());
		
		}
		
		return Response.status(200).entity("Messages sent").build();
	}
	
	@GET
	@Path("/messages/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Message> getMessagesForUser(@PathParam("user") String username) throws IOException {
		Collection<Message> mssgs = null;
		for(User u : database.getRegistered().values()) {
			if(u.getUsername().equals(username)) {
				mssgs = u.getMessages().values();
			}
		}
		
		if(mssgs == null) {
			System.out.println("This user didn't recieve any messages");
		}
		
		
		return mssgs;
	}
	
	
	//slanje poruke pojedinacnom korisniku
	@POST
	@Path("/messages/{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageUser(Message msg,@Context HttpServletRequest request) throws IOException {
	
		for(User u : database.getRegistered().values()) {
			if(msg.getReciever().equals(u.getUsername())) {
				System.out.println("Found user with added username");
				Message mssg = new Message();
				String sender = (String)request.getSession().getAttribute("username");
				mssg.setSender(sender);
				mssg.setReciever(msg.getReciever());
				mssg.setContent(msg.getContent());
				mssg.setTime(LocalDateTime.now());
				u.getMessages().put(mssg.getUuid(),mssg);
				
				database.getAllMessages().put(mssg.getUuid(), mssg);
				
				ws.sendOneMessage(mssg.getReciever(),mssg.getSender(), mssg.getContent());
				System.out.println("Message sent");
				return Response.status(200).build();
			}
		}
		return Response.status(400).entity("User not found").build();
			

	}
}