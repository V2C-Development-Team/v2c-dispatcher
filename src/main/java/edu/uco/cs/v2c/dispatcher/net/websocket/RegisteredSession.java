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

import org.eclipse.jetty.websocket.api.Session;

public class RegisteredSession {
	
	  private boolean eavesdrop = false;
	  private Session session = null;
	  private String app = null;
	  
	  public RegisteredSession(Session session, String app, boolean eavesdrop) {
	    this.session = session;
		  this.app = app;
		  this.eavesdrop = eavesdrop;
	  }
	  
	  public String getName() {
		  return app;
	  }

	  public boolean isEavesdropper() {
		  return eavesdrop;
	  }
	  
	  public Session getSession() {
	    return session;
	  }
}
