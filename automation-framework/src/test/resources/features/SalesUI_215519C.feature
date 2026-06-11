@SalesUI
Feature: Sales UI Tests for Tester 215519C

  Background:
    Given the Sales application UI is accessible

  Scenario: UI_Create_Salse_001
    Given I am logged in as Admin to access sales
    And a plant exists with stock available
    When I navigate to the Sales page
    And I click Sell Plant
    And I select the plant
    And I enter a valid quantity
    And I click the sell Save button
    Then a Sale should be created successfully
    And the plant stock should be reduced
    And I should be redirected to the Sales list

  Scenario: UI_Validate_Qty_Salse_002
    Given I am logged in as Admin to access sales
    When I navigate to the Sales page
    And I open the Sell Plant page
    And I enter quantity 0
    And I click the sell button
    Then I should see a validation message "Quantity must be greater than 0"

  Scenario: UI_Salse_Cansel_Button_003
    Given I am logged in as Admin to access sales
    When I navigate to the Sales page
    And I open the Sell Plant page
    And I click the cancel button
    Then I should be redirected to the Sales list page

  Scenario: UI_Sorting_by_Qty_Salse_004
    Given I am logged in as Admin to access sales
    And Sales data exists
    When I go to the Sales page
    And I click the Quantity column header
    Then Sales should be sorted by Quantity correctly

  Scenario: UI_Sorting_by_Tot_Price_Salse_005
    Given I am logged in as Admin to access sales
    And Sales data exists
    When I go to the Sales page
    And I click the Total Price column header
    Then Sales should be sorted by Total Price correctly

  Scenario: UI_Display_Salse_006
    Given I am logged in as a User
    And Sales records exist
    When I click on Sales in side navigation
    Then the Sales page loads successfully
    And the Sales list is visible

  Scenario: UI_Sorting_Default_Salse_007
    Given I am logged in as a User
    And Sales records exist
    When I navigate to the Sales page
    Then Sales should be sorted by Sold Date in descending order by default

  Scenario: UI__Empty_Salse_008
    Given I am logged in as a User
    And no sales records exist
    When I navigate to the Sales page
    Then the "No sales found" message is Displayed

  Scenario: UI_Sorting_By_Sold_At_Salse_009
    Given I am logged in as a User
    And Sales data exists
    When I go to the Sales page
    And I click the Sold At column header
    Then Sales should be sorted by Sold At correctly

  Scenario: UI_User_Pagination_Salse_010
    Given I am logged in as a User
    And Sales exceed one page
    When I open the Sales page
    Then Pagination controls are displayed and functional
