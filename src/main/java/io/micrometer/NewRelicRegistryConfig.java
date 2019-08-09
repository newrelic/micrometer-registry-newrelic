/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer;

import io.micrometer.core.instrument.config.MissingRequiredConfigurationException;
import io.micrometer.core.instrument.step.StepRegistryConfig;

public interface NewRelicRegistryConfig extends StepRegistryConfig {
  @Override
  default String prefix() {
    return "newrelic";
  }

  default String apiKey() {
    String apiKey = get(prefix() + ".apiKey");
    if (apiKey == null)
      throw new MissingRequiredConfigurationException(
          "apiKey must be set to report metrics to New Relic");
    return apiKey;
  }

  /**
   * @return The URI for the New Relic metric API. Please contact open-instrumentation@newrelic.com
   *     to get access.
   */
  default String uri() {
    String uri = get(prefix() + ".uri");
    if (uri == null) {
      throw new MissingRequiredConfigurationException(
          "uri must be set to report metrics to New Relic");
    }
    return uri;
  }
}
