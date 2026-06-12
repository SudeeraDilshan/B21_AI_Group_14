Feature: Plants UI Tests for Tester 215517T

  Background:
    Given the Plants application UI is accessible
    And I log in as Admin to access the system

  Scenario: UI_Plants_List_001
    When I navigate to "/ui/plants"
    Then the plants list page loads immediately

  Scenario: UI_Plants_ViewData_002
    When I open "/ui/plants"
    Then plant records are displayed with Name, Category, Price, Stock and Actions columns

  Scenario: UI_Plants_EmptyState_003
    When I open "/ui/plants" with a filter that returns no records
    Then the page shows "No plants found" message

  Scenario: UI_Plants_SearchMatch_004
    When I enter a valid plant name in Search plant and click Search
    Then only matching plant records are displayed

  Scenario: UI_Plants_SearchNoMatch_005
    When I enter a non-existing plant name and click Search
    Then no records are shown and empty-state message appears

  Scenario: UI_Plants_FilterCategory_006
    When I select a category and click Search
    Then only plants from the selected category are displayed

  Scenario: UI_Plants_ResetFilters_007
    Given the plants page has search or category filter applied
    When I click the Reset button
    Then all plants are shown again and filters are cleared

  Scenario: UI_Plants_SortName_008
    Given the plants list has multiple records
    When I click the Name column header
    Then plants are sorted by name

  Scenario: UI_Plants_SortPrice_009
    Given the plants list has multiple records
    When I click the Price column header
    Then plants are sorted by price

  Scenario: UI_Plants_SortStock_010
    Given the plants list has multiple records
    When I click the Stock column header
    Then plants are sorted by quantity

  Scenario: UI_Plants_LowStockHighlight_011
    When I view the plants page
    Then low stock plant is shown in red and displays Low badge

  Scenario: UI_Plants_PaginationNext_012
    When I click Next on the plants page
    Then the next page of plant records is displayed

  Scenario: UI_Plants_PaginationPrev_013
    Given I am on page 2 or higher
    When I click Previous
    Then the previous page of plant records is displayed

  Scenario: UI_Plants_AddPage_014
    When I click Add a Plant or navigate to "/ui/plants/add"
    Then the Add Plant form opens with Name, Category, Price, and Quantity fields


