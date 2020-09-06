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
