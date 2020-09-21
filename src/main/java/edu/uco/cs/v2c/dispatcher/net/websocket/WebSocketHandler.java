/*
 * Copyright (c) 2020 V2C Development Team. All rights reserved.
 * Licensed under the Version 0.0.1 of the V2C License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at <https://tinyurl.com/v2c-license>.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions 
 * limitations under the License.
 * 
 * THIS FILE HAS BEEN MODIFIED. ORIGINAL WORK ADHERES TO THE FOLLOWING NOTICE.
 * 
 * Copyright (c) 2020 Axonibyte Innovations, LLC. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.RegisterConfigurationPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.RegisterListenerPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.UpdateConfigurationPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.ErrorPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.RouteMessagePayload;
import edu.uco.cs.v2c.dispatcher.utility.ListenerRegistrationTimerAction;
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
  private static Map<Session,RegisteredSession> registeredSessions = new ConcurrentHashMap<Session,RegisteredSession>();
  private static Timer timer = Timer.build(new ListenerRegistrationTimerAction(), 3);//3 seconds to register
 
  
  private static String event, app, ip = null;//variables for outgoing messages
  private static final String sender = "DISPATCHER";// sender name for outgoing messages. //#TODO send messages on state change
  private static RouteMessagePayload outgoing = null;
  /*
   * Returns the registeredSessions Map 
   * 
   * 
   * */ 
  public static Map<Session,RegisteredSession> getRegisteredSessions() {
	 return registeredSessions;
 }
 
 // public static Map<Session,String> getRegisteredSessions() {
	//  return registeredSessions;
  //}
  
  
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
    //if registration is not done in time (10s currently) it will be disconnected.
    sessions.add(session);
    
    // create a message to notify eavesdroppers
    outgoing = new RouteMessagePayload().setMessage(new JSONObject().put("event", "CONNECT")
    																.put("message", String.format("Client %1$s at %2$d has connected"
    																		,session.getRemoteAddress().getHostString(),
    																		session.getRemoteAddress().getPort()))).setSender(sender);
     // for each sesssion k if v is eavesdropper, send message about new connecct																
     registeredSessions.forEach((k,v)-> {
    	 if(k.isOpen()) {
    	 if(v.isEavesdropper()) {
    		 outgoing.setRecipient(v.getName());
    		 try {
				dispatch(k, outgoing.serialize());
			} catch (MalformedPayloadException e) {
				V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
		                k.getRemoteAddress().getHostString(),
		                k.getRemoteAddress().getPort(),
		                e.getMessage()));
				        
			}
    	 }
     }});
  }
  
  /**
   * Removes a session from the broadcast pool.
   * 
   * @param session the session
   * @param statusCode the status code
   * @param reason the reason that the session closed
   */
  @OnWebSocketClose public void onDisconnect(Session session, int statusCode, String reason) {
    V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        String.format("%1$s:%2$d disconnected from WebSocket.",
            session.getRemoteAddress().getHostString(),
            session.getRemoteAddress().getPort()));
    
   
    // create a message to notify eavesdroppers
    outgoing = new RouteMessagePayload().setMessage(new JSONObject().put("event", "DISCONNECT")
    																.put("message", String.format("Client %1$s at %2$d has disconnected"
    																		,session.getRemoteAddress().getHostString(),
    																		session.getRemoteAddress().getPort()))).setSender(sender);
     // for each sesssion k if v is eavesdropper, send message about  disconnecct																
     registeredSessions.forEach((k,v)-> { 
    	 if(k.isOpen()) {
    	 if(v.isEavesdropper()) {
    		 outgoing.setRecipient(v.getName());
    		 try {
				dispatch(k, outgoing.serialize());
			} catch (MalformedPayloadException e) {
				V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
		                k.getRemoteAddress().getHostString(),
		                k.getRemoteAddress().getPort(),
		                e.getMessage()));
				        
			}
    	 }
     }
     });
    
    //Catches case where client disconnects before deregistering.
    if(registeredSessions.containsKey(session)) {
    	V2CDispatcher.getLogger().logInfo(LOG_LABEL,
    			String.format("Client Disconnected, listenener deregistered for " 
    	+ registeredSessions.get(session).getName() 
    	+ " %1$s:%2$d",session.getRemoteAddress().getHostString(),
                session.getRemoteAddress().getPort()));
    	registeredSessions.remove(session); 
        // remove from sessions with registered listeners: registeredSessions, subset of connected sessions: sessions
    	//broadcast(new JSONObject()); // send connected app name list to dash, eventually need to route.
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
        
        switch(action) {
        case DEREGISTER_LISTENER: {
          new DeregisterListenerPayload(json);
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        		  String.format("listenener deregistered for " 
        				  + json.getString("app") 
        				  + " %1$s:%2$d"
        				  ,session.getRemoteAddress().getHostString()
        				  ,session.getRemoteAddress().getPort()));
          registeredSessions.remove(session); // De-Register session from registration map
          
          // create a message to notify eavesdroppers
          outgoing = new RouteMessagePayload().setMessage(new JSONObject().put("event", "DEREGISTER_LISTENER")
          																.put("message", String.format("Client %1$s at %2$d has deregistered as %3$s"
          																		,session.getRemoteAddress().getHostString(),
          																		session.getRemoteAddress().getPort(),
          																		json.getString("app")))).setSender(sender);
           // for each sesssion k if v is eavesdropper, send message about new connecct																
           registeredSessions.forEach((k,v)-> {
        	   if(k.isOpen()) {
          	 if(v.isEavesdropper()) {
          		 outgoing.setRecipient(v.getName());
          		 try {
      				dispatch(k, outgoing.serialize());
      			} catch (MalformedPayloadException e) {
      				V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
      		                k.getRemoteAddress().getHostString(),
      		                k.getRemoteAddress().getPort(),
      		                e.getMessage()));
      				        
      			}
          	 }
           }});
          
          break;
        }
        
        case DISPATCH_COMMAND: {
          new DispatchCommandPayload(json);
          break;
        }
        
        case DISPATCH_MESSAGE: {
          new DispatchMessagePayload(json);
          break;
        }
        
        case REGISTER_CONFIGURATION: {
          new RegisterConfigurationPayload(json);
          break;
        }
        
        case REGISTER_LISTENER: {
          new RegisterListenerPayload(json);
          registeredSessions.put(session, new RegisteredSession(json.getString("app"),json.getBoolean("eavesdrop")));// map the session to the app name.
       
          V2CDispatcher.getLogger().logInfo(LOG_LABEL,
        		  String.format("Listenener Registered for " 
        				  + json.getString("app") 
        				  + " %1$s:%2$d",
        				  session.getRemoteAddress().getHostString(),
        				  session.getRemoteAddress().getPort())); //log the registration
          
          
          // create a message to notify eavesdroppers
          outgoing = new RouteMessagePayload().setMessage(new JSONObject().put("event", "REGISTER_LISTENER")
          																.put("message", String.format("Client %1$s at %2$d has registered as %3$s"
          																		,session.getRemoteAddress().getHostString(),
          																		session.getRemoteAddress().getPort(),
          																		json.getString("app")))).setSender(sender);
           // for each sesssion k if v is eavesdropper, send message about new Registration															
           registeredSessions.forEach((k,v)-> {
        	   if(k.isOpen()) {
          	 if(v.isEavesdropper()) {
          		 outgoing.setRecipient(v.getName());
          		 try {
      				dispatch(k, outgoing.serialize());
      			} catch (MalformedPayloadException e) {
      				V2CDispatcher.getLogger().logError(LOG_LABEL, String.format("Some exception was thrown while handling payload from %1$s:%2$d: %3$s",
      		                k.getRemoteAddress().getHostString(),
      		                k.getRemoteAddress().getPort(),
      		                e.getMessage()));
      				        
      			}
          	 }
           }});
          
          
          break;
        }
        
        case UPDATE_CONFIGURATION: {
          new UpdateConfigurationPayload(json);
          break;
        }
        
        default:
          throw new PayloadHandlingException(action, "Unexpected action.");
        }
        
        broadcast(json); // XXX this echoes incoming well-formed messages; needs to be removed in favor of a routing mechanism
      } catch(PayloadHandlingException e) { // TODO uncomment this
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
  
  /**
   * Queues a broadcast to all sessions.
   * 
   * @param payload the payload
   */
  public static void broadcast(JSONObject payload) {
    for(Session session : sessions)
      dispatch(session, payload);
  }
  
  /**
   * Queues a broadcast to a particular session.
   * 
   * @param session the session
   * @param payload the payload
   */
  public static void dispatch(Session session, JSONObject payload) {
    V2CDispatcher.getLogger().logDebug(LOG_LABEL,
        String.format("Queueing payload for dispatch to %1$s:%2$d",
            session.getRemoteAddress().getHostString(),
            session.getRemoteAddress().getPort()));
    
    synchronized(queue) {
      queue.add(new SimpleEntry<>(session, payload));
      queue.notifyAll();
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
              String.format("Dispatching a payload to %1$s:%2$d",
                  entry.getKey().getRemoteAddress().getHostString(),
                  entry.getKey().getRemoteAddress().getPort()));
          
          entry.getKey().getRemote().sendString(entry.getValue().toString());
        } catch(IOException e) {
          V2CDispatcher.getLogger().logError(LOG_LABEL,
              "Some exception was thrown while processing an outgoing message: " + e.getMessage());
        }
      }
    } catch(InterruptedException e) { }
  }
  
}
