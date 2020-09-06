/*
 * Copyright (c) 2020 Axonibyte Innovations, LLC. All rights reserved.
 *
 *   Original work Licensed under the Apache License, Version 2.0 (the "License");
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

package edu.uco.cs.v2c.dispatcher.net;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.patch;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;
import static spark.Spark.stop;
import static spark.Spark.webSocket;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.restful.DemoEndpoint;
import edu.uco.cs.v2c.dispatcher.net.restful.Endpoint;
import edu.uco.cs.v2c.dispatcher.net.restful.HTTPMethod;
import edu.uco.cs.v2c.dispatcher.net.websocket.WebSocketHandler;

/**
 * API Driver; manages RESTful and WebSocket API endpoints.
 * 
 * @author Caleb L. Power
 */
public class APIDriver implements Runnable {
  
  private static final String RESPONDER_STATIC_FOLDER = ".";
  private static final String WEBSOCKET_ROUTE = "/socket";
  
  private int port; // the port that the front end should run on
  private Endpoint endpoints[] = null; // the pages that will be accessible
  private String allowedOrigins = null; // the allowed origins for CORS
  private Thread thread = null; // the thread to run the frontend
  
  /**
   * Opens the specified external port so as to launch the front end.
   * 
   * @param port the port by which the front end will be accessible
   * @param allowedOrigins the allowed origins for CORS
   */
  private APIDriver(int port, String allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
    this.port = port;
    
    endpoints = new Endpoint[] {
        new DemoEndpoint()
    };
    
    staticFiles.location(RESPONDER_STATIC_FOLDER); // relative to the root of the classpath
  }

  /**
   * Runs the front end in a separate thread so that it can be halted externally.
   */
  @Override public void run() {
    webSocket(WEBSOCKET_ROUTE, WebSocketHandler.class); // initialize web socket for streaming blocks
    
    V2CDispatcher.getLogger().logInfo("API", "Exposing API on port " + port);
    port(port);
    
    before((req, res) -> {
      res.header("Access-Control-Allow-Origin", allowedOrigins);
      res.header("Access-Control-Allow-Methods", "DELETE, POST, GET, PATCH, PUT, OPTIONS");
      res.header("Access-Control-Allow-Headers",
          "Content-Type, "
            + "Access-Control-Allow-Headers, "
            + "Access-Control-Allow-Origin, "
            + "Access-Control-Allow-Methods, "
            + "Authorization, "
            + "X-Requested-With");
      res.header("Access-Control-Expose-Headers", "Content-Type, Content-Length");
      res.header("Content-Type", "application/json"); 
    });
    
    options("/*", (req, res)-> {
      String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
      if(accessControlRequestHeaders != null)
        res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      
      String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
      if(accessControlRequestMethod != null)
        res.header("Access-Control-Allow-Methods", accessControlRequestMethod);

      return "OK";
    });
    
    for(Endpoint endpoint : endpoints) { // iterate through initialized pages and determine the appropriate HTTP request types
      for(HTTPMethod method : endpoint.getHTTPMethods()) {
        switch(method) {
        case DELETE:
          delete(endpoint.getRoute(), endpoint::onRequest);
          break;
        case GET:
          get(endpoint.getRoute(), endpoint::onRequest);
          break;
        case PATCH:
          patch(endpoint.getRoute(), endpoint::onRequest);
          break;
        case POST:
          post(endpoint.getRoute(), endpoint::onRequest);
          break;
        case PUT:
          put(endpoint.getRoute(), endpoint::onRequest);
          break;
        }
      }
    }
    
    // this is a patch because the WebSocket route overrides Spark.notFound 
    get("*", (req, res) -> {
      if(!req.raw().getPathInfo().equals(WEBSOCKET_ROUTE)) {
        res.type("application/json");
        res.status(404);
        return new JSONObject()
            .put("status", "error")
            .put("info", "Resource not found.")
            .toString(2) + '\n';
      }
      
      return null;
    });
    
  }
  
  /**
   * Stops the web server.
   */
  public void halt() {
    stop();
  }
  
  /**
   * Builds the frontend and launches it in a thread.
   * 
   * @param port the listening port
   * @param allowedOrigins the allowed origins for CORS
   * @return a reference to this FrontEnd object
   */
  public static APIDriver build(int port, String allowedOrigins) {
    APIDriver aPIDriver = new APIDriver(port, allowedOrigins);
    aPIDriver.thread = new Thread(aPIDriver);
    aPIDriver.thread.setDaemon(false);
    aPIDriver.thread.start();
    return aPIDriver;
  }
  
}
