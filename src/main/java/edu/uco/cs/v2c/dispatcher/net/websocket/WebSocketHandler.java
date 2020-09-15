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
import java.util.Map.Entry;
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

/**
 * Handles interactions via the WebSocket
 * 
 * @author Caleb L. Power
 */
@WebSocket public class WebSocketHandler implements Runnable {
  
  private static LinkedList<Entry<Session, JSONObject>> queue = new LinkedList<>();
  private static List<Session> sessions = new CopyOnWriteArrayList<>();
  private static Thread instance = null;
  
  /**
   * Adds a session to the broadcast pool.
   * 
   * @param session the session
   */
  @OnWebSocketConnect public void onConnect(Session session) {
    if(instance == null) {
      instance = new Thread(this);
      instance.setDaemon(false);
      instance.start();
    }
    
    // TODO ask session to identify itself
    sessions.add(session);
  }
  
  /**
   * Removes a session from the broadcast pool.
   * 
   * @param session the session
   * @param statusCode the status code
   * @param reason the reason that the session closed
   */
  @OnWebSocketClose public void onDisconnect(Session session, int statusCode, String reason) {
    // TODO remove session from memory banks
    sessions.remove(session);
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
        
        session.getRemote().sendString(response.toString());
      } catch(JSONException e) {
        if(json != null) {
          ErrorPayload response = new ErrorPayload()
              .setInfo(e.getMessage())
              .setCause(json);
          
          session.getRemote().sendString(response.toString());
        }
      }
      
    } catch(IOException e) {
      e.printStackTrace();
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
          entry.getKey().getRemote().sendString(entry.getValue().toString());
        } catch(IOException e) {
          V2CDispatcher.getLogger().logError("WEBSOCKET HANDLER", e.getMessage());
        }
      }
    } catch(InterruptedException e) { }
  }
  
}
