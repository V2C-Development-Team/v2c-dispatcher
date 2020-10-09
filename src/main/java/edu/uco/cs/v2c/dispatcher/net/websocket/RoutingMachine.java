package edu.uco.cs.v2c.dispatcher.net.websocket;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import edu.uco.cs.v2c.dispatcher.V2CDispatcher;


public class RoutingMachine implements Runnable {
	
	private static final String TARGET_KEYWORD = "target";
	private static final String LABEL = "DISPATCHER-ROUTINGMACHINE";
	
	private LinkedList<String> incomingBuffer = new LinkedList<>();//buffer for incoming commands
	private LinkedList<String> confirmedBuffer = new LinkedList<>();// buffer for building targeted outgoing commands to modules
	private RoutingListener listener = null; //listener for routing
	private Thread thread = null; // the thread the RoutingMachine operates in.
	private AtomicReference<RegisteredSession> target = new AtomicReference<>();
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
		return machine;
	}
	
	/*
	 * flushes the outgoing buffer of data and reconstruct the message.
	 * */
	private void flush() {
		if(confirmedBuffer.isEmpty()) return; //if buffer is empty it is flushed
		
		StringBuilder stringBuilder = new StringBuilder();//a builder for building the outgoing command
		do
			stringBuilder.append(confirmedBuffer.remove(0)).append(' ');
		while(!confirmedBuffer.isEmpty());// while buffer !empty append token at 0, and a space
		// this is essentially reconstructing our tokenized string
		listener.onRoute(target.get(), stringBuilder.toString().stripTrailing());
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
          
          if(WebSocketHandler.getSessionMap().containsKey(token)) { // if token is among registeredsession names
            V2CDispatcher.getLogger().logDebug(LABEL, String.format("Changing recipient: %1$s", token));
            flush();// send message and flush outgoing
            target.setRelease(WebSocketHandler.getSessionMap().get(token)); // current recipient = token, need to set recipient in handler
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
	 * kill switch for the RoutingMachine thread
	 * */
	public void kill() {
		thread.interrupt();
	}
	
	public RegisteredSession getTarget() {
	  return target.get();
	}

}
