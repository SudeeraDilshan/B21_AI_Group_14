@SalesUI @SRSGap
Feature: Sales UI - Additional SRS Section 7 Coverage for Tester 215519C

  # These scenarios close the gaps between SalesUI_215519C.feature and the SRS
  # Sales Management Module (Section 7). Each maps to a specific SRS requirement:
  #   UI_021 -> SRS 7.1 : Sorting supported on Plant name
  #   UI_022 -> SRS 7.2 : "Plant is required" validation on the Sell Plant page
  #   UI_023 -> SRS 7.2 : On error, an error message is displayed on the same page

  Background:
    Given the Sales application UI is accessible

  Scenario: UI_Sorting_by_Plant_Name_Salse_021
    Given I am logged in as Admin to access sales
    And Sales data exists
    When I go to the Sales page
    And I click the Plant Name column header
    Then Sales should be sorted by Plant Name correctly

  Scenario: UI_Validate_Plant_Required_Salse_022
    Given I am logged in as Admin to access sales
    When I navigate to the Sales page
    And I open the Sell Plant page
    And I enter a valid quantity
    And I submit the sell form without selecting a plant
    Then I should see a plant validation message "Plant is required"

  Scenario: UI_Oversell_Stock_Error_Salse_023
    Given I am logged in as Admin to access sales
    And a plant exists with stock available
    When I navigate to the Sales page
    And I open the Sell Plant page
    And I select a plant from the dropdown
    And I enter a quantity greater than available stock
    And I click the sell button
    Then an error message should be displayed on the Sell Plant page
    And the sale should not be created
