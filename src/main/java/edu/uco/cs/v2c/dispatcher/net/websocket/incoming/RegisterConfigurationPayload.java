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
 * Encapsulates the incoming {@link IncomingAction#REGISTER_CONFIGURATION} payload.
 * 
 * @author Caleb L. Power
 */
public class RegisterConfigurationPayload extends IncomingPayload {

  private static final String APP_VAR = "app";
  private static final String CONFIG_VAR = "config";
  
  private JSONObject config = null;
  private String app = null;
  
  /**
   * Instantiates the REGISTER_CONFIGURATION payload.
   * 
   * @param raw the raw JSON
   * @throws PayloadHandlingException if the payload was invalid
   */
  public RegisterConfigurationPayload(JSONObject raw) throws PayloadHandlingException {
    super(raw, IncomingAction.REGISTER_CONFIGURATION);
    
    try {
      app = raw.getString(APP_VAR).toLowerCase();
      config = raw.getJSONObject(CONFIG_VAR);
    } catch(JSONException e) {
      throw new PayloadHandlingException(action, e, raw);
    }
  }
  
  /**
   * Retrieves the unique identifier of the application in question.
   * 
   * @return the application's unique identifier
   */
  public String getApp() {
    return app;
  }
  
  /**
   * Retrieves the default or current configuration denoted by the application.
   * 
   * @return the configuration JSON object
   */
  public JSONObject getConfig() {
    return config;
  }
  
}
