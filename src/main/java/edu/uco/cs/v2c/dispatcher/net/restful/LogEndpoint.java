package edu.uco.cs.v2c.dispatcher.net.restful;


import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.log.LogPrinter;
import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import spark.Request;
import spark.Response;

public class LogEndpoint extends Endpoint {
	
	private static JSONObject log = null;
	  
	
	
	public LogEndpoint() {
		super("/Log",APIVersion.VERSION_1,HTTPMethod.GET);
	}
	
	@Override public JSONObject doEndpointTask(Request req, Response res) throws EndpointException{
		log = null;
		log = LogPrinter.getTrafficLogJSON();
		return log;
	
	}
}
