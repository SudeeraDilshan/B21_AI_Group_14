# Project Overview — IS3440 IT Quality Assurance Automation Framework
### Group B21 · AI Group 14

---

## What Is This Project?

This project is an **automated software testing framework** built for the IS3440 (IT Quality Assurance) university assignment. It automatically tests a web application called the **QA Training App** — a plant shop management system — to make sure all its features work correctly without needing a human to click through everything by hand every time.

Think of it like a robot tester: you press one command, and it logs into the app, clicks buttons, fills forms, calls APIs, and checks that everything behaves as expected.

---

## What Does the QA Training App Do?

The application under test is a **Spring Boot** web app (packaged as `qa-training-app.jar`) that manages a plant business with two types of users:

| Role | What they can do |
|---|---|
| **Admin** | Manage categories, manage plants, view and create sales |
| **Normal User** | View categories, view plants, view sales (read-only) |

The app has these main pages and features:
- **Dashboard** — shows summary cards (Categories, Plants, Sales, Inventory counts)
- **Plants** — list, search, filter, sort, add, edit, delete plants
- **Categories** — manage plant categories
- **Sales** — view sales records
- **Swagger UI** — API documentation page at `/swagger-ui.html`

The backend exposes REST API endpoints (e.g. `/api/plants/summary`, `/api/categories`, `/api/auth/login`) which are also tested separately.

---

## Assignment Context

This is a group project for the course IS3440 – IT Quality Assurance. The framework has been assigned to use:

- **Selenium** for browser/UI automation
- **REST Assured** for API testing
- **TestNG / JUnit 5** as the test runner
- **Cucumber (BDD)** for writing test cases in plain English
- **Allure** for generating visual test reports

Each student writes **10 API tests** and **10 UI tests** covering both Admin and Normal User roles.

---

## Project Structure

```
B21_AI_Group_14/
│
├── application/                         ← The web app we are testing
│   ├── qa-training-app.jar              ← Spring Boot executable (the live app)
│   └── application.properties           ← App config (database, port, etc.)
│
├── automation-framework/                ← Our test project (Maven)
│   ├── pom.xml                          ← Dependencies and build config
│   └── src/test/
│       ├── java/
│       │   ├── runners/
│       │   │   └── TestRunner.java      ← Entry point that launches all tests
│       │   └── stepdefinitions/
│       │       ├── DashboardAPISteps.java   ← API test logic (REST calls)
│       │       ├── DashboardUISteps.java    ← Dashboard UI test logic (browser)
│       │       ├── HealthCheckSteps.java    ← App health/Swagger check
│       │       └── PlantsUISteps.java       ← Plants page UI test logic
│       └── resources/
│           └── features/
│               ├── DashboardAPI_215516N.feature   ← 10 API test scenarios
│               ├── DashboardUI_215516N.feature    ← 10 UI test scenarios
│               ├── HealthCheck.feature            ← 2 health check scenarios
│               └── PlantsUI_215517T.feature       ← 22 Plants UI scenarios
│
├── README.md
├── PROJECT_OVERVIEW.md                  ← This file
└── HOW_TO_RUN.md                        ← Setup and run instructions
```

---

## Technology Stack — What Each Tool Does

### 1. Cucumber (BDD — Behavior Driven Development)
Cucumber lets us write test cases in plain English inside `.feature` files using a language called **Gherkin**. This means non-developers (managers, testers) can read and understand what's being tested.

Example from `DashboardAPI_215516N.feature`:
```gherkin
Scenario: API_Dashboard_GetPlantSum_Admin_001
  Given an Admin token is available
  When I send a GET request to "/api/plants/summary"
  Then the API response status code should be 200
  And the response should contain "totalPlants" and "lowStockPlants" integers
```
Every line (`Given`, `When`, `Then`) is matched to a Java method in a step definition file.

### 2. Selenium WebDriver
Selenium controls a real Chrome browser — opening pages, clicking buttons, filling forms — just like a human user would. It's used for all UI tests.

### 3. REST Assured
REST Assured makes HTTP requests to the backend API and checks the responses. It's used for API tests (no browser needed).

### 4. JUnit 5
JUnit is the test runner framework. It manages the test lifecycle and provides the `Assertions` class used to check that expected values match actual values.

### 5. Allure Reporting
Allure collects all test results and generates a beautiful visual HTML report with charts, pass/fail counts, and step-by-step details.

### 6. Maven / mvnd (Maven Daemon)
Maven is the build tool that manages all the above libraries (downloads them, compiles the code, and runs tests). `mvnd` is the faster daemon version installed on this machine.

---

## Important Code Explained

### `pom.xml` — The Project Brain
**File:** [automation-framework/pom.xml](automation-framework/pom.xml)

This is the Maven project configuration file. Think of it as a shopping list of ingredients:

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>  <!-- Use Java 17 -->
    <cucumber.version>7.15.0</cucumber.version>
    <selenium.version>4.21.0</selenium.version>
    <restassured.version>5.4.0</restassured.version>
    <allure.version>2.27.0</allure.version>
</properties>
```

The `<dependencies>` block tells Maven to download and include Selenium, REST Assured, Cucumber, JUnit, and Allure.

The `<build>` section configures the test runner plugin (`maven-surefire-plugin`) to:
- **Not fail the build when tests fail** (`testFailureIgnore=true`) — useful so we can still see reports even if some tests break
- **Enable AspectJ weaving** — a technical requirement for Allure to capture detailed step data
- **Tell Allure where to save results** (`allure-results` folder in `target/`)

---

### `TestRunner.java` — The Test Launcher
**File:** [automation-framework/src/test/java/runners/TestRunner.java](automation-framework/src/test/java/runners/TestRunner.java)

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
public class TestRunner {
}
```

This tiny class is the entry point for all tests. The annotations (lines starting with `@`) are instructions:

| Annotation | Plain English meaning |
|---|---|
| `@Suite` | "This class launches a test suite" |
| `@IncludeEngines("cucumber")` | "Use Cucumber to run the tests" |
| `@SelectClasspathResource("features")` | "Find test scenarios in the `features/` folder" |
| `GLUE_PROPERTY_NAME = "stepdefinitions"` | "The Java code for each step lives in the `stepdefinitions` package" |
| `PLUGIN_PROPERTY_NAME` | "Print results to console AND send to Allure for reporting" |

---

### `DashboardAPISteps.java` — API Test Logic
**File:** [automation-framework/src/test/java/stepdefinitions/DashboardAPISteps.java](automation-framework/src/test/java/stepdefinitions/DashboardAPISteps.java)

This class handles the 10 Dashboard API test scenarios. Key parts:

**Getting an authentication token:**
```java
private String getToken(String username, String password) {
    Map<String, String> credentials = new HashMap<>();
    credentials.put("username", username);
    credentials.put("password", password);

    Response res = given()
            .contentType(ContentType.JSON)
            .body(credentials)
            .when()
            .post("/api/auth/login");

    return res.jsonPath().getString("token");
}
```
This logs in to the app's API and extracts the JWT token from the response. This token is then attached to all subsequent requests as proof of identity.

**Sending a GET request:**
```java
@When("I send a GET request to {string}")
public void i_send_a_get_request_to(String endpoint) {
    RequestSpecification req = given().contentType(ContentType.JSON);
    if (token != null) {
        req.header("Authorization", "Bearer " + token);
    }
    response = req.when().get(endpoint);
}
```
The `{string}` in the annotation matches whatever URL is written in the feature file (e.g. `"/api/plants/summary"`). If a token exists, it's added to the request header.

**Checking response data:**
```java
@And("the response should contain {string} and {string} integers")
public void the_response_should_contain_and_integers(String key1, String key2) {
    response.then().body("$", hasKey(key1))
                   .body("$", hasKey(key2));
}
```
This checks that the JSON response body contains specific fields (like `totalPlants` and `lowStockPlants`).

---

### `DashboardUISteps.java` — Dashboard Browser Test Logic
**File:** [automation-framework/src/test/java/stepdefinitions/DashboardUISteps.java](automation-framework/src/test/java/stepdefinitions/DashboardUISteps.java)

This class controls Chrome to test the Dashboard web page.

**Setting up Chrome:**
```java
public DashboardUISteps() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    driver = new ChromeDriver(options);         // Opens a real Chrome window
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Wait up to 10 seconds
}
```
A real visible Chrome window opens when tests run. `WebDriverWait` is a "smart pause" — instead of sleeping a fixed time, it waits for elements to appear (up to 10 seconds), then continues.

**Logging in:**
```java
private void loginAs(String username, String password) {
    driver.get(BASE_URL + "/ui/login");        // Open the login page
    WebElement usernameField = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.name("username"))
    );
    usernameField.sendKeys(username);          // Type the username
    driver.findElement(By.name("password")).sendKeys(password);
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dashboard-card")));
}
```
This finds HTML elements by their `name` attribute, types into them, and clicks the submit button.

**Cleaning up after each test:**
```java
@After
public void tearDown() {
    if (driver != null) {
        driver.quit();   // Close Chrome after every scenario
    }
}
```
The `@After` hook runs automatically after each test scenario to close the browser so Chrome doesn't pile up with open windows.

---

### `PlantsUISteps.java` — Plants Page Browser Test Logic
**File:** [automation-framework/src/test/java/stepdefinitions/PlantsUISteps.java](automation-framework/src/test/java/stepdefinitions/PlantsUISteps.java)

This is the largest file (416 lines) and handles 22 scenarios for the Plants management page.

**Adding a new plant:**
```java
@When("I fill valid data and click Save")
public void i_fill_valid_data_and_click_save() {
    driver.get(BASE_URL + "/ui/plants/add");
    driver.findElement(By.name("name")).sendKeys("Plant " + System.currentTimeMillis());
    Select categorySelect = new Select(driver.findElement(By.name("categoryId")));
    if (categorySelect.getOptions().size() > 1) categorySelect.selectByIndex(1);
    driver.findElement(By.name("price")).sendKeys("19.99");
    driver.findElement(By.name("quantity")).sendKeys("10");
    driver.findElement(By.xpath("//button[contains(text(), 'Save')]")).click();
}
```
`System.currentTimeMillis()` is used to generate a unique plant name each time so tests don't create duplicate entries.

**Verifying sorting works:**
```java
@Then("plants are sorted by name")
public void plants_are_sorted_by_name() {
    Assertions.assertTrue(driver.getCurrentUrl().contains("sortField=name"));
}
```
Instead of reading all rows and comparing alphabetical order (complex), this checks the URL — if clicking the Name header added `sortField=name` to the URL, the sorting was triggered correctly.

**Form validation check:**
```java
@Then("a validation error is shown for Plant Name")
public void a_validation_error_is_shown_for_plant_name() {
    wait.until(d -> d.findElements(By.className("text-danger"))
                     .stream()
                     .anyMatch(e -> e.getText().length() > 0));
}
```
This waits until at least one element with the CSS class `text-danger` (Bootstrap's red error style) has visible text — meaning a validation message appeared on screen.

---

### `HealthCheckSteps.java` — App Health & Swagger Tests
**File:** [automation-framework/src/test/java/stepdefinitions/HealthCheckSteps.java](automation-framework/src/test/java/stepdefinitions/HealthCheckSteps.java)

Two simple scenarios:
1. **API health check** — calls `/v3/api-docs` and checks HTTP 200
2. **Swagger UI check** — opens `/swagger-ui.html` in a **headless** (invisible) Chrome and checks the page title contains "Swagger UI"

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");   // No visible window for this test
```

---

## Test Scenarios Summary

### API Tests (10 scenarios — `DashboardAPI_215516N.feature`)
| # | Test Name | What It Checks |
|---|---|---|
| 001 | GetPlantSum_Admin | Admin can fetch total plants and low-stock count |
| 002 | GetCategorySum_Admin | Admin can fetch main and sub category counts |
| 003 | GetPlantSum_User | Normal User can also fetch plant summary |
| 004 | GetCategorySum_User | Normal User can fetch category summary |
| 005 | GetHealth_Admin | Health endpoint returns `status: "UP"` for Admin |
| 006 | GetHealth_User | Health endpoint returns `status: "UP"` for Normal User |
| 007 | GetSalesPage_Admin | Paginated sales data contains `content`, `pageable`, `totalElements` |
| 008 | Unauthorized_NoToken | Accessing without a token returns **401 Unauthorized** |
| 009 | InvalidToken | Using a fake token returns **401** |
| 010 | CreateCategory_UserForbidden | Normal User trying to create a category gets **403 Forbidden** |

### Dashboard UI Tests (10 scenarios — `DashboardUI_215516N.feature`)
| # | Test Name | What It Checks |
|---|---|---|
| 001 | RenderCards_Admin | Admin sees 4 dashboard cards |
| 002 | SummaryData_Admin | Dashboard shows numeric counts |
| 003 | CardHoverAnimation | Cards visually respond on hover |
| 004 | ManageCategoriesNav | "Manage Categories" button goes to `/ui/categories` |
| 005 | ManagePlantsNav | "Manage Plants" button goes to `/ui/plants` |
| 006 | RenderCards_User | Normal User sees cards but NO "Create Sale" button |
| 007 | ViewSalesNav_User | "View Sales" button goes to `/ui/sales` |
| 008 | SummaryCounts_User | No 403/Unauthorized errors on dashboard for Normal User |
| 009 | OpenInventoryDisabled | "Open Inventory" link is disabled with tooltip |
| 010 | ResponsiveLayout | Mobile 375px viewport collapses sidebar |

### Plants UI Tests (22 scenarios — `PlantsUI_215517T.feature`)
Covers: page loading, column display, search, filter, reset, sort by name/price/stock, low stock badge, pagination, add plant, form validation (name/category/price/quantity), edit plant, delete plant, cancel form.

### Health Check Tests (2 scenarios — `HealthCheck.feature`)
Covers: OpenAPI docs endpoint and Swagger UI page accessibility.

---

## How BDD Works (The Big Picture)

```
Feature File (.feature)         Step Definition (.java)          Browser / API
─────────────────────          ──────────────────────          ──────────────
"Given I am logged in    ────► i_am_logged_in_as_admin()  ────► Chrome opens login page,
 as Admin"                      calls loginAs("admin",           types credentials,
                                "admin123")                      clicks submit

"When I click the        ────► i_click_the_button()       ────► Chrome finds the button
 Manage Plants button"          btn.click()                      and clicks it

"Then I should be        ────► i_should_be_redirected_to()────► Chrome checks current URL
 redirected to /ui/plants"      assertTrue(url.contains(...))    contains "/ui/plants"
```

The feature file describes **what** to test in English. The step definition file contains the **how** in Java. Cucumber connects them automatically by matching the text.

---

## Key Design Decisions

| Decision | Reason |
|---|---|
| Tests run against a live app + real MySQL | More realistic — catches integration bugs that mocks would miss |
| `testFailureIgnore=true` in Maven | Lets all tests run even if some fail, so we get a complete report |
| `WebDriverWait` instead of `Thread.sleep()` | Faster and more reliable — waits only as long as needed |
| Unique plant names using `System.currentTimeMillis()` | Prevents test data collisions when tests run repeatedly |
| JWT token fetched fresh per scenario | Ensures each test is independent and tokens don't expire mid-run |
