package it.api.rest;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Class based on https://github.com/OpenLiberty/guide-microprofile-openapi/blob/master/finish/src/test/java/it/io/openliberty/guides/inventory/InventoryEndpointIT.java
 */
@TestMethodOrder(OrderAnnotation.class)
public class EndpointsIT {
    private static String baseUrl;
    private static String userJWT;
    private static long timestamp;

    private static Client client;
    private static String port;

    @BeforeAll
    public static void oneTimeSetup() {
        port = System.getProperty("http.port");
        baseUrl = "http://localhost:" + port;
        timestamp = new Date(System.currentTimeMillis()).getTime();
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
    public void testCreateUser() {
        String endpoint = baseUrl + "/users";
        String user = "{"
                + "  \"user\": {"
                + "    \"username\": \"Jacob" + timestamp + "\","
                + "    \"email\": \"jake" + timestamp + "@jake.jake\","
                + "    \"password\": \"jakejake\""
                + "  }"
                + "}";

        try (Response response = client.target(endpoint).request().post(Entity.json(user))) {
            this.assertResponse(endpoint, response);

            userJWT = response.readEntity(JsonObject.class).getJsonObject("user").getString("token");

            JsonObject responseBody = response.readEntity(JsonObject.class).getJsonObject("user");
            String expected = "Jacob" + timestamp;
            String actual = responseBody.getString("username");
            assertTrue(expected.equals(actual), "The new User's username should be included in the response body");
        }
    }

    @Test
    @Order(2)
    public void testCreateArticle() {
        String endpoint = baseUrl + "/articles";
        String testString = "{"
                + "  \"article\": {"
                + "    \"title\": \"How to train your dragon\","
                + "    \"description\": \"Ever wonder how?\","
                + "    \"body\": \"You have to believe\","
                + "    \"tagList\": ["
                + "      \"reactjs\","
                + "      \"angularjs\","
                + "      \"dragons\""
                + "    ]"
                + "  }"
                + "}";

        try (Response response = client.target(endpoint).request().header("Authorization", userJWT).post(Entity.json(testString))) {
            this.assertResponse(endpoint, response);

            JsonObject responseBody = response.readEntity(JsonObject.class).getJsonObject("article");
            assertEquals("How to train your dragon", responseBody.getString("title"), "The Article's title should be included in the response body");
            assertEquals("You have to believe", responseBody.getString("body"), "The Article's body should be included in the response body as null");
            assertTrue(responseBody.get("tagList").asJsonArray().contains(Json.createValue("reactjs")), "The Article's tagList should be included in the response body, and should contain reactjs");
        }
    }

    /**
     * <p>
     * Asserts that the given URL has the correct response code of 200.
     * </p>
     *
     * @param url      - target URL.
     * @param response - response received from the target URL.
     */
    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url + ". " + response.getStatusInfo());
    }

}
