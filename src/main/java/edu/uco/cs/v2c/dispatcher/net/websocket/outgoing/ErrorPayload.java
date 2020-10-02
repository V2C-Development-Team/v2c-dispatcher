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
package edu.uco.cs.v2c.dispatcher.net.websocket.outgoing;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.MalformedPayloadException;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.OutgoingPayload.OutgoingAction;

/**
 * Encapsulates the outgoing {@link OutgoingAction#WEBSOCKET_ERROR} payload.
 * 
 * @author Caleb L. Power
 */
public class ErrorPayload extends OutgoingPayload {
  
  private static final String CAUSE_VAR = "cause";
  private static final String INFO_VAR = "info";
  
  private JSONObject cause = null;
  private String info = null;
  
  /**
   * Overloaded constructor to instantiate the payload.
   */
  public ErrorPayload() {
    super(OutgoingAction.WEBSOCKET_ERROR);
  }
  
  /**
   * Retrieves the cause (the bad payload).
   * 
   * @return the cause or <code>null</code> if it didn't exist or
   *         wasn't a well-formed {@link JSONObject}
   */
  public JSONObject getCause() {
    return cause;
  }
  
  /**
   * Sets the cause (the bad payload).
   * 
   * @param cause the bad payload
   * @return this payload
   */
  public ErrorPayload setCause(JSONObject cause) {
    this.cause = cause;
    return this;
  }
  
  /**
   * Retrieves any information about the error.
   * 
   * @return the additional information
   */
  public String getInfo() {
    return info;
  }
  
  /**
   * Sets the information about the error in question.
   * 
   * @param info the additional information
   * @return this payload
   */
  public ErrorPayload setInfo(String info) {
    this.info = info;
    return this;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public JSONObject serialize() throws MalformedPayloadException {
    if(info == null)
      throw new MalformedPayloadException(action, "Invalid payload.");
    
    return new JSONObject()
        .put(ACTION_VAR, action.name())
        .put(INFO_VAR, info)
        .put(CAUSE_VAR, cause == null ? JSONObject.NULL : cause);
  }
  
}
