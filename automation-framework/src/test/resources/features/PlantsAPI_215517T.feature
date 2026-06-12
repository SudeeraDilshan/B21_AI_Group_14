Feature: Plants API Tests for Tester 215517T 
 
 Background:
     Given I login as Admin 
     And the plant management dashboard is displayed 
         
 Scenario: API_Plants_AddSubmit_015
    Given I am on the Add Plant page
    When I fill valid data and click Save
    Then the new plant is created with a success message

  Scenario: API_Plants_ValidateName_016
    Given I am on the Add Plant page
    When I leave Plant Name blank and click Save
    Then a validation error is shown for Plant Name

  Scenario: API_Plants_ValidateCategory_017
    Given I am on the Add Plant page
    When I leave Category unselected and click Save
    Then a validation error is shown for Category

  Scenario: API_Plants_ValidatePrice_018
    Given I am on the Add Plant page
    When I enter invalid price value and click Save
    Then a validation error is shown for Price

  Scenario: API_Plants_ValidateQuantity_019
    Given I am on the Add Plant page
    When I enter invalid quantity value and click Save
    Then a validation error is shown for Quantity

  Scenario: API_Plants_EditDetails_020
    When I click Edit on an existing plant, change fields, and Save
    Then the plant details are updated successfully

  Scenario: API_Plants_Delete_021
    When I click Delete, confirm deletion, and return to list
    Then the plant is removed from the list

  Scenario: API_Plants_CancelForm_022
    Given I am on the Add or Edit Plant page
    When I click Cancel
    Then I return to "/ui/plants" without saving changes
