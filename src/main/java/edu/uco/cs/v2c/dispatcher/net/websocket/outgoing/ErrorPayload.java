package edu.uco.cs.v2c.dispatcher.net.websocket.outgoing;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.websocket.MalformedPayloadException;

/**
 * Encapsulates the outgoing {@link OutgoingPayload.OutgoingAction#WEBSOCKET_ERROR} payload.
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
    if(cause == null
        || info == null)
      throw new MalformedPayloadException(action, "Invalid payload.");
    
    return new JSONObject()
        .put(ACTION_VAR, action)
        .put(INFO_VAR, info)
        .put(CAUSE_VAR, cause == null ? JSONObject.NULL : cause);
  }
  
}
