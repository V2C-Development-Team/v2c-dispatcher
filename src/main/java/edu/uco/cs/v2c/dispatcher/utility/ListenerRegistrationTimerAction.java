package edu.uco.cs.v2c.dispatcher.utility;
import org.eclipse.jetty.websocket.api.Session;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.websocket.RegisteredSession;
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
	
	private static final String LOG_LABEL = "WEBSOCKET HANDLER/REGISTRATIONTIMER";

	@Override public void onAction(Session session) {
		try {
  		if(!WebSocketHandler.getSessionMap().containsKey(session)) {
  			V2CDispatcher.getLogger().logError(LOG_LABEL,
  					String.format("%1$s:%2$d listener not registered in time.",
  					session.getRemoteAddress().getHostString(),
  	                session.getRemoteAddress().getPort()));
      		session.close(1, "OnConnect- Sesson listener not registered in time.");// if not registered disconnect
      }
    } catch (NullPointerException e){
			V2CDispatcher.getLogger().logError(LOG_LABEL, "Session closed before timer elapse");
		}
	}
	
}
