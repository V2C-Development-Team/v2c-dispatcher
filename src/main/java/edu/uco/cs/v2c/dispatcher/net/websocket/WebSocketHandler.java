/*
 * Copyright (c) 2019 Axonibyte Innovations, LLC. All rights reserved.
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

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.ErrorPayload;

/**
 * Handles interactions via the WebSocket
 * 
 * @author Caleb L. Power
 */
@WebSocket public class WebSocketHandler {
  
  /**
   * Adds a session to the broadcast pool.
   * 
   * @param session the session
   */
  @OnWebSocketConnect public void onConnect(Session session) {
    // TODO ask session to identify itself
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
        // TODO: add incoming actions
        
        default:
          throw new PayloadHandlingException(action, "Unexpected action.");
        }
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
  
  // TODO we need code here that will send and/or broadcasts to target sessions
  
}
