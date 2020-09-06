package edu.uco.cs.v2c.dispatcher.net.websocket.outgoing;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.MalformedPayloadException;

/**
 * An outgoing payload to be sent over a WebSocket for the purposes of
 * interacting with the chatroom functionality.
 * 
 * @author Caleb L. Power
 */
public abstract class OutgoingPayload {
  
  /**
   * Actions denoting the purpose of an outgoing message.
   * Such messages are generally sent by this system.
   * 
   * @author Caleb L. Power
   */
  public static enum OutgoingAction {
    
    /**
     * Denotes that the payload regards some error that has occurred, generally
     * (but not necessarily) in response to some action by a third party.
     */
    WEBSOCKET_ERROR
    
  }
  
  protected static final String ACTION_VAR = "action";
  
  protected OutgoingAction action = null;
  
  protected OutgoingPayload(OutgoingAction action) {
    this.action = action;
  }
  
  /**
   * Retrieves the {@link OutgoingAction} if it exists.
   * 
   * @return the appropriate {@link OutgoingAction}
   */
  public OutgoingAction getAction() {
    return action;
  }
  
  /**
   * Serializes the encapsulated payload into a JSON object that can be sent
   * over the WebSocket.
   * 
   * @return a {@link JSONObject}
   * @throws MalformedPayloadException if payload is invalid upon serialization
   */
  public abstract JSONObject serialize() throws MalformedPayloadException;
  
  /**
   * {@inheritDoc}
   */
  @Override public String toString() {
    try {
      return serialize().toString();
    } catch(MalformedPayloadException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
}
