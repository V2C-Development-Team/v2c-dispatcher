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
