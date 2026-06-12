Feature: Category UI Tests for Tester 215556K

  Scenario: UI_Category_AddMainCategory_001
    Given I have navigated to the Add Category page as an admin
    When I enter a valid Category Name and leave Parent Category empty
    And I click the Save category button
    Then a new main category should be created with a success message

  Scenario: UI_Category_NameRequiredValidation_002
    Given I have navigated to the Add Category page as an admin
    When I leave the Category Name empty
    And I click the Save category button
    Then a validation error for Category Name should be displayed

  Scenario: UI_Category_NameLengthValidation_003
    Given I have navigated to the Add Category page as an admin
    When I enter a Category Name exceeding the maximum length
    And I click the Save category button
    Then a validation error for Category Name length should be displayed

  Scenario: UI_Category_EditExistingCategory_004
    Given I am on the Categories list page as an admin
    When I click Edit for an existing category
    And I update the Category Name
    And I click the Save category button
    Then the category should be updated with a success message

  Scenario: UI_Category_AddCategoryCancelAction_005
    Given I have navigated to the Add Category page as an admin
    When I click the Cancel button
    Then I should be redirected back to the Categories list page

  Scenario: UI_Category_AccessAddCategoryPage_006
    Given I am logged in as a normal user
    When I attempt to navigate to the Add Category page directly
    Then I should be redirected to the 403 Forbidden page or login page

  Scenario: UI_Category_AccessEditCategoryPage_007
    Given I am logged in as a normal user
    When I attempt to navigate to the Edit Category page for an existing category directly
    Then I should be redirected to the 403 Forbidden page or login page

  Scenario: UI_Category_AddCategoryButtonVisibility_008
    Given I am on the Categories list page as a normal user
    Then the Add A Category button should not be visible

  Scenario: UI_Category_EditButtonVisibility_009
    Given I am on the Categories list page as a normal user
    Then the Edit and Delete buttons should not be visible for category rows

  Scenario: UI_Category_DisplayCategoriesListAsUser_010
    Given I am on the Categories list page as a normal user
    Then I should see the list of categories with ID, Name, and Parent columns
    


