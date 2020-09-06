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
    
  }
  
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
