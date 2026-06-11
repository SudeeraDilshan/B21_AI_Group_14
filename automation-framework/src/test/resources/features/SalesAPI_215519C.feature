Feature: Sales API Tests for Tester 215519C

  Scenario: API_Admin_Retrieve_Sale_011
    Given an Admin is logged in and valid Admin access token is available
    And Sales records exist in the system
    When I navigate to GET "/api/sales" and add Authorization header with valid Admin token
    And I send the sales GET request
    Then the response status code is 200 OK
    And the response body contains a list of sales
    And each sale object includes id, plant object with correct details, quantity, totalPrice, soldAt timestamp

  Scenario: API_Admin_Retrieve_Empty_Sale_012
    Given an Admin is logged in and valid Admin access token is available
    And No Sales records exist in the system
    When I navigate to GET "/api/sales" and add Authorization header with valid Admin token
    And I send the sales GET request
    Then the response status code is 200 OK
    And the response body is an empty array
    And no error message is returned

  Scenario: API_Admin_Sell_Plant_013
    Given an Admin is logged in and valid Admin access token is available
    And a Plant with the entered PlantId exists
    And the Plant stock quantity is greater than or equal to the requested quantity
    When I navigate to POST "/api/sales/plant/{plantId}"
    And I provide a valid plantId
    And I pass quantity with a positive integer value
    And I add Authorization header with Admin token
    And I send the POST request
    Then the response status code is 201 Created
    And the Sale record is created
    And the response body contains Sale id, Correct plant details, Sold quantity, Correct totalPrice, soldAt timestamp
    And the Plant inventory quantity is reduced correctly

  Scenario: API_Update_Inventory_Sale_014
    Given an Admin is logged in and valid Admin access token is available
    And a Plant exists with known stock quantity
    And I note the current plant stock quantity
    When I navigate to POST "/api/sales/plant/{plantId}"
    And I provide a valid plantId
    And I send a POST request to sell the plant with quantity 0
    Then the response status code is 400 Bad Request
    And the response body contains status, error, message indicating invalid quantity, timestamp
    And no sale is created
    And the Inventory remains unchanged

  Scenario: API_Unauthorized_user_retrieve_sale_015
    Given the User is not logged in
    And a Plant exists with stock
    When I navigate to GET "/api/sales"
    And I send the GET request without Authorization header
    Then the response status code is 401 Unauthorized
    And an Authentication error message is returned

  Scenario: API_User_Retrieve_Sale_016
    Given a user is logged in
    And a Valid User access token is available
    And Sales records exist in the system
    When I navigate to GET "/api/sales" and add Authorization header with valid User token
    And I send the sales GET request
    Then the response status code is 200 OK
    And the response body contains a list of sales
    And each sale object includes id, plant object with correct details, quantity, totalPrice, soldAt timestamp

  Scenario: API_User_Delete_Sale_017
    Given the User is not logged in
    And no authentication token is provided
    When I navigate to DELETE "/api/sales/{id}"
    And I send a DELETE request to "/api/sales/{id}" with a valid saleId
    And I do not include Authorization header
    Then the response status code is 401 Unauthorized
    And the Sale is not deleted

  Scenario: API_No_Sale_ID_018
    Given an Admin is logged in and valid Admin access token is available
    And a Sale ID does not exist in database
    When I navigate to GET "/api/sales/{id}"
    And I send a GET request to "/api/sales/{id}" using a non-existing saleId
    Then the response status code is 404 Not Found
    And an appropriate error message is returned indicating sale not found

  Scenario: API_Non_Admin_Delete_Sale_019
    Given a User is logged in as non-Admin
    And a valid authentication token is available
    And a Sale exists in the system
    When I navigate to DELETE "/api/sales/{id}"
    And I send a DELETE request to "/api/sales/{id}" using non-Admin token
    Then the response status code is 403 Forbidden
    And the Sale is not deleted

  Scenario: API_Sale_Internal_Server_error_020
    Given an Admin is logged in and valid Admin access token is available
    And the Backend service or database is unavailable
    When I navigate to DELETE "/api/sales/{id}"
    And I send a DELETE request to "/api/sales/{id}"
    Then the response status code is 500 Internal Server Error
    And an appropriate server error is returned
