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
package edu.uco.cs.v2c.dispatcher.net.websocket.outgoing;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.MalformedPayloadException;

/**
 * An outgoing payload to be sent over a WebSocket for the purposes of
 * interacting with the chatroom functionality.
 * 
 * @author Caleb L. Power
 */
public abstract class OutgoingPayload {
  
  /**
   * Actions denoting the purpose of an outgoing message.
   * Such messages are generally sent by this system.
   * 
   * @author Caleb L. Power
   */
  public static enum OutgoingAction {
    
    /**
     * Indicates that the payload contains some message, processed and ready to
     * be sent to the intended recipient.
     */
    ROUTE_MESSAGE,
    
    /**
     * Indicates that the payload contains some command, processed and ready to
     * be sent to the intended recipient. Such a message will have the target-
     * switching activation word sequences stripped.
     */
    ROUTE_COMMAND,
    
    /**
     * Indicates that the payload regards some error that has occurred,
     * generally (but not necessarily) in response to some action initiated by
     * a third party application.
     */
    WEBSOCKET_ERROR,
    
    /**
     * Indicates a keepalive request-- mostly a NOP, but used to maintain the
     * validity of the WebSocket connection.
     */
    HEARTBEAT
    
  }
  
  protected static final String ACTION_VAR = "action";
  
  protected OutgoingAction action = null;
  
  protected OutgoingPayload(OutgoingAction action) {
    this.action = action;
  }
  
  /**
   * Retrieves the {@link OutgoingAction} if it exists.
   * 
   * @return the appropriate {@link OutgoingAction}
   */
  public OutgoingAction getAction() {
    return action;
  }
  
  /**
   * Serializes the encapsulated payload into a JSON object that can be sent
   * over the WebSocket.
   * 
   * @return a {@link JSONObject}
   * @throws MalformedPayloadException if payload is invalid upon serialization
   */
  public abstract JSONObject serialize() throws MalformedPayloadException;
  
  /**
   * {@inheritDoc}
   */
  @Override public String toString() {
    try {
      return serialize().toString();
    } catch(MalformedPayloadException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
}
