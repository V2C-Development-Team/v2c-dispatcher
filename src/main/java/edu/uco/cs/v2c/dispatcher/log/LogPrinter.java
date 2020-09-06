/*
 * Copyright (c) 2019 Axonibyte Innovations, LLC. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uco.cs.v2c.dispatcher.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.axonibyte.bonemesh.listener.LogListener;

/**
 * Log manager.
 * 
 * @author Caleb L. Power
 */
public class LogPrinter implements LogListener {
  
  private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
  
  private BoneMeshLogCatcher boneMeshLogCatcher = null;
  private Calendar calendar = null;
  
  /**
   * Null constructor.
   */
  public LogPrinter() {
    this.boneMeshLogCatcher = new BoneMeshLogCatcher();
    this.calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onDebug(String label, String message, long timestamp) {
    log("DEBUG", label, message, timestamp);
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onInfo(String label, String message, long timestamp) {
    log("INFO", label, message, timestamp);
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onError(String label, String message, long timestamp) {
    log("ERROR", label, message, timestamp);
  }
  
  private void log(String type, String label, String message, long timestamp) {
    calendar.setTimeInMillis(timestamp);
    System.out.println(
        String.format("[%1$s: %2$s] %3$s - %4$s",
            sdf.format(calendar.getTime()),
            type,
            label,
            message));
  }
  
  /**
   * Retrieves the BoneMesh-specific logger.
   * 
   * @return the BoneMesh logger
   */
  public BoneMeshLogCatcher getBoneMeshLogCatcher() {
    return boneMeshLogCatcher;
  }
  
  private class BoneMeshLogCatcher implements LogListener {
    
    /**
     * {@inheritDoc}
     */
    @Override public void onDebug(String label, String message, long timestamp) {
      // LogPrinter.this.onDebug("<BONEMESH DEBUG> " + label, message, timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void onInfo(String label, String message, long timestamp) {
      LogPrinter.this.onDebug("<BONEMESH INFO> " + label, message, timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void onError(String label, String message, long timestamp) {
      LogPrinter.this.onDebug("<BONEMESH ERROR> " + label, message, timestamp);
    }
    
  }

}
