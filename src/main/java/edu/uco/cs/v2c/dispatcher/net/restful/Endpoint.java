/*
 * Copyright (c) 2019 Axonibyte Innovations, LLC. All rights reserved.
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

package edu.uco.cs.v2c.dispatcher.net.restful;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import spark.Request;
import spark.Response;

/**
 * Module abstract class for the easy-adding of custom pages.
 * 
 * @author Caleb L. Power
 */
public abstract class Endpoint {
  
  private HTTPMethod[] methods = null;
  private String route = null;
  
  /**
   * Overloaded constructor to set the request type, the route, and the version.
   * 
   * @param resource the public resource
   * @param version the version of the endpoint
   * @param httpMethods the HTTP methods that can be used on the route
   */
  protected Endpoint(String resource, APIVersion version, HTTPMethod... httpMethods) {
    this(String.format("/v%1$d%2$s", version.getVal(), resource), httpMethods);
  }
  
  /**
   * Overloaded constructor to set the request type and the route.
   * 
   * @param route the public endpoint
   * @param methods the HTTP methods that can can be used on the route
   */
  protected Endpoint(String route, HTTPMethod... methods) {
    this.route = route;
    this.methods = methods;
    StringBuilder stringBuilder = new StringBuilder();
    if(methods.length == 0) {
      V2CDispatcher.getLogger().logError("API",
          String.format("WARNING: Route %1$s loaded without HTTP methods.", route));
    } else {
      for(int i = 0; i < methods.length; i++) {
        stringBuilder.append(methods[i].name());
        if(i < methods.length - 1) stringBuilder.append(", ");
      }
      V2CDispatcher.getLogger().logDebug("API",
          String.format("Loaded route %1$s with HTTP method(s) %2$s", route, stringBuilder.toString()));
    }
  }
  
  /**
   * Retrieve the HTTP method types for this route.
   * 
   * @return array of type HTTPMethod
   */
  public HTTPMethod[] getHTTPMethods() {
    return methods;
  }
  
  /**
   * Retrieve the route for the module.
   * 
   * @return String representing the route to be used for the module.
   */
  public String getRoute() {
    return route;
  }
  
  /**
   * The actions that will be carried out for all routes.
   * 
   * @param request REST request
   * @param response REST response
   * @return ModelAndView containing the HTTP response (often in JSON)
   */
  public String onRequest(Request request, Response response) {
    try {
      V2CDispatcher.getLogger().logInfo("API", String.format("%1$s accessed %2$s %3$s.",
          request.ip(),
          request.requestMethod(),
          request.pathInfo()));
      
      return doEndpointTask(request, response)
          .toString(2) + '\n';
    } catch(EndpointException e) {
      V2CDispatcher.getLogger().logError("API", String.format("Response code %1$d: %2$s (%3$s)",
          e.getErrorCode(),
          e.getMessage(),
          e.toString()));
      response.status(e.getErrorCode());
      if(e.getErrorCode() >= 500) e.printStackTrace();
      return new JSONObject()
          .put("status", "error")
          .put("info", e.toString())
          .toString(2) + '\n';
    } catch(Exception e) { // if we hit this block, something has gone terribly wrong (or the developer is dumb)
      V2CDispatcher.getLogger().logError("API", e.getMessage());
      e.printStackTrace();
      response.status(500);
      return new JSONObject()
          .put("status", "error")
          .put("info", "Internal server error.")
          .toString(2) + '\n';
    }
  }
  
  /**
   * The action in question for the particular module.
   * 
   * @param request HTTP request
   * @param response HTTP response
   * @return ModelAndView containing the HTTP response (often in JSON)
   * @throws EndpointException thrown if the response is not good
   */
  public abstract JSONObject doEndpointTask(Request request, Response response) throws EndpointException;
  
}