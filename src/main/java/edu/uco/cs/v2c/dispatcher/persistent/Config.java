/*
 * Original work copyright (c) 2019 Axonibyte Innovations, LLC. All rights reserved.
 *
 *   Original work Licensed under the Apache License, Version 2.0 (the "License");
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
 * 
 * Modifications, including integration with the rest of the Software where applicable,
 * are proprietary. Modifications copyright (c) 2019 Tritaurian Holdings, Incorporated.
 * The original source from which this Software was derived is covered under terms
 * relating to "Background Technology" in the applicable written and executed
 * contract(s). 
 */

package edu.uco.cs.v2c.dispatcher.persistent;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dispatcher.persistent.PersistentDataException.ExceptionDetail;

/**
 * Configuration file loader.
 * 
 * @author Caleb L. Power
 */
public class Config {
  
  private static String API_OBJ_KEY = "api";
  private static String PORT_INT_KEY = "port";
  private static String ALLOWED_ORIGINS_STR_KEY = "allowedOrigins";
  
  private JSONObject data = null;
  
  /**
   * Overloaded constructor to load config from raw data.
   * 
   * @param raw the raw data
   * @throws PersistentDataException if the configuration file was invalid
   */
  public Config(String raw) throws PersistentDataException {
    if(raw == null) throw new PersistentDataException(ExceptionDetail.CONFIG,
        "Configuration file could not be read (does it exist?)");
    
    try {
      data = new JSONObject(raw);
      
      JSONObject apiObject = data.getJSONObject(API_OBJ_KEY);
      int apiObjectPort = apiObject.getInt(PORT_INT_KEY);
      if(apiObjectPort < 0 || apiObjectPort > 65535)
        throw new PersistentDataException(ExceptionDetail.CONFIG,
            "Integer value in config api.port is out of bounds.");
      
    } catch(JSONException e) {
      throw new PersistentDataException(ExceptionDetail.CONFIG, e);
    }
  }
  
  /**
   * Retrieves a representation of the default configuration.
   * 
   * @return the JSON representation of the default configuration
   */
  public static JSONObject getDefaultConfig() {
    return new JSONObject()
        .put(API_OBJ_KEY, new JSONObject()
            .put(PORT_INT_KEY, 2585)
            .put(ALLOWED_ORIGINS_STR_KEY, "*"));
  }
  
  /**
   * Retrieves the exposed HTTP port for incoming API requests.
   * 
   * @return integer denoting the exposed HTTP port
   */
  public int getAPIPort() {
    return data.getJSONObject(API_OBJ_KEY).getInt(PORT_INT_KEY);
  }
  
  /**
   * Retrieves allowed origins for CORS.
   * 
   * @return String denoting the allowed origins or <code>*</code> if it's not specified
   */
  public String getAPIAllowedOrigins() {
    String allowedOrigins = data.getJSONObject(API_OBJ_KEY).optString(ALLOWED_ORIGINS_STR_KEY);
    return allowedOrigins == null ? "*" : allowedOrigins;
  }
  
  /**
   * Retrieves the underlying configuration data.
   * 
   * @return the JSON representation of the configuration
   */
  public JSONObject getRaw() {
    return data;
  }
}
