package it.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.app.domain.Address;
import ibm.gse.eda.app.dto.OrderParameters;

public class OrderCreationTest {

	private static String baseUrl;
    private static final String ORDERS_ENDPOINT = "/orders";
    
   	private Client client;
   	private Response response;
   	private final Jsonb jsonb = JsonbBuilder.create();

    @BeforeAll
    public static void oneTimeSetup() {
        final String port = System.getProperty("liberty.test.port", "9080");
        final String context = System.getProperty("context.root", "orderms");
        baseUrl = "http://localhost:" + port + "/" + context;
    }

    @BeforeEach
    public void setup() {
        response = null;
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    @AfterEach
    public void teardown() {
        if (response != null)
            response.close();
        client.close();
    }

    @Test
    public void shouldCreateOrder() {
        final Address destinationAddress = new Address("Street", "City", "DestinationCountry", "State", "Zipcode");
        final OrderParameters inOrder = new OrderParameters("C11", "P02", 10, destinationAddress);

        final String inOrderJson = jsonb.toJson(inOrder);
        response =  client.target(baseUrl + ORDERS_ENDPOINT).request().post(Entity.entity(inOrderJson, MediaType.APPLICATION_JSON_TYPE));
        assertEquals( 200, response.getStatus());
    }
}
