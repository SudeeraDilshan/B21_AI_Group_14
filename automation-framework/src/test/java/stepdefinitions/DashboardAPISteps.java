package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DashboardAPISteps {

    private final String BASE_URL = "http://localhost:8080";
    private String token;
    private Response response;

    public DashboardAPISteps() {
        RestAssured.baseURI = BASE_URL;
    }

    @Given("the application API is running")
    public void the_application_api_is_running() {
        // Assuming it's running
    }

    private String getToken(String username, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response res = given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/api/auth/login");

        Assertions.assertEquals(200, res.getStatusCode(), "Login failed for " + username);
        return res.jsonPath().getString("token");
    }

    @Given("an Admin token is available")
    public void an_admin_token_is_available() {
        token = getToken("admin", "admin123");
    }

    @Given("a Normal User token is available")
    public void a_normal_user_token_is_available() {
        token = getToken("testuser", "test123");
    }

    @Given("no authentication token is sent")
    public void no_authentication_token_is_sent() {
        token = null;
    }

    @Given("an invalid token string is used")
    public void an_invalid_token_string_is_used() {
        token = "invalid_token_xyz";
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        RequestSpecification req = given().contentType(ContentType.JSON);
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        response = req.when().get(endpoint);
    }

    @When("I send a POST request to {string} with body:")
    public void i_send_a_post_request_to_with_body(String endpoint, String body) {
        RequestSpecification req = given().contentType(ContentType.JSON);
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        response = req.body(body).when().post(endpoint);
    }

    @Then("the API response status code should be {int}")
    public void the_api_response_status_code_should_be(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @And("the response should contain {string} and {string} integers")
    public void the_response_should_contain_and_integers(String key1, String key2) {
        response.then().body("$", hasKey(key1))
                       .body("$", hasKey(key2));
    }

    @And("the response body should return status {string}")
    public void the_response_body_should_return_status(String statusValue) {
        response.then().body("status", equalTo(statusValue));
    }

    @And("the response should contain pagination data")
    public void the_response_should_contain_pagination_data() {
        response.then().body("$", hasKey("pageable"))
                       .body("$", hasKey("totalElements"))
                       .body("$", hasKey("content"));
    }

    @And("the error message should state {string}")
    public void the_error_message_should_state(String expectedMessage) {
        response.then().body("message", equalTo(expectedMessage));
    }

    @And("the response should contain error {string} and path {string}")
    public void the_response_should_contain_error_and_path(String error, String path) {
        response.then().body("error", equalTo(error))
                       .body("path", equalTo(path));
    }
}
