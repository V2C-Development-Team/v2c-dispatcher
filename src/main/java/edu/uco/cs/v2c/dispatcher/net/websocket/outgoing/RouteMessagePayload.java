/*
 * Copyright (c) 2020 Caleb L. Power, Everistus Akpabio, Rashed Alrashed,
 * Nicholas Clemmons, Jonathan Craig, James Cole Riggall, and Glen Mathew.
 * All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.OutgoingPayload.OutgoingAction;

/**
 * Encapsulates the outgoing {@link OutgoingAction#ROUTE_MESSAGE} payload.
 * 
 * @author Caleb L. Power
 */
public class RouteMessagePayload extends OutgoingPayload {
  
  private static final String MESSAGE_VAR = "message";
  private static final String SENDER_VAR = "sender";
  private static final String RECIPIENT_VAR = "recipient";
  private static final String EAVESDROPPED_VAR = "eavesdropped";
  
  private boolean eavesdropped = false;
  private JSONObject message = null;
  private String sender = null;
  private String recipient = null;
  
  /**
   * Overloaded constructor to instantiate the payload.
   */
  public RouteMessagePayload() {
    super(OutgoingAction.ROUTE_MESSAGE);
  }
  
  /**
   * Determines whether or not this is an eavesdropped message.
   * 
   * @return {@code true} iff this is an eavesdropped message
   */
  public boolean isEavesdropped() {
    return eavesdropped;
  }
  
  /**
   * Sets the eavesdrop status of this message.
   * 
   * @param eavesdropped {@code true} iff this message was eavesdropped
   * @return this payload
   */
  public RouteMessagePayload setEavesdropped(boolean eavesdropped) {
    this.eavesdropped = eavesdropped;
    return this;
  }
  
  /**
   * Retrieves the message to be routed.
   * 
   * @return the message
   */
  public JSONObject getMessage() {
    return message;
  }
  
  /**
   * Sets the message to be routed.
   * 
   * @param message the message
   * @return this payload
   */
  public RouteMessagePayload setMessage(JSONObject message) {
    this.message = message;
    return this;
  }
  
  /**
   * Retrieves the original sender of the payload.
   * 
   * @return the sender's identification or <code>null</code> if the message
   *         originates from a source unknown to the dispatcher
   */
  public String getSender() {
    return sender;
  }
  
  /**
   * Sets the original sender of the payload.
   * 
   * @param sender the sender's identification or <code>null</code> if the
   *        message originates from a source unknown to the dispatcher
   * @return this payload
   */
  public RouteMessagePayload setSender(String sender) {
    this.sender = sender;
    return this;
  }
  
  /**
   * Retrieves the intended recipient of the payload.
   * 
   * @return the recipient's identification or <code>null</code> if the message
   *         is to be broadcasted to all known listeners
   */
  public String getRecipient() {
    return recipient;
  }
  
  /**
   * Sets the intended recipient of the payload.
   * 
   * @param recipient the recipient's identification or <code>null</code> if
   *        the message is to be broadcasted to all known members
   * @return this payload
   */
  public RouteMessagePayload setRecipient(String recipient) {
    this.recipient = recipient;
    return this;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public JSONObject serialize() throws MalformedPayloadException {
    if(message == null)
      throw new MalformedPayloadException(action, "Invalid payload.");
    
    return new JSONObject()
        .put(ACTION_VAR, action.name())
        .put(MESSAGE_VAR, message)
        .put(SENDER_VAR, sender == null ? JSONObject.NULL : sender)
        .put(RECIPIENT_VAR, recipient == null ? JSONObject.NULL : recipient)
        .put(EAVESDROPPED_VAR, eavesdropped);
  }
  
}
