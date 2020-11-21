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
package edu.uco.cs.v2c.dispatcher.net.websocket;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload;
import edu.uco.cs.v2c.dispatcher.net.websocket.incoming.IncomingPayload.IncomingAction;

/**
 * An exception be thrown if there's an issue
 * with an incoming WebSocket message.
 * 
 * @author Caleb L. Power
 */
public class PayloadHandlingException extends Exception {
  private static final long serialVersionUID = -2474710163721967366L;
  
  private JSONObject offendingPayload = null;

  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingPayload}
   */
  public PayloadHandlingException(IncomingAction action) {
    super(String.format("Unknown error occurred on %1$s.",
        action == null ? "nonexistent action" : action.toString()));
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingPayload}
   * @param message additional details regarding the exception
   */
  public PayloadHandlingException(IncomingAction action, String message) {
    super(String.format("Error occurred on %1$s: %2$s.",
        (action == null ? "nonexistent action" : action.toString()),
        message));
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingPayload}
   * @param payload the offending payload
   */
  public PayloadHandlingException(IncomingAction action, JSONObject payload) {
    this(action);
    this.offendingPayload = payload;
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingAction}
   * @param message additional details regarding the exception
   * @param payload the offending payload
   */
  public PayloadHandlingException(IncomingAction action, String message, JSONObject payload) {
    this(action, message);
    this.offendingPayload = payload;
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingAction}
   * @param cause the cause of this exception
   */
  public PayloadHandlingException(IncomingAction action, Throwable cause) {
    super(String.format("Error occurred on %1$s: %2$s",
        (action == null ? "nonexistent action" : action.toString()),
        (cause.getMessage() == null ? "no further information available"
            : cause.getMessage())),
        cause);
  }
  
  /**
   * Overloaded constructor.
   * 
   * @param action the associated {@link IncomingAction}
   * @param cause the cause of this exception
   * @param payload the offending payload
   */
  public PayloadHandlingException(IncomingAction action, Throwable cause, JSONObject payload) {
    this(action, cause);
    this.offendingPayload = payload;
  }
  
  /**
   * Determines whether or not there is an offending payload associated with
   * this particular exception.
   * 
   * @return <code>true</code> iff the {@link IncomingPayload} is not <code>null</code>.
   */
  public boolean hasOffendingPayload() {
    return offendingPayload != null;
  }
  
  /**
   * Retrieves the offending payload if it exists.
   * 
   * @return the appropriate {@link IncomingPayload} if it exists
   */
  public JSONObject getOffendingPayload() {
    return offendingPayload;
  }
  
}
