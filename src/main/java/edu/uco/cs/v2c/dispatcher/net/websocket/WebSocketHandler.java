/*
 * Copyright (c) 2020 Caleb L. Power, Everistus Akpabio, Rashed Alrashed,
 * Nicholas Clemmons, Jonathan Craig, James Cole Riggall, and Glen Mathew.
 * All rights reserved. Original code copyright (c) 2020 Axonibyte Innovations,
 * LLC. All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uco.cs.v2c.dispatcher.net.websocket;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.DeregisterListenerPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.DispatchCommandPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.DispatchMessagePayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.HeartbeatAckPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.RegisterConfigurationPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.RegisterListenerPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.UpdateConfigurationPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.ErrorPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.HeartbeatPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.RouteCommandPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.RouteMessagePayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.session.SessionMap;
import edu.uco.cs.v2c.dispatcher.utility.ListenerRegistrationTimerAction;
import edu.uco.cs.v2c.dispatcher.utility.RepeatingTimer;
import edu.uco.cs.v2c.dispatcher.utility.Timer;

/**
 * Handles interactions via the WebSocket
 * 
 * @author Caleb L. Power
 */
@WebSocket public class WebSocketHandler implements Runnable {
  
  private static final String LOG_LABEL = "WEBSOCKET HANDLER";
  
  private static LinkedList<Entry<Session, JSONObject>> queue = new LinkedList<>();
  private static List<Session> sessions = new CopyOnWriteArrayList<>();
  private static Thread instance = null;
  private static SessionMap sessionMap = new SessionMap();
  private static Timer timer = Timer.build(new ListenerRegistrationTimerAction(), 15);//15 seconds to register
  private RoutingListener routingListener = new Router();
  private RoutingMachine routingMachine = RoutingMachine.build(routingListener);
  
  
  
  @SuppressWarnings("unused") private static RepeatingTimer keepaliveTimer = RepeatingTimer.build(30000L, new Runnable() {
    @Override public void run() {
      try {
        broadcast(new HeartbeatPayload()
            .setKey(UUID.randomUUID())
            .setTimestamp(System.currentTimeMillis())
            .serialize());
      } catch(MalformedPayloadException e) {
        e.printStackTrace();
      }
    }
  });
  
  public static SessionMap getSessionMap() {
    return sessionMap;
  }
  
  /**
   * Adds a session to the broadcast pool.
   * 
   * @param session the session
   */
  @OnWebSocketConnect public void onConnect(Session session) {
    V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        String.format("%1$s:%2$d connected via WebSocket.",
            session.getRemoteAddress().getHostString(),
            session.getRemoteAddress().getPort()));
    
    if(instance == null) {
      instance = new Thread(this);
      instance.setDaemon(false);
      instance.start();
    }
    
    
   
    timer.queue(session); //start timer for listener registration.
    //if registration is not done in time (15s currently) it will be disconnected.
    sessions.add(session);
  }
  @OnWebSocketClose public void onDisconnect(Session session, int statusCode, String reason) {
    V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        String.format("%1$s:%2$d disconnected from WebSocket.",
            session.getRemoteAddress().getHostString(),
            session.getRemoteAddress().getPort()));
    
    //reset the target in routing machine, as this session will be null
 	if(routingMachine.getTarget().getSession().equals(session)) {
 		routingMachine.resetTarget();
 		V2CDispatcher.getLogger().logDebug(LOG_LABEL,
 		        "Routing Target Reset");
 	}
    
    //Catches case where client disconnects before deregistering.
    if(sessionMap.containsKey(session)) {
    	V2CDispatcher.getLogger().logInfo(LOG_LABEL,
    			String.format("Client Disconnected, listenener deregistered for " 
    	+ sessionMap.get(session).getName() 
    	+ " %1$s:%2$d",session.getRemoteAddress().getHostString(),
                session.getRemoteAddress().getPort()));
    	sessionMap.deregister(session); 
    	
        // remove from sessions with registered listeners: registeredSessions, subset of connected sessions: sessions
    	
    }
     
    sessions.remove(session); // remove from connected WS sessions 
  }
  
  /**
   * Handles an incoming message on a WebSocket.
   *
   * @param session the session
   * @param message the message
   */
  @OnWebSocketMessage public void onMessage(Session session, String message) {
    JSONObject json = null;
    
    try {
      try {
        json = new JSONObject(message);
        IncomingAction action = IncomingAction.valueOf(json.getString("action"));
        String sender = sessionMap.containsKey(session) ? sessionMap.get(session).getName() : null;
        
        switch(action) {
        case DEREGISTER_LISTENER: {
        	
          DeregisterListenerPayload deregisterListenerPayload = new DeregisterListenerPayload(json);
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        		  String.format("listenener deregistered for " 
        				  + deregisterListenerPayload.getApp()
        				  + " %1$s:%2$d"
        				  ,session.getRemoteAddress().getHostString()
        				  ,session.getRemoteAddress().getPort()));
          sessionMap.deregister(session); // De-Register session from registration map
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());       
        
          
       // for each sesssion k if v is eavesdropper, forward deregistration message 	
          messageEavesdroppers(json, null);
          break;
        }
        
        case DISPATCH_COMMAND: {
          DispatchCommandPayload incoming = new DispatchCommandPayload(json);
          routingMachine.queue(incoming.getCommand());
          if(routingMachine.getTarget() == null) {
            V2CDispatcher.getLogger().logError(LOG_LABEL, "Message could not be sent because target has not yet been set.");
          } else {
            RouteCommandPayload outgoing = new RouteCommandPayload()
                .setCommand(incoming.getCommand())
                .setRecipient(routingMachine.getTarget().getName());
            try {
            messageEavesdroppers(outgoing.setEavesdropped(true).serialize(), routingMachine.getTarget());
            }
            catch(MalformedPayloadException e) {
          	  V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Malformed Payload-Failed to notify eavesdroppers of command %1s$", outgoing.toString() ));
            }
          }
          
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());
          break;
        }
        
        case DISPATCH_MESSAGE: {
          DispatchMessagePayload incoming = new DispatchMessagePayload(json);
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());
          
          Session target = sessionMap.containsKey(incoming.getRecipient()) ? sessionMap.get(incoming.getRecipient()).getSession() : null;

          if(target == null) {
            ErrorPayload outgoing = new ErrorPayload()
                .setCause(json)
                .setInfo("Unknown recipient.");
            dispatch(session, outgoing.serialize());
          } else {
            RouteMessagePayload outgoing = new RouteMessagePayload()
                .setMessage(incoming.getMessage())
                .setRecipient(incoming.getRecipient())
                .setSender(sender);
            dispatch(target, outgoing.serialize());
            messageEavesdroppers(outgoing.setEavesdropped(true).serialize(), sessionMap.get(target));
          }
          
          break;
        }
        
        case REGISTER_CONFIGURATION: {
          try {
            new RegisterConfigurationPayload(json);
          } catch(PayloadHandlingException e) {
            V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Received a bad default configuration from %1$s.", sender));
            throw e;
          }
          
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, String.format("Received a default configuration: %1$s", json.toString()));
          messageEavesdroppers(json, sessionMap.containsKey(session) ? sessionMap.get(session) : null);
          break;
        }
        
        case REGISTER_LISTENER: {
          RegisterListenerPayload incoming = null;
          
          try {
            incoming = new RegisterListenerPayload(json); 
          } catch(PayloadHandlingException e) {
            V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Received a bad configuration update message from %1$s.", sender));
            throw e;
          }
          
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());
          sessionMap.register(new RegisteredSession(session, incoming.getApp(), incoming.isEavesdropper())); // map the session to the app name.
         
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        		  String.format("Listenener Registered for " 
        				  + incoming.getApp() 
        				  + " %1$s:%2$d",
        				  session.getRemoteAddress().getHostString(),
        				  session.getRemoteAddress().getPort())); //log the registration
          
         
       // for each sesssion k if v is eavesdropper, forward message about new Registration	
          messageEavesdroppers(json, null);
          
          break;
        }
        
        case UPDATE_CONFIGURATION: {
          UpdateConfigurationPayload incoming = new UpdateConfigurationPayload(json);
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());
          
          Session target = sessionMap.containsKey(incoming.getApp()) ? sessionMap.get(incoming.getApp()).getSession() : null;
          
          if(target == null) {
            ErrorPayload outgoing = new ErrorPayload()
                .setCause(json)
                .setInfo("Unknown recipient.");
            dispatch(session, outgoing.serialize());
          } else {
            dispatch(target, json);
            messageEavesdroppers(json, sessionMap.get(target));
          }
          
          break;
        }
        
        case HEARTBEAT_ACK: {
          V2CDispatcher.getLogger().logDebug(LOG_LABEL, json.toString());
          HeartbeatAckPayload incoming = new HeartbeatAckPayload(json);
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
              String.format("Got ack from %1$s, key = %2$s",
                  incoming.getApp(),
                  incoming.getKey().toString()));
          break;
        }
        
        default:
          throw new PayloadHandlingException(action, "Unexpected action.");
        }
      } catch(PayloadHandlingException e) {
        ErrorPayload response = new ErrorPayload()
            .setInfo(e.getMessage())
            .setCause(e.getOffendingPayload());
        
        V2CDispatcher.getLogger().logError(LOG_LABEL,
            String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
                session.getRemoteAddress().getHostString(),
                session.getRemoteAddress().getPort(),
                e.getMessage()));
        
        session.getRemote().sendString(response.toString());
      } catch(MalformedPayloadException e) {
        ErrorPayload response = new ErrorPayload()
            .setInfo(e.getMessage())
            .setCause(e.getOffendingPayload());
        
        V2CDispatcher.getLogger().logError(LOG_LABEL,
            String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
                session.getRemoteAddress().getHostString(),
                session.getRemoteAddress().getPort(),
                e.getMessage()));
        
        session.getRemote().sendString(response.toString());
      } catch(JSONException e) {
        ErrorPayload response = new ErrorPayload().setInfo(e.getMessage());
        
        if(json != null)
          response.setCause(json);
        
        V2CDispatcher.getLogger().logError(LOG_LABEL,
            String.format("Some exception was thrown while parsing message from %1$s:%2$d: %3$s",
                session.getRemoteAddress().getHostString(),
                session.getRemoteAddress().getPort(),
                e.getMessage()));
        
        session.getRemote().sendString(response.toString());
      }
      
    } catch(IOException e) {
      V2CDispatcher.getLogger().logError(LOG_LABEL,
          "Some exception was thrown while handling an incoming message: " + e.getMessage());
    }
  }
  
  
  public static void messageEavesdroppers(JSONObject out, RegisteredSession exclude) {
    final AtomicInteger count = new AtomicInteger(0);
    
	  sessionMap.getSessionsByName().forEach((k,v) -> {
		  if((exclude == null || !k.equalsIgnoreCase(exclude.getName()))
		      && v.getSession().isOpen() && v.isEavesdropper()) try {
		    count.incrementAndGet();
		    V2CDispatcher.getLogger().logDebug(LOG_LABEL,
		        String.format("The following message is being sent to the %1$s eavesdropper: %2$s",
		            v.getName(), out.toString()));
		    dispatch(v.getSession(), out);
		  } catch(Exception e) {
		    V2CDispatcher.getLogger().logError(LOG_LABEL,
		        String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
		            v.getSession().getRemoteAddress().getHostString(),
		            v.getSession().getRemoteAddress().getPort(),
		            e.getMessage()));
		  }
	  });
	  
    V2CDispatcher.getLogger().logDebug(LOG_LABEL,
        String.format("No unexcluded eavesdroppers were registered so the following message was not sent to them: %1$s",
            out.toString()));
  }
  

  
  
  
  /**
   * Queues a broadcast to all sessions.
   * 
   * @param payload the payload
   */
  public static void broadcast(JSONObject payload) {
	// for(Session session : sessions)
	for(Session session : sessionMap.getSessions())
      dispatch(session, payload);
  }
  
  /**
   * Queues a broadcast to a particular session.
   * 
   * @param session the session
   * @param payload the payload
   */
  public static void dispatch(Session session, JSONObject payload) {
  try {
	  V2CDispatcher.getLogger().logDebug(LOG_LABEL,
        String.format("Queueing payload for dispatch to %1$s:%2$d",
            session.getRemoteAddress().getHostString(),
            session.getRemoteAddress().getPort()));

    synchronized(queue) {
      queue.add(new SimpleEntry<>(session, payload));
      queue.notifyAll();
    }
  }
  
  catch(Exception e) {
	  V2CDispatcher.getLogger().logError(LOG_LABEL,
              "failed to dispatch: " + e.getMessage());
  }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public void run() {
    try {
      while(!instance.isInterrupted()) {
        Entry<Session, JSONObject> entry = null;
        
        synchronized(queue) {
          while(queue.isEmpty()) queue.wait();
          entry = queue.remove(0);
        }
        
        try {
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
              String.format("Dispatching a payload of %1$s to %2$s:%3$d",
                  entry.getValue().has("action") ? "type " + entry.getValue().getString("action") : "unknown type",
                  entry.getKey().getRemoteAddress().getHostString(),
                  entry.getKey().getRemoteAddress().getPort()));
          
          entry.getKey().getRemote().sendString(entry.getValue().toString());
        } catch(Exception e) {
          V2CDispatcher.getLogger().logError(LOG_LABEL,
              "Some exception was thrown while processing an outgoing message: " + e.getMessage());
        }
      }
    } catch(InterruptedException e) { }
  }
  
}
