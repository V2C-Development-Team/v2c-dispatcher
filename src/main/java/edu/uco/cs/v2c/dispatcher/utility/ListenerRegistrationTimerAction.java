package edu.uco.cs.v2c.dispatcher.utility;
import org.eclipse.jetty.websocket.api.Session;

import edu.uco.cs.v2c.dispatcher.net.websocket.WebSocketHandler;

import java.util.Map;
/*
 * A timer action that Disconnects the sesssion should they fail to 
 * register a listener within the time limit
 * 
 * @author Jon Craig
 * 
 * */




public class ListenerRegistrationTimerAction implements TimerAction{
	
	private static Map<Session,String> registeredSessions;

	@Override public void onAction(Session session) {
		registeredSessions = WebSocketHandler.getRegisteredSessions();
		if(!registeredSessions.containsKey(session)) {
    		session.close(1, "OnConnect- Sesson listener not registered in time.");// if not registered disconnect
    	}
		
	}
	
}
