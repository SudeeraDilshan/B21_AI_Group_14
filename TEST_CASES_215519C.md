# Sales Module — Automated Test Cases (Tester 215519C)

**Project:** IS3440 QA Training App (Spring Boot Plant Management System)
**Group:** B21 — AI Group 14
**Framework:** Cucumber 7.15 (BDD) + JUnit 5 + Selenium 4.21 (UI) + REST Assured 5.4 (API) + Allure reporting
**Feature files:** `automation-framework/src/test/resources/features/SalesUI_215519C.feature`, `SalesAPI_215519C.feature`
**Step definitions:** `automation-framework/src/test/java/stepdefinitions/SalesUISteps.java`, `SalesAPISteps.java`
**Execution date:** 2026-06-12

---

## 1. Execution Result — ALL PASSED ✅

```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Suite | Scenarios | Result |
|---|---|---|
| Sales UI (`UI_*_001`–`010`) | 10 | ✅ 10 passed |
| Sales API (`API_*_011`–`020`) | 10 | ✅ 10 passed |
| Health Check (api-docs + Swagger UI) | 2 | ✅ 2 passed |
| **Total** | **22** | **✅ 22 passed** |

Run command (app must be running on `http://localhost:8080` first):

```
cd automation-framework
mvn test          # or: mvnd test
mvn allure:serve  # optional: open the Allure report
```

---

## 2. How the Framework Works

- **Cucumber/Gherkin (BDD):** Each test case is a `Scenario` written in plain English. Every line (`Given/When/Then`) is matched to a Java method (a *step definition*) by annotation text.
- **UI tests (Selenium):** Each `@SalesUI` scenario launches a real Chrome browser, logs in through the actual login form at `/ui/login`, drives the Sales pages by clicking links/buttons and typing into fields, then asserts on the resulting page. The browser is closed in an `@After` hook.
- **API tests (REST Assured):** Scenarios call the REST endpoints directly (`/api/auth/login`, `/api/sales`, `/api/sales/{id}`, `/api/sales/plant/{plantId}?quantity=`) with or without a JWT `Authorization: Bearer` header, then assert on status codes and JSON bodies.
- **Test users:** `admin`/`admin123` (ROLE_ADMIN) and `testuser`/`test123` (ROLE_USER).
- **TestRunner** (`runners/TestRunner.java`) is a JUnit 5 suite that picks up every feature file on the classpath and wires it to the `stepdefinitions` package, with Allure attached as a reporting plugin.

---

## 3. UI Test Cases Explained (001–010)

All UI scenarios share a `Background` step that opens `/ui/login` and asserts the page title contains "Login" — this guarantees the app is reachable before each test.

| ID | What it tests | How the automation does it | Result |
|---|---|---|---|
| UI_001 Create Sale | An admin can sell a plant and the sale appears in the list | Logs in as admin → opens Sales → clicks **Sell Plant** → enters quantity 1 → clicks the submit button → asserts redirect back to `/ui/sales` | ✅ Pass |
| UI_002 Quantity validation | Quantity 0 is rejected with a validation message | Opens the Sell form → types `0` → clicks Sell → looks for a `.invalid-feedback`/`.alert` element, falling back to the browser-native HTML5 `validationMessage` (the input has `min="1"`) | ✅ Pass |
| UI_003 Cancel button | Cancel abandons the form | Opens the Sell form → clicks **Cancel** → asserts the URL returns to the Sales list | ✅ Pass |
| UI_004 Sort by Quantity | Clicking the Quantity header sorts the table | Opens Sales → clicks the `Quantity` `<th>` → asserts the sorted Sales page loads | ✅ Pass |
| UI_005 Sort by Total Price | Clicking the Total Price header sorts the table | Same pattern as UI_004 with the `Total Price` header | ✅ Pass |
| UI_006 User can view Sales | A non-admin user can open the Sales list | Logs in as `testuser` → clicks **Sales** in the side navigation → asserts the page loads and a `<table>` is present | ✅ Pass |
| UI_007 Default sort | Sales are sorted by Sold Date descending by default | Opens `/ui/sales` with no parameters → asserts the table renders (default server sort is `soldAt desc`) | ✅ Pass |
| UI_008 Empty list message | "No sales found" shows when there are no sales | Opens the Sales page as a user (precondition: empty DB) | ✅ Pass *(see §5 — lenient)* |
| UI_009 Sort by Sold At | Clicking the Sold At header re-sorts the table | Clicks the `Sold At` `<th>` → asserts the Sales page reloads sorted | ✅ Pass |
| UI_010 Pagination | Pagination controls appear when sales exceed one page (10 rows) | Opens the Sales page (precondition: >10 sales) | ✅ Pass *(see §5 — lenient)* |

---

## 4. API Test Cases Explained (011–020)

| ID | What it tests | How the automation does it | Result |
|---|---|---|---|
| API_011 Admin retrieves sales | `GET /api/sales` with admin JWT returns the sales list | Logs in via `/api/auth/login`, sends GET with `Bearer` token → asserts **200**, body is a list, and each item has `id`, `plant`, `quantity` | ✅ Pass |
| API_012 Empty sales list | `GET /api/sales` returns `200` + `[]` when no sales exist | Same GET as 011 → asserts **200** and no error (status < 400) | ✅ Pass *(see §5 — lenient)* |
| API_013 Admin sells a plant | `POST /api/sales/plant/1?quantity=1` creates a sale | Sends authenticated POST → asserts **201**, response contains sale `id`, `plant` object and `quantity = 1` | ✅ Pass |
| API_014 Quantity 0 rejected | Selling quantity 0 returns a validation error | Sends `POST /api/sales/plant/1?quantity=0` → asserts **400** and the body contains an `error` field (`"Quantity must be greater than 0"`) | ✅ Pass |
| API_015 Unauthenticated read | `GET /api/sales` without a token is blocked | Sends GET with no `Authorization` header → asserts **401** | ✅ Pass |
| API_016 User retrieves sales | A normal user (ROLE_USER) may read sales | Logs in as `testuser`, sends authenticated GET → asserts **200** + list schema | ✅ Pass |
| API_017 Unauthenticated delete | `DELETE /api/sales/{id}` without a token is blocked | Sends DELETE with no header → asserts **401** (sale untouched) | ✅ Pass |
| API_018 Sale not found | Requesting a non-existing sale returns 404 | `GET /api/sales/99999` with admin token → asserts **404** + "not found" error | ✅ Pass |
| API_019 Non-admin delete | A ROLE_USER must NOT be able to delete a sale (expects 403) | `DELETE /api/sales/1` with `testuser` token → assertion accepts **403 or 404** | ✅ Pass *(see §5 — masks a real defect)* |
| API_020 Server error | Backend/DB failure should yield 500 | `DELETE /api/sales/99999` → assertion accepts **500, 404 or 204** because a DB outage cannot be simulated against the live app | ✅ Pass *(see §5 — lenient)* |

---

## 5. Honest Caveats — Why "All Passed" Needs Context

The suite is green, but several step definitions use **lenient or placeholder assertions** so the suite stays stable against a live, pre-populated database. These are worth knowing before quoting "22/22":

1. **API_019 masks a genuine defect.** The step accepts `403 OR 404`. In this run sale id `1` no longer exists, so the server returned `404` and the test passed. However, the earlier manual investigation ([SALES_MODULE_TEST_REPORT.md](SALES_MODULE_TEST_REPORT.md), DEFECT-01) proved that when the sale **does** exist, a non-admin delete returns **204 and actually deletes the sale** — an authorization vulnerability. The spec requires 403. The Cucumber test passing here is a false negative caused by the lenient assertion and DB state.
2. **API_020 cannot truly test a 500.** A backend/DB outage can't be simulated against the single live instance, so the step accepts 404/204 as well. Real coverage would need a mock or a failing test profile.
3. **Placeholder assertions** — `UI_008` ("No sales found"), `UI_010` (pagination), and `API_012` (empty array) effectively `assertTrue(true)` because they depend on DB state (completely empty DB, or >10 sales) that the suite does not set up or clean up itself.
4. **Sorting checks are shallow.** UI_004/005/009 click the column header and assert the page reloads — they do not read the rendered rows and verify the actual order. (Row-order verification was done in the manual run and passed.)
5. **Data side effects.** UI_001 and API_013 create real sales and reduce plant stock on every run; nothing is rolled back.

**Suggested hardening (future work):** seed/clean test data via the API in `@Before` hooks, assert real row order for the sorting cases, tighten API_019 to require exactly 403 against an existing sale (this will correctly FAIL until the authorization bug is fixed), and cover API_020 with a mocked service test.

---

## 6. Environment

| Item | Value |
|---|---|
| Application | `qa-training-app.jar`, Spring Boot 3.3.4, `http://localhost:8080` |
| Database | MySQL 8.0 (`qa_training`) |
| Browser | Chrome 149 via Selenium Manager |
| Java / build | Java 17, Maven (surefire 3.2.5, `testFailureIgnore=true`) |
| Reporting | Allure results in `automation-framework/target/allure-results` |
