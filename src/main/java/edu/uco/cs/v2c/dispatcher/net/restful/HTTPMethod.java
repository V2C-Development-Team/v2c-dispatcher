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
 * 
 * THIS FILE HAS BEEN MODIFIED. ORIGINAL WORK ADHERES TO THE FOLLOWING NOTICE.
 * 
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
package edu.uco.cs.v2c.dispatcher.net.restful;

import java.util.function.BiConsumer;

import spark.Route;

/**
 * HTTP request methods.
 * 
 * @author Caleb L. Power
 */
public enum HTTPMethod {
  
  /**
   * HTTP DELETE method.
   */
  DELETE(spark.Spark::delete),
  
  /**
   * HTTP GET method.
   */
  GET(spark.Spark::get),
  
  /**
   * HTTP PATCH method.
   */
  PATCH(spark.Spark::patch),
  
  /**
   * HTTP POST method.
   */
  POST(spark.Spark::post),
  
  /**
   * HTTP PUT method.
   */
  PUT(spark.Spark::put);
  
  private BiConsumer<String, Route> sparkMethod = null;
  
  private HTTPMethod(BiConsumer<String, Route> sparkMethod) {
    this.sparkMethod = sparkMethod;
  }
  
  /**
   * Retrieves the Spark method associated with this HTTPMethod.
   * 
   * @return the appropriate BiConsumer
   */
  public BiConsumer<String, Route> getSparkMethod() {
    return sparkMethod;
  }
  
}
