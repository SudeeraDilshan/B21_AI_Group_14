Feature: Dashboard UI Tests for Tester 215516N

  Background:
    Given the application UI is accessible

  Scenario: UI_AdminDashboard_RenderCards_001
    Given I am logged in as Admin
    When I access the main dashboard page
    Then I should see the 4 cards: "Categories", "Plants", "Sales", and "Inventory"

  Scenario: UI_AdminDashboard_SummaryData_002
    Given I am logged in as Admin
    When I observe the counts on each dashboard card
    Then the Categories card should display counts
    And the Plants card should display counts
    And the Sales card should display sales and revenue

  Scenario: UI_AdminDashboard_CardHoverAnimation_003
    Given I am logged in as Admin
    When I hover over the dashboard cards
    Then the cards should scale up and cursor should change to pointer

  Scenario: UI_AdminDashboard_ManageCategoriesNav_004
    Given I am logged in as Admin
    When I click the "Manage Categories" button
    Then I should be redirected to "/ui/categories"

  Scenario: UI_AdminDashboard_ManagePlantsNav_005
    Given I am logged in as Admin
    When I click the "Manage Plants" button
    Then I should be redirected to "/ui/plants"

  Scenario: UI_UserDashboard_RenderCards_006
    Given I am logged in as Normal User
    When I access the main dashboard page
    Then I should see the 4 cards: "Categories", "Plants", "Sales", and "Inventory"
    And the "Sales" card should lack the button to create sales

  Scenario: UI_UserDashboard_ViewSalesNav_007
    Given I am logged in as Normal User
    When I click the "View Sales" button
    Then I should be redirected to "/ui/sales"

  Scenario: UI_UserDashboard_SummaryCounts_008
    Given I am logged in as Normal User
    When I observe the counts on each dashboard card
    Then the summary numbers should load successfully without authorization errors

  Scenario: UI_UserDashboard_OpenInventoryDisabled_009
    Given I am logged in as Normal User
    When I locate the Inventory card
    Then the "Open Inventory" link should be disabled
    And a tooltip displaying "Inventory page coming soon" should appear on hover

  Scenario: UI_UserDashboard_ResponsiveLayout_010
    Given I am logged in as Normal User
    When I set the browser viewport width to 375px
    Then the sidebar menu should collapse or hide
    And the cards should stack vertically
