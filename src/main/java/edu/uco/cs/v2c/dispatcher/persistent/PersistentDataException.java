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
package edu.uco.cs.v2c.dispatcher.persistent;

/**
 * Exception to be thrown if there are errors connecting to a persistent data store.
 * 
 * @author Caleb L. Power
 */
public class PersistentDataException extends Exception {
  private static final long serialVersionUID = -8204810678827366624L;

  /**
   * Details relating to the exception.
   * 
   * @author Caleb L. Power
   */
  public static enum ExceptionDetail {
    
    /**
     * To be used if the exception relates to the configuration file.
     */
    CONFIG,
    
    /**
     * To be used if the exception relates to the database.
     */
    DATABASE
    
  }
  
  private ExceptionDetail exceptionDetail = null;
  
  /**
   * Overloaded constructor to specify the exception detail.
   * 
   * @param detail details regarding the exception that is to be thrown
   */
  public PersistentDataException(ExceptionDetail detail) {
    this.exceptionDetail = detail;
  }
  
  /**
   * Overloaded constructor to specify both the exception detail and the cause
   * of the exception to be thrown.
   * 
   * @param detail details regarding the exception that is to be thrown
   * @param cause the Throwable cause of the exception (if this exception is
   *        a wrapper for a previously-thrown exception)
   */
  public PersistentDataException(ExceptionDetail detail, Throwable cause) {
    super(cause);
    this.exceptionDetail = detail;
  }
  

  /**
   * Overloaded constructor to specify the exception detail.
   * 
   * @param detail details regarding the exception that is to be thrown
   * @param message a helpful message
   */
  public PersistentDataException(ExceptionDetail detail, String message) {
    super(message);
    this.exceptionDetail = detail;
  }
  
  /**
   * Overloaded constructor to specify both the exception detail and the cause
   * of the exception to be thrown.
   * 
   * @param detail details regarding the exception that is to be thrown
   * @param cause the Throwable cause of the exception (if this exception is
   *        a wrapper for a previously-thrown exception)
   * @param message a helpful message
   */
  public PersistentDataException(ExceptionDetail detail, Throwable cause, String message) {
    super(message, cause);
    this.exceptionDetail = detail;
  }
  
  
  /**
   * Retrieves the details regarding the exception.
   * 
   * @return ExceptionDetail the exception details
   */
  public ExceptionDetail getExceptionDetail() {
    return exceptionDetail;
  }
  
}
