/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

import com.newrelic.telemetry.http.HttpResponse;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.core.ipc.http.HttpUrlConnectionSender;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

class MicrometerHttpPosterTest {

  @Test
  void testSuccessfulPost() throws Throwable {
    ClientAndServer mockServer = ClientAndServer.startClientAndServer();
    byte[] body = "some important post data".getBytes();
    Map<String, String> headers = new HashMap<>();
    headers.put("TESTING", "stuff");
    String url = "http://127.0.0.1:" + mockServer.getLocalPort() + "/test";
    mockServer
        .when(request().withPath("/test").withHeader("TESTING", "stuff").withBody(body))
        .respond(response().withBody("Thanks for calling.").withHeader("Foo", "bar"));

    HttpSender sender = new HttpUrlConnectionSender();
    MicrometerHttpPoster poster = new MicrometerHttpPoster(sender);
    HttpResponse result = poster.post(new URL(url), headers, body, "application/json");
    assertEquals(200, result.getCode());
    assertEquals("Response code: 200", result.getMessage());
    assertTrue(result.getHeaders().isEmpty());
    assertEquals("Thanks for calling.", result.getBody());

    mockServer.verify(request().withPath("/test").withHeader("TESTING", "stuff"), exactly(1));
  }

  @Test
  void testException() throws Exception {
    HttpSender sender = new HttpUrlConnectionSender();
    MicrometerHttpPoster poster = new MicrometerHttpPoster(sender);
    assertThrows(
        IOException.class,
        () -> {
          HttpResponse result =
              poster.post(
                  new URL("http://localhost:5151/nope"),
                  new HashMap<>(),
                  "boom".getBytes(),
                  "application/json");
        });
  }
}
