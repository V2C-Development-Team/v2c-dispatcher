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
