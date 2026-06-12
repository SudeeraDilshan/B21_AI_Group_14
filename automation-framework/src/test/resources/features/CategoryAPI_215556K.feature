Feature: Category API Tests for Tester 215556K

  Scenario: API_Category_CreateCategory_001
    Given I have a valid category admin API token
    When I send a category POST request to "/api/categories" with a unique name
    Then the category response status should be 201
    And the category is created successfully

  Scenario: API_Category_EmptyCategoryNameValidation_002
    Given I have a valid category admin API token
    When I send a category POST request to "/api/categories" with an empty name
    Then the category response status should be 400

  Scenario: API_Category_CategoryNameLengthValidation_003
    Given I have a valid category admin API token
    When I send a category POST request to "/api/categories" with a name exceeding max length
    Then the category response status should be 400

  Scenario: API_Category_UpdateCategory_004
    Given I have a valid category admin API token
    And an existing category
    When I send a category PUT request to update the category name
    Then the category response status should be 200
    And the category is updated successfully

  Scenario: API_Category_DeleteCategory_005
    Given I have a valid category admin API token
    And an existing category
    When I send a category DELETE request to "/api/categories/{id}"
    Then the category response status should be 200
    And the category is removed

  Scenario: API_Category_CreateCategoryUnauthorized_006
    Given I do not have a valid category API token
    When I send a category POST request to "/api/categories"
    Then the category response status should be 403 or 401

  Scenario: API_Category_EditCategoryUnauthorized_007
    Given I do not have a valid category API token
    When I send a category PUT request to update the category name
    Then the category response status should be 403 or 401

  Scenario: API_Category_DeleteCategoryUnauthorized_008
    Given I do not have a valid category API token
    When I send a category DELETE request to "/api/categories/{id}"
    Then the category response status should be 403 or 401

  Scenario: API_Category_GetCategoriesPageAsUser_009
    Given I have a valid category user API token
    When I send a category GET request to "/api/categories"
    Then the category response status should be 200
    And a list of categories is returned

  Scenario: API_SubCategory_GetByParentId__010
    Given I have a valid category user API token
    And a parent category with subcategories exists
    When I send a category GET request to "/api/categories/{id}/subcategories"
    Then the category response status should be 200
    
