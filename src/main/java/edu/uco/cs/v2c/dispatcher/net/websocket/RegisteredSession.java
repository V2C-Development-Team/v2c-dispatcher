package edu.uco.cs.v2c.dispatcher.net.websocket;

public class RegisteredSession {
	
	  private boolean eavesdrop = false;
	  private String app = null;
	  
	  public RegisteredSession(String app, boolean eavesdrop) {
		  this.app = app;
		  this.eavesdrop = eavesdrop;
	  }
	  
	  public String getName() {
		  return app;
	  }

	  
	  public boolean isEavesdropper() {
		  return eavesdrop;
	  }
}
