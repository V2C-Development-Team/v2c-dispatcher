package edu.uco.cs.v2c.dispatcher.net.websocket;
import org.eclipse.jetty.websocket.api.Session;

public interface RoutingListener {
	
	public void onRoute(Session module, String message, String moduleName);

}