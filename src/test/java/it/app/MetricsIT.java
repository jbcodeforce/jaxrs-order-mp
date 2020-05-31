package it.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class MetricsIT {
    private static String httpPort;
    private static String httpsPort;
    private static String baseHttpUrl;
    private static String baseHttpsUrl;
  
    private List<String> metrics;
    private Client client;

    private final String METRICS_APPLICATION = "metrics/application";
    private final String ORDERS_URL = "orderms/orders";

    @BeforeAll
    public static void oneTimeSetup() {
      httpPort = System.getProperty("http.port","9080");
      httpsPort = System.getProperty("https.port","9443");
      baseHttpUrl = "http://localhost:" + httpPort + "/";
      baseHttpsUrl = "https://localhost:" + httpsPort + "/";
    }
  
    @BeforeEach
    public void setup() {
      client = ClientBuilder.newClient();
      client.register(JsrJsonpProvider.class);
    }
  
    @AfterEach
    public void teardown() {
      client.close();
    }
  
    @Test
    @Order(1)
    public void testOrdersRequestTimeMetric() {
      connectToEndpoint(baseHttpUrl + ORDERS_URL);
      metrics = getMetrics();
      for (String metric : metrics) {
        if (metric.startsWith(
            "application_orderProcessingTime_rate_per_second")) {
          float seconds = Float.parseFloat(metric.split(" ")[1]);
          assertTrue(4 > seconds);
        }
      }
    }

    @Test
    @Order(2)
    public void testOrderAccessCountMetric() {
      connectToEndpoint(baseHttpUrl + ORDERS_URL);
      metrics = getMetrics();
      for (String metric : metrics) {
        if (metric.startsWith("application_ordersAccessCount_total")) {
          assertTrue(
              1 <= Integer.parseInt(metric.split(" ")[metric.split(" ").length - 1]));
        }
      }
    }

    @Test
    @Order(3)
    public void testOrderSizeGaugeMetric() {
      metrics = getMetrics();
      for (String metric : metrics) {
        if (metric.startsWith("application_ordersSizeGauge")) {
          assertTrue(
              1 <= Character.getNumericValue(metric.charAt(metric.length() - 1)));
        }
      }
    }

    public void connectToEndpoint(String url) {
        Response response = this.getResponse(url);
        this.assertResponse(url, response);
        response.close();
      }
    
      private List<String> getMetrics() {
        String usernameAndPassword = "admin" + ":" + "adminpwd";
        String authorizationHeaderValue = "Basic "
            + java.util.Base64.getEncoder()
                              .encodeToString(usernameAndPassword.getBytes());
        Response metricsResponse = client.target(baseHttpsUrl + METRICS_APPLICATION)
                                         .request(MediaType.TEXT_PLAIN)
                                         .header("Authorization",
                                             authorizationHeaderValue)
                                         .get();
    
        BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)
        metricsResponse.getEntity()));
        List<String> result = new ArrayList<String>();
        try {
          String input;
          while ((input = br.readLine()) != null) {
            result.add(input);
          }
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
          fail();
        }
    
        metricsResponse.close();
        return result;
      }
    
      private Response getResponse(String url) {
        return client.target(url).request().get();
      }
    
      private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
      }
}