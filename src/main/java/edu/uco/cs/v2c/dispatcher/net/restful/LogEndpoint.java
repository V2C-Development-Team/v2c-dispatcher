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


import org.json.JSONArray;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.log.LogPrinter;
import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import spark.Request;
import spark.Response;

public class LogEndpoint extends Endpoint {
	
	private  JSONArray log = null;
	private JSONObject logAsOutput = null;  
	
	
	public LogEndpoint() {
		super("/log",APIVersion.VERSION_1,HTTPMethod.GET);
	}
	
	@Override public JSONObject doEndpointTask(Request req, Response res) throws EndpointException{
		log = LogPrinter.getTrafficLog();// get the trafficLog
		logAsOutput = new JSONObject();//create JSONobject that can be returned w/ doEnpointTask
		if(log.length() > 100) {
			for(int i = log.length() - 100; i < log.length(); i++) {
				logAsOutput.append("Log", log.get(i));
			}
		}
		else logAsOutput.put("LOG",log);//put the log in it
		return logAsOutput;//output
		
	
	}
}
