package edu.uco.cs.v2c.dispatcher.net.websocket;
import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.RouteCommandPayload;

public class Router implements RoutingListener {
	private static final String LABEL = "DISPATCHER-ROUTER";

  @Override public void onRoute(RegisteredSession module, String message) {
    if(module == null) V2CDispatcher.getLogger().logError(LABEL,
        String.format("Could not send message (%1$s), recipient is null", message));
    else if(message.isBlank()) V2CDispatcher.getLogger().logError(LABEL, "Could not send message, message empty");
    else {
      try {
        WebSocketHandler.dispatch(module.getSession(),
            new RouteCommandPayload().setRecipient(module.getName()).setCommand(message).serialize());
      } catch(MalformedPayloadException e) {}
      V2CDispatcher.getLogger().logDebug(LABEL,
          String.format("Sent message (%1$s), to a registered session at %2$s:%3$d", message,
              module.getSession().getRemoteAddress().getHostString(), module.getSession().getRemoteAddress().getPort()));
    }
  }
	
	

}
