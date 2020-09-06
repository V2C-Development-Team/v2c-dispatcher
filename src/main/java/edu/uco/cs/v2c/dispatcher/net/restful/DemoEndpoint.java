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

import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.net.APIVersion;
import spark.Request;
import spark.Response;

/**
 * A demonstration of an endpoint.
 * 
 * @author Caleb L. Power
 */
public class DemoEndpoint extends Endpoint {
  
  /**
   * Instantiates endpoint.
   */
  public DemoEndpoint() {
    super("/demo", APIVersion.VERSION_1, HTTPMethod.GET);
  }
  

  /**
   * {@inheritDoc}
   */
  @Override public JSONObject doEndpointTask(Request req, Response res) throws EndpointException {
    return new JSONObject()
        .put("status", "ok")
        .put("info", "You've hit the demo endpoint.");
  }
  
}
