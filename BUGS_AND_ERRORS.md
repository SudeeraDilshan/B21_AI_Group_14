# Bugs and Errors Report — B21 AI Group 14

## Overview

This document lists bugs and quality issues found in both the **test automation code** and the **target Spring Boot application**. Issues are grouped by severity.

---

## Part 1 — Test Code Bugs (automation-framework)

### BUG-T01 — False-Positive Assertions (`Assertions.assertTrue(true)`)

**Severity:** High  
**Files:** `PlantsUISteps.java`, `DashboardUISteps.java`

Multiple step definitions assert `true` unconditionally, meaning the test always passes regardless of what the application actually does. These scenarios appear green in the Allure report but provide **no real verification**.

| Method | Line | Scenario It Skips Verifying |
|--------|------|----------------------------|
| `only_matching_plant_records_are_displayed()` | 123 | Whether search results actually match the query |
| `only_plants_from_the_selected_category_are_displayed()` | 155 | Whether filtering by category works |
| `all_plants_are_shown_again_and_filters_are_cleared()` | 171 | Whether the reset actually cleared the filters |
| `the_next_page_of_plant_records_is_displayed()` | 239 | Whether pagination moved forward |
| `the_previous_page_of_plant_records_is_displayed()` | 259 | Whether pagination moved backward |
| `the_plant_details_are_updated_successfully()` | 379 | Whether the edit was saved |
| `the_plant_is_removed_from_the_list()` | 399 | Whether the plant was deleted |
| `the_cards_should_scale_up_and_cursor_should_change_to_pointer()` | DashboardUISteps:128 | Whether hover animation works |
| `the_sidebar_menu_should_collapse_or_hide()` | DashboardUISteps:192 | Whether responsive sidebar collapses |
| `the_cards_should_stack_vertically()` | DashboardUISteps:196 | Whether cards stack on mobile |

**Fix:** Replace each `Assertions.assertTrue(true)` with a real assertion. Examples:

```java
// For pagination next:
String newUrl = driver.getCurrentUrl();
Assertions.assertTrue(newUrl.contains("page=1") || !newUrl.equals(previousUrl), "Page did not change");

// For plant deletion:
wait.until(ExpectedConditions.urlContains("/ui/plants"));
List<WebElement> remaining = driver.findElements(By.xpath("//td[contains(text(), '" + deletedName + "')]"));
Assertions.assertTrue(remaining.isEmpty(), "Deleted plant still visible in list");
```

---

### BUG-T02 — Silent Exception Swallowing Hides Real Failures

**Severity:** High  
**Files:** `PlantsUISteps.java`, `DashboardUISteps.java`

Many step definitions wrap their code in `try/catch` and only print to console when something goes wrong, instead of failing the test. A broken UI interaction is silently ignored.

```java
// PlantsUISteps.java — loginAs()
} catch (Exception e) {
    System.out.println("Login page might be structured differently or already logged in: " + e.getMessage());
}
```

**Affected methods:**
- `loginAs()` in both `PlantsUISteps` and `DashboardUISteps`
- `i_click_next_on_the_plants_page()` — Next button not found = silent pass
- `i_click_previous()` — Previous button not found = silent pass
- `i_click_edit_on_an_existing_plant_change_fields_and_save()` — Edit fails = silent pass
- `i_click_delete_confirm_deletion_and_return_to_list()` — Delete fails = silent pass
- `i_click_the_button()` in `DashboardUISteps` — Button click fails = silent pass
- `the_link_should_be_disabled()` — Inventory link not found = silent pass
- `a_tooltip_displaying_should_appear_on_hover()` — Tooltip missing = silent pass

**Fix:** Remove the `try/catch` or re-throw the exception so the test fails properly:

```java
private void loginAs(String username, String password) {
    driver.get(BASE_URL + "/ui/login");
    WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
    driver.findElement(By.name("password")).sendKeys(password);
    usernameField.sendKeys(username);
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    wait.until(ExpectedConditions.urlContains("/ui/dashboard"));
}
```

---

### BUG-T03 — Duplicate WebDriver Instances Per Scenario (Resource Waste)

**Severity:** Medium  
**Files:** `DashboardUISteps.java`, `PlantsUISteps.java`

Both `DashboardUISteps` and `PlantsUISteps` create a new `ChromeDriver` in their constructors and both have an `@After tearDown()` method. Cucumber instantiates all step definition classes for every scenario, which means **two Chrome browser windows open for every scenario** — one that is used, and one that opens and immediately closes unused.

This also means every `@After` method in every class runs after every scenario, not just the class responsible for that scenario.

**Root cause:** No shared driver state; no `@CucumberContextConfiguration` or dependency injection to scope the WebDriver to a single instance per scenario.

**Fix:** Use Cucumber PicoContainer or Spring DI to share one WebDriver instance across all step definition classes:

```java
// SharedDriver.java
@ScenarioScoped
public class SharedDriver {
    public final WebDriver driver;
    public final WebDriverWait wait;

    public SharedDriver() {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
```

---

### BUG-T04 — Hardcoded Plant Name "Rose" Assumes Database State

**Severity:** Medium  
**File:** `PlantsUISteps.java:116`

```java
driver.findElement(By.name("name")).sendKeys("Rose"); // Assuming Rose exists
```

The search test hardcodes "Rose" and assumes this plant exists in the database. If the database is empty or reset, the search returns no results, but the test still passes because the assertion is `Assertions.assertTrue(true)` (see BUG-T01). This makes the test both data-dependent and meaningless at the same time.

**Fix:** Either seed a known plant before the test using a `@Before` step, or search for a plant name retrieved dynamically from the page's first row.

---

### BUG-T05 — Dashboard Count Assertions Are Too Broad

**Severity:** Medium  
**File:** `DashboardUISteps.java:97-110`

Three separate step definitions verify dashboard card counts using the same overly-permissive check:

```java
boolean hasDigits = driver.getPageSource().matches("(?s).*\\d+.*");
Assertions.assertTrue(hasDigits, "Counts are missing");
```

This regex matches if **any digit exists anywhere in the HTML source**, including in script tags, CSS, hidden attributes, or the page footer. It does not verify that the correct counts appear inside the correct dashboard cards.

**Fix:** Assert on text within the specific card element:

```java
WebElement plantCard = driver.findElement(By.xpath("//div[contains(@class,'dashboard-card') and .//h5[contains(text(),'Plants')]]"));
String cardText = plantCard.getText();
Assertions.assertTrue(cardText.matches(".*\\d+.*"), "Plant card shows no numeric count");
```

---

### BUG-T06 — Low Stock Test Has No Assertion

**Severity:** Medium  
**File:** `PlantsUISteps.java:218-223`

```java
@Then("low stock plant is shown in red and displays Low badge")
public void low_stock_plant_is_shown_in_red_and_displays_low_badge() {
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
    List<WebElement> badges = driver.findElements(
        By.xpath("//span[contains(@class, 'bg-danger') and contains(text(), 'Low')]"));
    // Asserting conditionally since db state may vary
}
```

The `badges` list is found but never asserted against. The test always passes, even if there are no low-stock plants and no badges at all.

**Fix:** Either seed low-stock data before the test, or skip the scenario when there is no low-stock data (use Cucumber's `@ConditionalIgnore` or `Assume.assumeTrue()`):

```java
Assumptions.assumeFalse(badges.isEmpty(), "No low-stock plants in DB, skipping");
Assertions.assertFalse(badges.isEmpty(), "Expected at least one Low badge but found none");
```

---

### BUG-T07 — Sort Tests Only Verify URL, Not Actual Data Order

**Severity:** Low  
**File:** `PlantsUISteps.java:185-209`

The three sort scenarios (Name, Price, Stock) only check that the URL contains `sortField=xxx`. They do not verify that the rows in the table are actually sorted in the expected order.

```java
@Then("plants are sorted by name")
public void plants_are_sorted_by_name() {
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
    Assertions.assertTrue(driver.getCurrentUrl().contains("sortField=name"));
}
```

The URL could contain `sortField=name` while the server-side sorting logic is broken and returns results in random order. The test would still pass.

**Fix:** Read the first column values from all visible rows and assert they are in ascending alphabetical (or numeric) order:

```java
List<WebElement> nameCells = driver.findElements(By.cssSelector("tbody tr td:nth-child(1)"));
List<String> names = nameCells.stream().map(WebElement::getText).collect(Collectors.toList());
List<String> sorted = new ArrayList<>(names);
Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);
Assertions.assertEquals(sorted, names, "Plants are not sorted by name ascending");
```

---

### BUG-T08 — Edit Plant Does Not Clear Field Before Appending

**Severity:** Low  
**File:** `PlantsUISteps.java:369`

```java
driver.findElement(By.name("name")).sendKeys(" Updated");
```

`sendKeys()` appends text to the existing field value without clearing it first. The plant name becomes `"Original Name Updated"`. While this might still save successfully, it is not the intended edit behaviour and could produce unexpected names with many spaces.

**Fix:** Clear the field before typing:

```java
WebElement nameField = driver.findElement(By.name("name"));
nameField.clear();
nameField.sendKeys("Updated Plant Name");
```

---

### BUG-T09 — `@After` in `HealthCheckSteps` May Conflict

**Severity:** Low  
**File:** `HealthCheckSteps.java`

The `HealthCheckSteps` class uses a headless Chrome instance. If it also has an `@After` teardown, it could quit a driver at an unexpected time. Without seeing the file contents, this should be verified to ensure the headless driver is scoped correctly and doesn't affect other scenarios.

---

## Part 2 — Application Bugs (qa-training-app)

### BUG-A01 — Inventory Feature Is Incomplete (Coming Soon)

**Severity:** Medium  
**Location:** `dashboard.html`, Inventory card

The Inventory card link is permanently disabled with the tooltip "Inventory page coming soon". This is an incomplete feature shipped in the production-equivalent JAR. No test can interact with inventory data; any tests expecting inventory functionality will fail.

**Evidence from HTML:**
```html
<a class="btn btn-sm btn-outline-primary disabled" 
   title="Inventory page coming soon">Manage Inventory</a>
```

---

### BUG-A02 — No Validation for Duplicate Plant Names

**Severity:** Medium  
**Tests:** `UI_Plants_AddSubmit_015`

The Add Plant tests use `System.currentTimeMillis()` in the plant name to avoid conflicts, implying the application may allow duplicate plant names. If duplicate names are allowed, the search results are ambiguous (multiple plants with the same name appear).

**To verify:** Try adding two plants with identical names and check whether the application accepts or rejects the second one.

---

### BUG-A03 — Low Stock Threshold Is Not Configurable or Documented

**Severity:** Low  
**Tests:** `UI_Plants_LowStockHighlight_011`

The low stock badge and red text appear when a plant's quantity falls below a certain threshold, but the threshold value is not documented anywhere in the project. Tests cannot reliably add a "low stock" plant without knowing the threshold.

**To verify:** Add plants with quantities 0, 1, 5, 10 and observe which ones trigger the "Low" badge.

---

### BUG-A04 — Normal User Can Access Add Plant URL Directly

**Severity:** High (if confirmed)  
**Test:** Not currently tested

The tests verify that Normal Users do not see the Edit/Delete buttons on the plants list. However, no test verifies that a Normal User who navigates directly to `/ui/plants/add` or `/ui/plants/edit/{id}` is blocked. If the application does not enforce server-side authorization on those URLs, a Normal User can bypass the UI restriction by typing the URL directly.

**To verify:** Log in as `testuser`, then navigate to `http://localhost:8080/ui/plants/add` manually.

---

### BUG-A05 — API Endpoint `/api/health` Field Name Inconsistency

**Severity:** Low  
**File:** `DashboardAPI_215516N.feature`

The health check test asserts `body("status", equalTo("UP"))`. If the Spring Boot actuator health endpoint returns `{"status": "UP"}`, this is fine. However, if the custom `/api/health` endpoint returns a different field name or casing (e.g. `"Status"`, `"state"`, or a nested object), the test would fail with a misleading error message.

**To verify:** Call `GET http://localhost:8080/api/health` with a valid token and inspect the raw JSON response.

---

### BUG-A06 — Pagination With `page=1` May Not Return Page 2

**Severity:** Low  
**File:** `PlantsUISteps.java:244`

```java
driver.get(BASE_URL + "/ui/plants?page=1");
```

Spring Boot pagination is **0-indexed** by default (`page=0` is the first page). Using `page=1` correctly navigates to the second page. However, the Previous button test assumes this returns a "page 2 or higher" state. If the database has fewer plants than the page size (default 10), `page=1` returns an empty result set rather than page 2, and the Previous button may not appear at all — silently passing due to BUG-T02.

---

## Summary Table

| ID | Location | Severity | Type | Status |
|----|----------|----------|------|--------|
| BUG-T01 | PlantsUISteps, DashboardUISteps | High | False-positive assertions | Open |
| BUG-T02 | PlantsUISteps, DashboardUISteps | High | Silent exception swallowing | Open |
| BUG-T03 | All UISteps classes | Medium | Duplicate WebDriver per scenario | Open |
| BUG-T04 | PlantsUISteps:116 | Medium | Hardcoded test data | Open |
| BUG-T05 | DashboardUISteps:97-110 | Medium | Overly broad count assertions | Open |
| BUG-T06 | PlantsUISteps:218-223 | Medium | Missing assertion on low stock | Open |
| BUG-T07 | PlantsUISteps:185-209 | Low | Sort tests verify URL only | Open |
| BUG-T08 | PlantsUISteps:369 | Low | sendKeys appends instead of replaces | Open |
| BUG-T09 | HealthCheckSteps | Low | Potential @After conflict | Needs review |
| BUG-A01 | dashboard.html | Medium | Inventory feature incomplete | Known |
| BUG-A02 | Plant API | Medium | Duplicate plant names allowed | Needs verification |
| BUG-A03 | Plants UI | Low | Low stock threshold undocumented | Open |
| BUG-A04 | Plants UI routes | High | Normal user URL bypass (unverified) | Needs testing |
| BUG-A05 | /api/health response | Low | Health field name inconsistency | Needs verification |
| BUG-A06 | Pagination | Low | 0-indexed page param edge case | Open |
