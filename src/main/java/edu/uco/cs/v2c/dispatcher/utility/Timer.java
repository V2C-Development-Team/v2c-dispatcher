package edu.uco.cs.v2c.dispatcher.utility;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;





/*
 * An implementation of a timer
 * 
 * @author Caleb Powers https://github.com/calebpower/timer-demo.git
 * 
 * adapted for V2C dispatcher under open source by
 * @ author Jon Craig
 */

public class Timer implements Runnable {
	private long delay = 0L; // delay in long
	  private Thread thread = null; //thread for the timer to run in
	  private TimerAction action = null; // action to perform after elapse.
	  private List<Entry<Long, Session>> entries = null; 
	  private Map<Session,String> registeredSessionsMap = null; //ugly and Non OOP, but a workaround
	  
	
	  private Timer(TimerAction action, long delay) {
		  this.action = action;
		  this.delay = delay;
		  this.entries = new LinkedList<>();
	  }
	  
	  /*
	   * Instantiate a timer
	   * @param action: the action the timer will take
	   * @param delay: the delay in seconds
	   * @return: the timer object
	   * */
	  public static Timer build(TimerAction action, long delay, Map<Session,String> registeredSessions ) {
		  Timer timer = new Timer(action, delay * 1000);
		  timer.thread = new Thread(timer);
		  timer.thread.setDaemon(true);
		  timer.thread.start();
		  timer.registeredSessionsMap = registeredSessions;
		  return timer;
	  }
	  

	  
	  
	   // Runs the thread, maintains the action queue in timer
	   
	  @Override public void run() {
		    try {
		      for(;;) {
		        Entry<Long, Session> entry = null;
		        synchronized(entries) {
		          while(entries.size() == 0) {
		            entries.wait();
		          }
		          entry = entries.remove(0);
		        }
		        while(entry.getKey() > System.currentTimeMillis())
		          Thread.sleep(500L);
		        action.onAction(entry.getValue(),this.registeredSessionsMap);
		      }
		    } catch(InterruptedException e) { }
		  }
		  
	  
	  // Stop the thread
	  public void stop() {
		  this.thread.interrupt();
	  }
		  
	  public void queue(Session session) {
		    synchronized(entries) {
		      entries.add(new SimpleEntry<>(System.currentTimeMillis() + delay, session));
		      entries.notifyAll();
		    }
		  }
		  
		  
	  }
	  
	  
	  

