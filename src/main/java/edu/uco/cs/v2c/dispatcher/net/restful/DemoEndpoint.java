package edu.uco.cs.v2c.dispatcher.net.restful;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import spark.Request;
import spark.Response;

/**
 * A demonstration of an endpoint.
 * 
 * @author Caleb L. Power
 */
public class DemoEndpoint extends Endpoint {
  
  /**
   * Instantiates endpoint.
   */
  public DemoEndpoint() {
    super("/demo", APIVersion.VERSION_1, HTTPMethod.GET);
  }
  

  /**
   * {@inheritDoc}
   */
  @Override public JSONObject doEndpointTask(Request req, Response res) throws EndpointException {
    return new JSONObject()
        .put("status", "ok")
        .put("info", "You've hit the demo endpoint.");
  }
  
}
