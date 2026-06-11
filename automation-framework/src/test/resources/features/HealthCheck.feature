Feature: QA Training Application Health Check

  Scenario: Verify API documentation is accessible
    Given the application is running
    When I make a GET request to "/v3/api-docs"
    Then the response status code should be 200

  Scenario: Verify Swagger UI is accessible
    Given the application is running
    When I open the Swagger UI in the browser
    Then the page title should contain "Swagger UI"
