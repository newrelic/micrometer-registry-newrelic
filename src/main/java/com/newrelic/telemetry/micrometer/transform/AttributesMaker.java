/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import com.newrelic.telemetry.Attributes;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import java.util.List;

public class AttributesMaker {

  Attributes make(Meter.Id id, String sourceType) {
    List<Tag> tags = id.getTags();
    String baseUnit = id.getBaseUnit();
    String description = id.getDescription();

    Attributes attributes = new Attributes();

    if (description != null) {
      attributes.put("description", description);
    }
    if (baseUnit != null) {
      attributes.put("baseUnit", baseUnit);
    }
    tags.forEach(tag -> attributes.put(tag.getKey(), tag.getValue()));

    return attributes;
  }
}
