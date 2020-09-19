package edu.uco.cs.v2c.dispatcher.utility;
import org.eclipse.jetty.websocket.api.Session;
import java.util.Map;

/*
 * Some action to be taken by the timer
 * 
 * @author Caleb Power https://github.com/calebpower/timer-demo.git
 * 
 * Adapted for V2C dispatcher under open source by @author
 * Jon Craig
 * 
 * */



public interface TimerAction {
	
	/*
	 * The action
	 * 
	 * @param Session, the associated session
	 * 
	 * */
	
	public void onAction(Session session, Map<Session,String> registeredSessions);
	
}
