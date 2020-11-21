/*
 * Copyright (c) 2020 Caleb L. Power, Everistus Akpabio, Rashed Alrashed,
 * Nicholas Clemmons, Jonathan Craig, James Cole Riggall, and Glen Mathew.
 * All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uco.cs.v2c.dispatcher.net.restful;

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import edu.uco.cs.v2c.dispatcher.net.websocket.RegisteredSession;
import edu.uco.cs.v2c.dispatcher.net.websocket.WebSocketHandler;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.websocket.api.Session;


public class RegisteredSessionsEndpoint extends Endpoint {
	private static List<String> moduleNames = null;
	//Instantiates the endpoint
	public RegisteredSessionsEndpoint() {
		super("/registeredsessions",APIVersion.VERSION_1, HTTPMethod.GET);
	}
	
	@Override public JSONObject doEndpointTask(Request req, Response res) throws EndpointException{
		return new JSONObject()
		    .put("Connected Applications", WebSocketHandler.getSessionMap().getSessionsByName().keySet());
	}
	
}
