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
 */
package edu.uco.cs.v2c.dispatcher.net.websocket.outgoing;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.MalformedPayloadException;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.OutgoingPayload.OutgoingAction;

/**
 * Encapsulates the outgoing {@link OutgoingAction#ROUTE_COMMAND} payload.
 * 
 * @author Caleb L. Power
 */
public class RouteCommandPayload extends OutgoingPayload {
  
  private static final String COMMAND_VAR = "command";
  private static final String RECIPIENT_VAR = "recipient";
  
  private String command = null;
  private String recipient = null;
  
  /**
   * Overloaded constructor to instantiate the payload.
   */
  public RouteCommandPayload() {
    super(OutgoingAction.ROUTE_COMMAND);
  }
  
  /**
   * Retrieves the command to be routed.
   * 
   * @return the command
   */
  public String getCommand() {
    return command;
  }
  
  /**
   * Sets the command to be routed.
   * 
   * @param command the command
   * @return this payload
   */
  public RouteCommandPayload setCommand(String command) {
    this.command = command;
    return this;
  }
  
  /**
   * Retrieves the unique identifier of the recipient.
   * 
   * @return the recipient
   */
  public String getRecipient() {
    return recipient;
  }
  
  /**
   * Sets the payload's intended recipient.
   * 
   * @param recipient the recipient's identification
   * @return this payload
   */
  public RouteCommandPayload setRecipient(String recipient) {
    this.recipient = recipient;
    return this;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public JSONObject serialize() throws MalformedPayloadException {
    if(command == null)
      throw new MalformedPayloadException(action, "Invalid payload.");
    
    return new JSONObject()
        .put(ACTION_VAR, action)
        .put(COMMAND_VAR, command)
        .put(RECIPIENT_VAR, recipient == null ? JSONObject.NULL : recipient);
  }
  
}
