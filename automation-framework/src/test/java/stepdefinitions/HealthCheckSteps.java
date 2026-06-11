package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class HealthCheckSteps {

    private Response response;
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080";

    public HealthCheckSteps() {
        RestAssured.baseURI = BASE_URL;
    }

    @Given("the application is running")
    public void the_application_is_running() {
        // We assume the application is running independently for this test.
    }

    @When("I make a GET request to {string}")
    public void i_make_a_get_request_to(String endpoint) {
        response = RestAssured.get(endpoint);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @When("I open the Swagger UI in the browser")
    public void i_open_the_swagger_ui_in_the_browser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Run headless
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get(BASE_URL + "/swagger-ui.html");
    }

    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedTitle) {
        try {
            String actualTitle = driver.getTitle();
            Assertions.assertTrue(actualTitle.contains(expectedTitle), 
                "Expected title to contain '" + expectedTitle + "' but was '" + actualTitle + "'");
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
