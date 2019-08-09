/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic;

import static java.util.Collections.emptyMap;

import com.newrelic.telemetry.http.HttpPoster;
import com.newrelic.telemetry.http.HttpResponse;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.core.ipc.http.HttpSender.Request;
import io.micrometer.core.ipc.http.HttpSender.Response;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * This is a small adapter between the New Relic HttpPoster interface (from the telemetry SDK) and
 * the micrometer HttpSender interface.
 */
public class MicrometerHttpPoster implements HttpPoster {

  private final HttpSender sender;

  public MicrometerHttpPoster(HttpSender sender) {
    this.sender = sender;
  }

  @Override
  public HttpResponse post(URL url, Map<String, String> headers, byte[] body, String mediaType)
      throws IOException {

    try {
      Request.Builder request = sender.post(url.toString()).withContent(mediaType, body);
      headers.forEach(request::withHeader);
      Response response = request.send();
      return convertResponse(response);
    } catch (Throwable th) {
      throw new IOException("Error posting metrics to New Relic", th);
    }
  }

  private HttpResponse convertResponse(Response response) {
    String body = response.body();
    int code = response.code();
    return new HttpResponse(body, code, "Response code: " + code, emptyMap());
  }
}
