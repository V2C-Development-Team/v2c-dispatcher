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
package edu.uco.cs.v2c.dispatcher.net.websocket.incoming;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.PayloadHandlingException;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;

/**
 * Encapsulates the incoming {@link IncomingAction#DISPATCH_COMMAND} payload.
 * 
 * @author Caleb L. Power
 */
public class DispatchCommandPayload extends IncomingPayload {
  
  private static final String COMMAND_VAR = "command";
  
  private String command = null;
  
  /**
   * Instantiates the DISPATCH_COMMAND payload.
   * 
   * @param raw the raw JSON
   * @throws PayloadHandlingException if the payload was invalid
   */
  public DispatchCommandPayload(JSONObject raw) throws PayloadHandlingException {
    super(raw, IncomingAction.DISPATCH_COMMAND);
    
    try {
      command = raw.getString(COMMAND_VAR);
    } catch(JSONException e) {
      throw new PayloadHandlingException(action, e, raw);
    }
  }
  
  /**
   * Retrieves the raw transcribed command, presumably sent by a V2C Recognizer.
   * 
   * @return the raw transcribed command
   */
  public String getCommand() {
    return command;
  }
  
}
