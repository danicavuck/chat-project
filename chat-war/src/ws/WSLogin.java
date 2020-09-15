package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/login/{username}")
@LocalBean
public class WSLogin {
	
static Map<String,Session> sessions = new HashMap<>();
	
	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		if(!sessions.containsKey(username)) {
			sessions.put(username,session);
		}
	}
	
	
	//web socket prosledjuje poruku svim klijentima
	@OnMessage
	public void newLogin(String username) {
		try {
			for(String s : sessions.keySet()) {
				sessions.get(s).getBasicRemote().sendText(username);
				}
			} catch(IOException e) {
			e.printStackTrace();
		}

	}

	
		
	@OnClose
	public void close(@PathParam("username") String username,Session session) {
		sessions.remove(username);
	}
	
	@OnError
	public void error(@PathParam("username") String username,Session session, Throwable t) {
		sessions.remove(username);
		t.printStackTrace();
	}
	
	

}
