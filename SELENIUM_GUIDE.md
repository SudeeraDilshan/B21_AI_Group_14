# Selenium Testing Guide — B21 AI Group 14

## What is Selenium?

Selenium is a browser automation framework that controls a real web browser (Chrome, Firefox, Edge) programmatically. In this project, Selenium drives Chrome to simulate a user clicking buttons, filling forms, navigating pages, and reading text — exactly as a real user would.

This project uses **Selenium WebDriver 4.21.0** paired with **Cucumber BDD** so that test scenarios are written in plain English (Gherkin) and the Java step definitions use Selenium to carry them out.

---

## How Selenium Is Set Up in This Project

### Technology Stack

| Tool | Version | Role |
|------|---------|------|
| Selenium WebDriver | 4.21.0 | Browser automation |
| ChromeDriver | Auto-managed | Controls Chrome browser |
| Cucumber Java | 7.15.0 | BDD scenario runner |
| JUnit 5 | 5.10.2 | Test execution framework |
| REST Assured | 5.4.0 | API-layer tests (not Selenium) |
| Allure | 2.27.0 | HTML test reports |

### Project Layout

```
automation-framework/
├── pom.xml                              ← Maven build + all dependencies
└── src/test/
    ├── java/
    │   ├── runners/
    │   │   └── TestRunner.java          ← Entry point: runs all features
    │   └── stepdefinitions/
    │       ├── DashboardUISteps.java    ← Selenium: dashboard page tests
    │       ├── PlantsUISteps.java       ← Selenium: plants CRUD tests
    │       ├── DashboardAPISteps.java   ← REST Assured: API tests
    │       └── HealthCheckSteps.java    ← Headless Selenium + API health checks
    └── resources/features/
        ├── DashboardUI_215516N.feature  ← 10 dashboard UI scenarios
        ├── PlantsUI_215517T.feature     ← 22 plants UI scenarios
        ├── DashboardAPI_215516N.feature ← 10 API scenarios
        └── HealthCheck.feature          ← 2 health check scenarios
```

### How the Browser Is Started

Every `UISteps` class creates a ChromeDriver in its constructor:

```java
public PlantsUISteps() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
}
```

- The browser opens **visibly** (not headless) so you can watch tests run.
- `WebDriverWait` of 10 seconds is used everywhere to wait for elements before acting on them.
- The browser closes after each scenario via the `@After tearDown()` hook.

---

## How to Run the Tests

### Prerequisites

1. MySQL running with database `qa_training` (credentials: `root` / `admin`)
2. The Spring Boot app running: `java -jar application/qa-training-app.jar`
3. Java 17 installed
4. Chrome browser installed (ChromeDriver is auto-managed by Selenium 4)

### Run All Tests

```powershell
cd automation-framework
mvnd clean test
```

### Run a Specific Feature File

```powershell
cd automation-framework
mvnd clean test -Dcucumber.filter.tags="@tag"
```

Or by feature file path (add `-Dcucumber.features` to Surefire config in pom.xml).

### View the Allure Report

```powershell
cd automation-framework
mvnd allure:serve
```

This opens a rich HTML report in your browser showing passed/failed/skipped scenarios with screenshots and step details.

---

## What the Selenium Tests Cover

### Dashboard UI Tests — `DashboardUI_215516N.feature` (10 scenarios)

| ID | Scenario | What Selenium Does |
|----|----------|--------------------|
| UI_Dashboard_001 | Admin sees 4 cards | Logs in as admin, checks card text on page |
| UI_Dashboard_002 | Cards display summary counts | Verifies numeric data appears on dashboard |
| UI_Dashboard_003 | Card hover animation | Moves mouse over dashboard card |
| UI_Dashboard_004 | Manage Categories navigation | Clicks button, asserts redirect to `/ui/categories` |
| UI_Dashboard_005 | Manage Plants navigation | Clicks button, asserts redirect to `/ui/plants` |
| UI_Dashboard_006 | Normal User sees 4 cards, no Create Sale | Logs in as testuser, checks button absence |
| UI_Dashboard_007 | View Sales navigation (Normal User) | Clicks Sales button, asserts redirect |
| UI_Dashboard_008 | Summary loads without auth errors | Checks page does not show 403/Unauthorized |
| UI_Dashboard_009 | Inventory link is disabled with tooltip | Finds disabled link and tooltip attribute |
| UI_Dashboard_010 | Responsive layout at 375px | Resizes browser to mobile width |

### Plants UI Tests — `PlantsUI_215517T.feature` (22 scenarios)

| ID | Scenario | What Selenium Does |
|----|----------|--------------------|
| UI_Plants_001 | Plants list page loads | Navigates to `/ui/plants`, waits for table |
| UI_Plants_002 | Column headers displayed | Reads thead text, asserts all 5 columns present |
| UI_Plants_003 | Empty state message | Applies filter returning no results, checks tbody |
| UI_Plants_004 | Search by plant name | Types "Rose" in search, clicks Search |
| UI_Plants_005 | Search with no match | Types random string, verifies "No plants found" |
| UI_Plants_006 | Filter by category | Selects from category dropdown, clicks Search |
| UI_Plants_007 | Reset filters | Clicks Reset link, verifies clean URL |
| UI_Plants_008 | Sort by Name | Clicks Name header, verifies `sortField=name` in URL |
| UI_Plants_009 | Sort by Price | Clicks Price header, verifies `sortField=price` in URL |
| UI_Plants_010 | Sort by Stock | Clicks Stock header, verifies `sortField=quantity` in URL |
| UI_Plants_011 | Low stock shown in red + badge | Finds elements with `bg-danger` and "Low" text |
| UI_Plants_012 | Pagination Next | Clicks Next button, verifies next page loads |
| UI_Plants_013 | Pagination Previous | Navigates to page 2, clicks Previous |
| UI_Plants_014 | Add Plant form fields | Navigates to form, asserts 4 fields exist |
| UI_Plants_015 | Add Plant successfully | Fills form with valid data, saves, checks success alert |
| UI_Plants_016 | Validate: Name required | Submits blank name, checks `text-danger` error |
| UI_Plants_017 | Validate: Category required | Submits with no category, checks error |
| UI_Plants_018 | Validate: Price invalid | Submits `-10` as price, checks error |
| UI_Plants_019 | Validate: Quantity invalid | Submits `-5` as quantity, checks error |
| UI_Plants_020 | Edit existing plant | Clicks Edit, appends " Updated", saves |
| UI_Plants_021 | Delete plant | Clicks Delete, confirms modal, verifies removal |
| UI_Plants_022 | Cancel form | Opens Add form, clicks Cancel, verifies redirect |

### Health Check Tests — `HealthCheck.feature` (2 scenarios)

| ID | Scenario | Method |
|----|----------|--------|
| HC_001 | `/v3/api-docs` returns HTTP 200 | REST Assured (no browser) |
| HC_002 | Swagger UI title contains "Swagger UI" | Headless Chrome |

---

## Things You Can Test Using Selenium in This Project

### 1. Authentication Flows
- Verify login with valid credentials redirects to dashboard
- Verify login with wrong password shows error
- Verify protected pages redirect to login when unauthenticated
- Verify logout clears session and redirects to login

### 2. Role-Based Access Control (RBAC)
- Confirm Admin sees Edit/Delete buttons on plants
- Confirm Normal User does NOT see Edit/Delete buttons
- Confirm "Create Sale" button is Admin-only
- Confirm Normal User cannot access `/ui/plants/add` directly

### 3. Plants CRUD Operations
- Add a plant with all valid fields
- Edit an existing plant and verify the update persists
- Delete a plant and confirm it's removed from the list
- Cancel adding/editing and confirm no changes were saved

### 4. Search and Filtering
- Search by partial or full plant name
- Filter plants by category using the dropdown
- Combine name search + category filter
- Verify "No plants found" when search yields no results
- Verify Reset clears all filters

### 5. Sorting
- Sort by Name, Price, Stock in ascending order
- Click again to sort descending
- Verify the actual row order reflects the sort direction

### 6. Pagination
- Navigate to next/previous pages
- Verify page numbers are correct
- Verify page size (number of rows per page)

### 7. Form Validation
- Submit form with blank required fields
- Submit with negative price or quantity
- Submit with non-numeric values in numeric fields
- Verify specific error messages appear next to each field

### 8. Low Stock Visual Indicator
- Add a plant with quantity ≤ low-stock threshold
- Verify the row text turns red (`text-danger` class)
- Verify the "Low" badge appears (`bg-danger` class)

### 9. Dashboard Cards
- Verify category count matches actual data
- Verify plant count reflects total plants and low-stock count
- Verify sales revenue and count

### 10. Responsive / Cross-Viewport Testing
- Test at 375px (mobile), 768px (tablet), 1280px (desktop)
- Verify sidebar collapses on mobile
- Verify cards stack vertically on narrow screens

---

## How to Add a New Selenium Test

### Step 1 — Write the Gherkin Scenario

Add to an existing `.feature` file or create a new one in `src/test/resources/features/`:

```gherkin
Scenario: UI_Plants_SearchCaseSensitive_023
  When I enter "rose" in lowercase in the search field
  Then plants matching "Rose" should appear in the results
```

### Step 2 — Implement the Step Definitions

Add methods to the appropriate Java class in `src/test/java/stepdefinitions/`:

```java
@When("I enter {string} in lowercase in the search field")
public void i_enter_lowercase_in_search(String term) {
    driver.get(BASE_URL + "/ui/plants");
    wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")));
    driver.findElement(By.name("name")).sendKeys(term);
    driver.findElement(By.xpath("//button[contains(text(), 'Search')]")).click();
}

@Then("plants matching {string} should appear in the results")
public void plants_matching_should_appear(String expected) {
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
    List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr td:first-child"));
    boolean found = rows.stream().anyMatch(r -> r.getText().toLowerCase().contains(expected.toLowerCase()));
    Assertions.assertTrue(found, "No plant matching '" + expected + "' was found");
}
```

### Step 3 — Run and Verify

```powershell
cd automation-framework
mvnd clean test
mvnd allure:serve
```

---

## Common Selenium Patterns Used in This Project

### Wait for Element Before Interacting
```java
WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
el.sendKeys("admin");
```

### Click a Button by Text
```java
driver.findElement(By.xpath("//button[contains(text(), 'Save')]")).click();
```

### Select from Dropdown
```java
Select select = new Select(driver.findElement(By.name("categoryId")));
select.selectByIndex(1);
```

### Assert URL Contains Path
```java
wait.until(ExpectedConditions.urlContains("/ui/plants"));
Assertions.assertTrue(driver.getCurrentUrl().contains("/ui/plants"));
```

### Assert Element Text Contains String
```java
String text = driver.findElement(By.tagName("tbody")).getText();
Assertions.assertTrue(text.contains("No plants found"));
```

### Find All Matching Elements
```java
List<WebElement> badges = driver.findElements(
    By.xpath("//span[contains(@class, 'bg-danger') and contains(text(), 'Low')]")
);
Assertions.assertFalse(badges.isEmpty(), "No low-stock badges found");
```

---

## Useful Locator Strategies

| Strategy | Example | Best For |
|----------|---------|---------|
| `By.name()` | `By.name("username")` | Form fields with `name` attribute |
| `By.id()` | `By.id("deleteModal")` | Elements with unique `id` |
| `By.className()` | `By.className("dashboard-card")` | Elements sharing a CSS class |
| `By.cssSelector()` | `By.cssSelector("button[type='submit']")` | Complex attribute combinations |
| `By.xpath()` | `By.xpath("//button[contains(text(),'Save')]")` | Matching by visible text |
| `By.tagName()` | `By.tagName("table")` | Generic element type |

---

## Configuration Reference

| Setting | Value | Location |
|---------|-------|----------|
| App base URL | `http://localhost:8080` | All step definition classes |
| Admin credentials | `admin` / `admin123` | `loginAs()` calls |
| Normal user credentials | `testuser` / `test123` | `loginAs()` calls |
| WebDriverWait timeout | 10 seconds | All step definition constructors |
| Allure results dir | `target/allure-results/` | `pom.xml` |
| Test failure behaviour | Ignore failures, still report | `testFailureIgnore=true` in `pom.xml` |
