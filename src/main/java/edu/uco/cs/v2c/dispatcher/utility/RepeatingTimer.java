/*
 * Copyright (c) 2020 V2C Development Team. All rights reserved.
 * Licensed under the Version 0.0.1 of the V2C License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at <https://tinyurl.com/v2c-license>.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions 
 * limitations under the License.
 */
package edu.uco.cs.v2c.dispatcher.utility;

/**
 * Represents some action that can be repeated.
 * 
 * @author Caleb L. Power
 */
public class RepeatingTimer implements Runnable {
  
  private long delay = 1L;
  private Runnable runnable = null;
  private Thread thread = null;
  
  private RepeatingTimer(long delay, Runnable runnable) {
    this.delay = delay;
    this.runnable = runnable;
  }
  
  /**
   * Instantiates and starts a repeating timer.
   * 
   * @param delay the amount of time in milliseconds, after which the runnable
   *        will be executed, and after which the runnable will be repeated
   *        after initial execution
   * @param runnable the action to be periodically executed
   * @return the newly-instantiated repeating timer
   */
  public static RepeatingTimer build(long delay, Runnable runnable) {
    RepeatingTimer repeatingTimer = new RepeatingTimer(delay, runnable);
    repeatingTimer.thread = new Thread(repeatingTimer);
    repeatingTimer.thread.setDaemon(true);
    repeatingTimer.thread.start();
    return repeatingTimer;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public void run() {
    try {
      for(;;) {
        Thread.sleep(delay);
        runnable.run();
      }
    } catch(InterruptedException e) { }
  }
  
}
