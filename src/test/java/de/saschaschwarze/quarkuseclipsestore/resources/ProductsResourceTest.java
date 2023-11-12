package de.saschaschwarze.quarkuseclipsestore.resources;

import static io.restassured.RestAssured.given;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductsResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/products")
          .then()
             .statusCode(200)
             .body(CoreMatchers.containsString("Chair"))
             .body(CoreMatchers.containsString("Table"))
             ;
    }

}