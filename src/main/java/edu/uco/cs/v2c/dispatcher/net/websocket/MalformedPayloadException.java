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

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.OutgoingPayload.OutgoingAction;

/**
 * An exception be thrown if there's an issue
 * with an incoming WebSocket message.
 * 
 * @author Caleb L. Power
 */
public class MalformedPayloadException extends Exception {
  private static final long serialVersionUID = -2474710163721967366L;
  
  private JSONObject offendingPayload = null;

  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   */
  public MalformedPayloadException(OutgoingAction action) {
    super(String.format("Unknown error occurred on %1$s.",
        action == null ? "nonexistent action" : action.toString()));
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   * @param message additional details regarding the exception
   */
  public MalformedPayloadException(OutgoingAction action, String message) {
    super(String.format("Error occurred on %1$s: %2$s.",
        (action == null ? "nonexistent action" : action.toString()),
        message));
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   * @param payload the offending payload
   */
  public MalformedPayloadException(OutgoingAction action, JSONObject payload) {
    this(action);
    this.offendingPayload = payload;
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   * @param message additional details regarding the exception
   * @param payload the offending payload
   */
  public MalformedPayloadException(OutgoingAction action, String message, JSONObject payload) {
    this(action, message);
    this.offendingPayload = payload;
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   * @param cause the cause of this exception
   */
  public MalformedPayloadException(OutgoingAction action, Throwable cause) {
    super(String.format("Error occurred on %1$s: %2$s",
        (action == null ? "nonexistent action" : action.toString()),
        (cause.getMessage() == null ? "no further information available"
            : cause.getMessage())),
        cause);
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link OutgoingAction}
   * @param cause the cause of this exception
   * @param payload the offending payload
   */
  public MalformedPayloadException(OutgoingAction action, Throwable cause, JSONObject payload) {
    this(action, cause);
    this.offendingPayload = payload;
  }
  
  /**
   * Determines whether or not there is an offending payload associated with
   * this particular exception.
   * 
   * @return <code>true</code> iff the {@link OutgoingAction} is not <code>null</code>.
   */
  public boolean hasOffendingPayload() {
    return offendingPayload != null;
  }
  
  /**
   * Retrieves the offending payload if it exists.
   * 
   * @return the appropriate {@link OutgoingAction} if it exists
   */
  public JSONObject getOffendingPayload() {
    return offendingPayload;
  }
  
}
