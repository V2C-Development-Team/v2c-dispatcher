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
package edu.uco.cs.v2c.dispatcher.net.websocket.incoming;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.PayloadHandlingException;

/**
 * An incoming payload sent over a WebSocket for the purposes of interacting
 * with the chatroom functionality.
 * 
 * @author Caleb L. Power
 */
public class IncomingPayload {
  
  /**
   * Actions denoting the purpose of an incoming message.
   * Such messages are generally sent by an external user or system.
   * 
   * @author Caleb L. Power
   */
  public static enum IncomingAction {
    
    /**
     * Indicates that the payload regards some listener that is requesting to
     * be registered on the network.
     */
    REGISTER_LISTENER,
    
    /**
     * Indicates that the payload regards some listener that is requesting to
     * be deregistered on the network.
     */
    DEREGISTER_LISTENER,
    
    /**
     * Indicates that the payload contains some message that needs to be routed
     * to another module in the system. The message in question will generally
     * regard some application statistic or event.
     */
    DISPATCH_MESSAGE,
    
    /**
     * Indicates that the payload contains some raw partial voice transcription
     * that potentially needs to be routed to some additional module.
     */
    DISPATCH_COMMAND,
    
    /**
     * Indicates that the payload contains some default configuration for a
     * particular application in question.
     */
    REGISTER_CONFIGURATION,
    
    /**
     * Indicates that the payload contains some updated configuration that
     * another module should apply.
     */
    UPDATE_CONFIGURATION
  }
  
  protected static final String ACTION_VAR = "action";
  
  protected IncomingAction action = null;
  protected JSONObject raw = null;
  
  protected IncomingPayload(JSONObject raw, IncomingAction expected) throws PayloadHandlingException {
    this.raw = raw;
    
    try {
      if(null == (action = IncomingAction.valueOf(raw.getString("action"))))
        throw new PayloadHandlingException(null, raw);
    } catch(JSONException e) {
      throw new PayloadHandlingException(action, e, raw);
    }
    
    if(this.action != expected)
      throw new PayloadHandlingException(action, "Wrong payload handler.", raw);
  }
  
  /**
   * Retrieves the {@link IncomingAction} if it exists.
   * 
   * @return the appropriate {@link IncomingAction}
   */
  public IncomingAction getAction() {
    return action;
  }
  
  /**
   * Retrieves the raw {@link JSONObject} message.
   * 
   * @return the appropriate {@link JSONObject}
   */
  public JSONObject getRaw() {
    return raw;
  }
  
}
