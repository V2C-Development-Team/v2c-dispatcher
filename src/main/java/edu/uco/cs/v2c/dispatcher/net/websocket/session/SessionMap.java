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
package edu.uco.cs.v2c.dispatcher.net.websocket.session;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import edu.uco.cs.v2c.dispatcher.net.websocket.RegisteredSession;

/**
 * Denotes a container of indexed registered sessions.
 * 
 * @author Caleb L. Power
 */
public class SessionMap {
  
  private Map<Session, RegisteredSession> sessionsBySession = new ConcurrentHashMap<>();
  private Map<String, RegisteredSession> sessionsByName = new ConcurrentHashMap<>();
  
  /**
   * Registers a session.
   * 
   * @param registeredSession the registered session
   */
  public void register(RegisteredSession registeredSession) {
    sessionsBySession.put(registeredSession.getSession(), registeredSession);
    sessionsByName.put(registeredSession.getName(), registeredSession);
  }
  
  /**
   * Deregisters a session.
   * 
   * @param registeredSession the registered session
   */
  public void deregister(RegisteredSession registeredSession) {
    sessionsBySession.remove(registeredSession.getSession());
    sessionsByName.remove(registeredSession.getName());
  }
  
  /**
   * Deregisters a session.
   * 
   * @param sessionName the name of the registered session
   */
  public void deregister(String sessionName) {
    RegisteredSession registeredSession = get(sessionName);
    if(registeredSession != null) deregister(registeredSession);
  }
  
  /**
   * Deregisters a session.
   * 
   * @param session the session itself
   */
  public void deregister(Session session) {
    RegisteredSession registeredSession = get(session);
    if(registeredSession != null) deregister(registeredSession);
  }
  
  /**
   * Retrieves a registered session.
   * 
   * @param sessionName the name of the session
   * @return the registered session
   */
  public RegisteredSession get(String sessionName) {
    return sessionsByName.get(sessionName);
  }
  
  /**
   * Retrieves a registered session.
   * 
   * @param session the session itself
   * @return the registered session
   */
  public RegisteredSession get(Session session) {
    return sessionsBySession.get(session);
  }
  
  /**
   * Determines whether or not the map contains the session name.
   * 
   * @param sessionName the session name
   * @return <code>true</code> iff the map contains the session name
   */
  public boolean containsKey(String sessionName) {
    return sessionsByName.containsKey(sessionName);
  }
  
  /**
   * Determines whether or not the map contains the session itself.
   * 
   * @param session the session itself
   * @return <code>true</code> iff the map contains the session itself
   */
  public boolean containsKey(Session session) {
    return sessionsBySession.containsKey(session);
  }
  
  /**
   * Retrieves a set of all name keys in the map.
   * 
   * @return the complete set of names
   */
  public Set<String> getNames() {
    return sessionsByName.keySet();
  }
  
  /**
   * Retrieves a set of all sessions in the map.
   * 
   * @return the complete set of sessions
   */
  public Set<Session> getSessions() {
    return sessionsBySession.keySet();
  }
  
  /**
   * Retrieves a collection of all registered sessions in the map.
   * 
   * @return the complete collection of registered sessions
   */
  public Collection<RegisteredSession> getRegisteredSessions() {
    return sessionsBySession.values();
  }
  
  /**
   * Retrieves a map of registered sessions keyed by their sessions.
   * 
   * @return the map of registered sessions
   */
  public Map<Session, RegisteredSession> getSessionsBySession() {
    return sessionsBySession;
  }
  
  /**
   * Retrieves a map of registered sessions keyed by their names.
   * 
   * @return the map of registered sessions
   */
  public Map<String, RegisteredSession> getSessionsByName() {
    return sessionsByName;
  }
  
}
