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
package edu.uco.cs.v2c.dispatcher.utility;

import org.eclipse.jetty.websocket.api.Session;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.websocket.RegisteredSession;
import edu.uco.cs.v2c.dispatcher.net.websocket.WebSocketHandler;

import java.util.Map;
/*
 * A timer action that Disconnects the sesssion should they fail to 
 * register a listener within the time limit
 * 
 * @author Jon Craig
 * 
 * */




public class ListenerRegistrationTimerAction implements TimerAction{
	
	private static final String LOG_LABEL = "WEBSOCKET HANDLER/REGISTRATIONTIMER";

	@Override public void onAction(Session session) {
		try {
  		if(!WebSocketHandler.getSessionMap().containsKey(session)) {
  			V2CDispatcher.getLogger().logError(LOG_LABEL,
  					String.format("%1$s:%2$d listener not registered in time.",
  					session.getRemoteAddress().getHostString(),
  	                session.getRemoteAddress().getPort()));
      		session.close(1, "OnConnect- Sesson listener not registered in time.");// if not registered disconnect
      }
    } catch (NullPointerException e){
			V2CDispatcher.getLogger().logError(LOG_LABEL, "Session closed before timer elapse");
		}
	}
	
}
