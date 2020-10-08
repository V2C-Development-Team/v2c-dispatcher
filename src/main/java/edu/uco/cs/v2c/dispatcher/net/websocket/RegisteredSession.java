package edu.uco.cs.v2c.dispatcher.net.websocket;

import org.eclipse.jetty.websocket.api.Session;

public class RegisteredSession {
	
	  private boolean eavesdrop = false;
	  private Session session = null;
	  private String app = null;
	  
	  public RegisteredSession(Session session, String app, boolean eavesdrop) {
	    this.session = session;
		  this.app = app;
		  this.eavesdrop = eavesdrop;
	  }
	  
	  public String getName() {
		  return app;
	  }

	  public boolean isEavesdropper() {
		  return eavesdrop;
	  }
	  
	  public Session getSession() {
	    return session;
	  }
}
