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
package com.calebpower.demo.webstandalone.domain.persistent;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.uco.cs.v2c.dispatcher.persistent.Config;
import edu.uco.cs.v2c.dispatcher.persistent.PersistentDataException;

/**
 * Example test case for Config; ensures that the default port is indeed 2585.
 * 
 * @author Caleb L. Power
 */
public class ConfigTest {
  
  /**
   * Test the default port.
   * @throws PersistentDataException if the config couldn't be read
   */
  @Test public void testSparkPort() throws PersistentDataException {
    Config config = new Config(Config.getDefaultConfig().toString());
    assertTrue("Default Spark port should be 4567.", config.getAPIPort() == 2585);
  }
}
