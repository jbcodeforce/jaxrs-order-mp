// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/

package it.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;

public class HealthITUtil {

  private static String port;
  private static String baseUrl;
  public static final String INV_MAINTENANCE_FALSE = "io_openliberty_guides_inventory_inMaintenance\":false";
  public static final String INV_MAINTENANCE_TRUE = "io_openliberty_guides_inventory_inMaintenance\":true";

  static {
    port = System.getProperty("default.http.port","9080");
    baseUrl = "http://localhost:" + port + "/";
  }

  public static JsonArray connectToHealthEndpoint(int expectedResponseCode,
      String endpoint) {
    String healthURL = baseUrl + endpoint;
    Client client = ClientBuilder.newClient().register(JsrJsonpProvider.class);
    Response response = client.target(healthURL).request().get();
    assertEquals(expectedResponseCode, response.getStatus(),
        "Response code is not matching " + healthURL);
    JsonArray servicesStates = response.readEntity(JsonObject.class)
        .getJsonArray("checks");
    response.close();
    client.close();
    return servicesStates;
  }

  public static String getActualState(String service, JsonArray servicesStates) {
    String state = "";
    for (Object obj : servicesStates) {
      if (obj instanceof JsonObject) {
        if (service.equals(((JsonObject) obj).getString("name"))) {
          state = ((JsonObject) obj).getString("status");
        }
      }
    }
    return state;
  }
}

