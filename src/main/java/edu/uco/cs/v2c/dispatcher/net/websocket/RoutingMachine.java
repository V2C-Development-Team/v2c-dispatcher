package edu.uco.cs.v2c.dispatcher.net.websocket;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.websocket.api.Session;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;


public class RoutingMachine implements Runnable {
	
	private static final String TARGET_KEYWORD = "target";
	private static final String LABEL = "DISPATCHER-ROUTINGMACHINE";
	
	private LinkedList<String> incomingBuffer = new LinkedList<>();//buffer for incoming commands
	private LinkedList<String> confirmedBuffer = new LinkedList<>();// buffer for building targeted outgoing commands to modules
	private RoutingListener listener = null; //listener for routing
	private Map<Session,RegisteredSession> registeredSessions = null; //map of connected modules - replaces modules Set in demo
	private String recipient = null; //string representing the targeted module. will "map" to RegisteredSession name, which in turn maps to a session.
	private Thread thread = null; // the thread the RoutingMachine operates in.
	private AtomicReference<Session> target = new AtomicReference<>(); // it's magic
	private RegisteredSession holder = null;
	/*
	 * private contstructor for routing machine. not called except internally, use build function to create
	 * @param listener is a concrete instance of a routing listener
	 * */
	private RoutingMachine(RoutingListener listener) {
		this.listener = listener;
	}
	
	
	/*
	 * Defacto constructor for RoutingMachine. instantiates machine, instantiates machine's thread and returns
	 * a reference to the machine
	 * @param listener is a concrete instance of a routing listener
	 * */
	public static RoutingMachine build(RoutingListener listener) {
		RoutingMachine machine = new RoutingMachine(listener);
		machine.thread = new Thread(machine);
		machine.thread.setDaemon(true);
		machine.thread.start();
		machine.registeredSessions = WebSocketHandler.getRegisteredSessions();
		return machine;
	}
	
	/*
	 * flushes the outgoing buffer of data and reconstruct the message.
	 * */
	private void flush() {
	  System.out.println("!!!!!!!!!!!!!!!! FLUSH");
		if(confirmedBuffer.isEmpty()) return; //if buffer is empty it is flushed
		System.out.println("!!!!!!!!!!!!!!!! FLUSH x 2");
		
		StringBuilder stringBuilder = new StringBuilder();//a builder for building the outgoing command
		do
			stringBuilder.append(confirmedBuffer.remove(0)).append(' ');
		while(!confirmedBuffer.isEmpty());// while buffer !empty append token at 0, and a space
		// this is essentially reconstructing our tokenized string
		listener.onRoute(target.get(), stringBuilder.toString().stripTrailing(), recipient);
		System.out.println("!!!!!!!!!!!!!!!! END FLUSH");
	}
	
	
	/*
	 * Takes the string off command data, and tokenizes it for processing.
	 * @param input is a string that represents a command from the recognizer
	 * 
	 *
	 * */
	public void queue(String input) {
		String tokens [] = input.split("\\s+"); // take the input string and tokenize it, 1-* spaces are token delimiters.
		synchronized(incomingBuffer) {
			for(String token: tokens)
				incomingBuffer.add(token.toLowerCase());// synchonized for multithreading, add each token to buffer
			incomingBuffer.notifyAll(); //notify all waiting threads of update.
		}
	}
	
	
  /*
   * Logic for the process of routing the thread continually handles.
   */
  @Override public void run() {
    try {
      boolean foundTarget = false; // a flag to indicate if the target word was found in the command
      
      while(!thread.isInterrupted()) { // continue while there is no interrupt
        String token = null; // initilize a placeholder for the token

        synchronized(incomingBuffer) {
          while(incomingBuffer.isEmpty()) {
            flush();// send message and flush outgoing
            incomingBuffer.wait();// wait until buffer has data
          }
          token = incomingBuffer.remove(0); // buff !empty, token is first position in buffer
          incomingBuffer.notifyAll();// notify all threads of update
        }
        
        // branch for if you have already located target_keyword in command
        if(foundTarget) {
          
          if(appRegistered(registeredSessions, token)) { // if token is among registeredsession names
            V2CDispatcher.getLogger().logDebug(LABEL, String.format("Changing recipient: %1$s", token));
            flush();// send message and flush outgoing
            recipient = token;// current recipient = token, need to set recipient in handler
          } else {
            V2CDispatcher.getLogger().logDebug(LABEL, String.format("Invalid routing target: %1$s", token));
            confirmedBuffer.add(TARGET_KEYWORD);
            confirmedBuffer.add(token);
          }
          
          foundTarget = false;
        } else if(token.equals(TARGET_KEYWORD)) {
          V2CDispatcher.getLogger().logDebug(LABEL, "Found routing keyword");
          foundTarget = true;
        } // branch if current token from is target keyword
        else {
          V2CDispatcher.getLogger().logDebug(LABEL, String.format("Adding token: %1$s to confirmed buffer", token));
          confirmedBuffer.add(token);
        } // branch if it is a normal token, and target is not found

      }
    } catch(InterruptedException e) {}

  }
		
	
	/*
	 * A function to check the Map of Session->RegisteredSession to see if the appName token matches 
	 * any of the RegisteredSession names, additionally mutates the recipient in an ugly way, that makes 
	 * me sad :(
	 * @param registeredSessions, the registeredSessions<Session,RegisteredSession> map from WS Handler
	 * @param appName, the token currently being processed.
	 * @return isAppregistered, true if token matches on of the registered session names.
	 * */
	public boolean appRegistered(Map<Session,RegisteredSession> registeredSessions, String appName) {
		boolean isAppRegistered = false;
		LinkedList<RegisteredSession> appList = new LinkedList<>(); // make a new linked list so we can iterate
		registeredSessions.forEach((k,v)->{
			if(k.isOpen())
		appList.push(v); // add all the registered to the list
		});
		
		for(RegisteredSession registeredSession: appList) {
			if (registeredSession.getName().equals(appName)) {
				isAppRegistered = true; // if app name matches registered is true
		        holder = registeredSession; // horrible mutation :/ tracks which value we need
			}
				
		}
		if(isAppRegistered) { // if the above logic hit go here too
		registeredSessions.forEach((k,v) -> { // for each in the map
		if(holder.equals(v)) // if our holder equals the current vale
		{
			target.setRelease(k); // then our target is session k, and we can mutate that too :(((
		}
		});
		}
		
		
		return isAppRegistered;
	}
	
	
	
	/*
	 * kill switch for the RoutingMachine thread
	 * */
	public void kill() {
		thread.interrupt();
	}
	
	public Session getTarget() {
	  return target.get();
	}

}
