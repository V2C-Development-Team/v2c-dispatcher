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
package edu.uco.cs.v2c.dispatcher.net.websocket.incoming;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.PayloadHandlingException;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;

/**
 * Encapsulates the incoming {@link IncomingAction#DISPATCH_MESSAGE} payload.
 * 
 * @author Caleb L. Power
 */
public class DispatchMessagePayload extends IncomingPayload {
  
  private static final String MESSAGE_VAR = "message";
  private static final String RECIPIENT_VAR = "recipient";
  
  private JSONObject message = null;
  private String recipient = null;
  
  /**
   * Instantiates the DISPATCH_MESSAGE payload.
   * 
   * @param raw the raw JSON
   * @throws PayloadHandlingException if the payload was invalid
   */
  public DispatchMessagePayload(JSONObject raw) throws PayloadHandlingException {
    super(raw, IncomingAction.DISPATCH_MESSAGE);
    
    try {
      message = raw.getJSONObject(MESSAGE_VAR);
      recipient = raw.has(RECIPIENT_VAR) && !raw.isNull(RECIPIENT_VAR) ? raw.getString(RECIPIENT_VAR).toLowerCase() : null;
    } catch(JSONException e) {
      throw new PayloadHandlingException(action, e, raw);
    }
  }
  
  /**
   * Retrieves the embedded datum from the message, which will most likely be
   * the bulk of the message
   * 
   * @return the embedded datum
   */
  public JSONObject getMessage() {
    return message;
  }
  
  /**
   * Retrieves the unique identifier of the listener that the message is
   * intended for.
   * 
   * @return the recipient
   */
  public String getRecipient() {
    return recipient;
  }
  
}
