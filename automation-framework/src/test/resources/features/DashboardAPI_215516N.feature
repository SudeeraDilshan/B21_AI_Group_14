Feature: Dashboard API Tests for Tester 215516N

  Background:
    Given the application API is running

  Scenario: API_Dashboard_GetPlantSum_Admin_001
    Given an Admin token is available
    When I send a GET request to "/api/plants/summary"
    Then the API response status code should be 200
    And the response should contain "totalPlants" and "lowStockPlants" integers

  Scenario: API_Dashboard_GetCategorySum_Admin_002
    Given an Admin token is available
    When I send a GET request to "/api/categories/summary"
    Then the API response status code should be 200
    And the response should contain "mainCategories" and "subCategories" integers

  Scenario: API_Dashboard_GetPlantSum_User_003
    Given a Normal User token is available
    When I send a GET request to "/api/plants/summary"
    Then the API response status code should be 200
    And the response should contain "totalPlants" and "lowStockPlants" integers

  Scenario: API_Dashboard_GetCategorySum_User_004
    Given a Normal User token is available
    When I send a GET request to "/api/categories/summary"
    Then the API response status code should be 200
    And the response should contain "mainCategories" and "subCategories" integers

  Scenario: API_Dashboard_GetHealth_Admin_005
    Given an Admin token is available
    When I send a GET request to "/api/health"
    Then the API response status code should be 200
    And the response body should return status "UP"

  Scenario: API_Dashboard_GetHealth_User_006
    Given a Normal User token is available
    When I send a GET request to "/api/health"
    Then the API response status code should be 200
    And the response body should return status "UP"

  Scenario: API_Dashboard_GetSalesPage_Admin_007
    Given an Admin token is available
    When I send a GET request to "/api/sales/page?page=0&size=5"
    Then the API response status code should be 200
    And the response should contain pagination data

  Scenario: API_Dashboard_Unauthorized_NoToken_008
    Given no authentication token is sent
    When I send a GET request to "/api/plants/summary"
    Then the API response status code should be 401
    And the error message should state "Unauthorized - Use Basic Auth or JWT"

  Scenario: API_Dashboard_InvalidToken_009
    Given an invalid token string is used
    When I send a GET request to "/api/categories/summary"
    Then the API response status code should be 401

  Scenario: API_Dashboard_CreateCategory_UserForbidden_010
    Given a Normal User token is available
    When I send a POST request to "/api/categories" with body:
      """
      {
        "name": "Indoor"
      }
      """
    Then the API response status code should be 403
    And the response should contain error "Forbidden" and path "/api/categories"
