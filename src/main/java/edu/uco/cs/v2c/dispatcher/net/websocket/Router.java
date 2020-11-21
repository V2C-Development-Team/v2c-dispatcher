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
package edu.uco.cs.v2c.dispatcher.net.websocket;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;
import edu.uco.cs.v2c.dispatcher.net.websocket.outgoing.RouteCommandPayload;

public class Router implements RoutingListener {
	private static final String LABEL = "DISPATCHER-ROUTER";

  @Override public void onRoute(RegisteredSession module, String message) {
    if(module == null) V2CDispatcher.getLogger().logError(LABEL,
        String.format("Could not send message (%1$s), recipient is null", message));
    else if(message.isBlank()) V2CDispatcher.getLogger().logError(LABEL, "Could not send message, message empty");
    else {
      try {
        WebSocketHandler.dispatch(module.getSession(),
            new RouteCommandPayload().setRecipient(module.getName()).setCommand(message).serialize());
      } catch(MalformedPayloadException e) {}
      V2CDispatcher.getLogger().logDebug(LABEL,
          String.format("Sent message (%1$s), to a registered session at %2$s:%3$d", message,
              module.getSession().getRemoteAddress().getHostString(), module.getSession().getRemoteAddress().getPort()));
    }
  }
	
	

}
