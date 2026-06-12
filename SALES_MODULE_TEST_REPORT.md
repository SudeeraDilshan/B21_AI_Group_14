# Sales Module — Test Execution Report

**Project:** IS3440 QA Training App (Spring Boot Plant Management System)
**Group:** B21 — AI Group 14
**Module under test:** Sales (UI + REST API)
**Tester ID:** 215519C
**Execution date:** 2026-06-12
**Application version:** `qa-training-app.jar` (Spring Boot 3.3.4) — *application code unchanged*

---

## 1. Executive Summary

20 test cases were executed against the **live, running application** — 10 UI cases (`UI_*_Salse_001`–`010`) driven through a real Chrome browser via Selenium, and 10 API cases (`API_*_011`–`020`) executed directly against the REST endpoints.

| Result | Count | Cases |
|---|---|---|
| ✅ **Pass** | 18 | UI_001–010, API_011, 012, 013, 014, 015, 016, 017, 018 |
| ❌ **Fail (defect)** | 1 | API_019 — non-admin can delete a sale |
| ⚠️ **Not executable (blocked)** | 1 | API_020 — cannot simulate backend/DB outage |

**Key finding:** A genuine **authorization defect** was found. `DELETE /api/sales/{id}` is protected by *authentication only*, not by the **ADMIN** role. A normal user (`testuser`, `ROLE_USER`) successfully deleted a sale and received `204 No Content` where the specification requires `403 Forbidden`.

**Secondary observation:** The "quantity must be > 0" validation (UI_002) is enforced, but on the UI the **browser-native** message (`min="1"`) fires before the form is submitted, so the server-side message text *"Quantity must be greater than 0"* is not what the end user sees in the browser (it is the configured server text, confirmed via API_014).

---

## 2. Test Environment

| Item | Value |
|---|---|
| Application URL | `http://localhost:8080` |
| Database | MySQL 8.0, schema `qa_training` |
| App framework | Spring Boot 3.3.4, Hibernate 6.5.3, Spring Security (JWT + form login) |
| Admin account | `admin` / `admin123` → `ROLE_ADMIN` |
| User account | `testuser` / `test123` → `ROLE_USER` |
| UI automation | Python Selenium 4.x, Chrome (headless), Selenium Manager driver |
| API automation | Python `requests` 2.33 |
| Sales page defaults | `page=0, size=10, sortField=soldAt, sortDir=desc` (pagination appears when sales > 10) |

### Methodology
- **No application source was modified.** Controllers, templates, and the security config were read only (decompiled from the JAR) to derive accurate expected behavior.
- API status codes, response bodies, and inventory before/after were captured directly.
- UI cases used a real browser: clicking links/buttons, selecting dropdowns, submitting forms, and reading rendered table rows. Sorting correctness was cross-checked against the authoritative order returned by `GET /api/sales`.

> **Data side-effects (intentional):** Executing these cases mutates data by design. API_012 (empty-list) required deleting all sales — the two original seed sales were removed. Sale-creation cases (API_013, UI_001) plus 12 seeded sales (for sorting/pagination) reduced plant *rose* (id 2) stock. Note: deleting a sale does **not** restore plant stock. At end of run the DB held 13 test sales.

---

## 3. API Test Results (011–020)

> Endpoints: `POST /api/auth/login`, `GET /api/sales`, `GET /api/sales/{id}`, `POST /api/sales/plant/{plantId}?quantity=`, `DELETE /api/sales/{id}`.

| ID | Test | Expected | Actual | Status |
|---|---|---|---|---|
| API_011 | Admin retrieve all sales | 200 + list, each sale has id, plant{}, quantity, totalPrice, soldAt | 200; list returned; schema valid | ✅ Pass |
| API_012 | Admin empty list when none exist | 200 + `[]` | 200; body `[]` | ✅ Pass |
| API_013 | Admin sell plant (valid qty) | 201; sale created; correct totalPrice; stock reduced | 201; totalPrice 300.0 (60×5); stock 40250→40245 | ✅ Pass |
| API_014 | Sell with quantity = 0 | 400; no sale; inventory unchanged | 400; sales count 3→3; stock unchanged | ✅ Pass |
| API_015 | Unauthenticated GET sales | 401 | 401 | ✅ Pass |
| API_016 | User retrieve all sales | 200 + list | 200; list returned | ✅ Pass |
| API_017 | Unauthenticated DELETE sale | 401; not deleted | 401; sale still exists | ✅ Pass |
| API_018 | GET non-existing sale id | 404 + error | 404; `"Sale not found: 999999"` | ✅ Pass |
| API_019 | Non-admin DELETE sale | **403; not deleted** | **204; sale deleted** | ❌ **Fail** |
| API_020 | 500 on backend/DB failure | 500 | Not reproducible | ⚠️ Blocked |

### Detail & Evidence

**API_011 — Admin retrieve all sales** ✅
`GET /api/sales` with admin token → `200`. Each object validated to contain `id`, `plant` (with `id, name, price, quantity`), `quantity`, `totalPrice`, `soldAt`. Sample: `id=1, plant="coconut Updated", totalPrice=1000000.0`.

**API_012 — Empty list** ✅
After deleting all sales (ids `1`→204, `2`→204), `GET /api/sales` → `200` with body `[]`. No error returned.

**API_013 — Sell plant, valid quantity** ✅
`POST /api/sales/plant/2?quantity=5` (rose, price 60.0) → `201`. Response is a full Sale object; `totalPrice = 300.0` (= 60 × 5); `soldAt` present. Plant stock reduced **40250 → 40245** (exactly −5).

**API_014 — Quantity = 0 rejected** ✅
`POST /api/sales/plant/2?quantity=0` → `400`. Body: `{"error":"BAD_REQUEST","message":"Quantity must be greater than 0","status":400,"timestamp":...}`. Sales count unchanged (3→3); stock unchanged (40245→40245).

**API_015 — Unauthenticated read** ✅
`GET /api/sales` without token → `401`. Body: `{"status":401,"error":"UNAUTHORIZED","message":"Unauthorized - Use Basic Auth or JWT"}`.

**API_016 — User read** ✅
`GET /api/sales` with `testuser` token → `200` + list with full schema. Read access is permitted to non-admins (intended).

**API_017 — Unauthenticated delete** ✅
`DELETE /api/sales/{id}` without token → `401`; the targeted sale still exists afterward (verified via admin GET). *(Re-tested in isolation against a freshly created sale to remove cross-test interference.)*

**API_018 — Non-existing sale id** ✅
`GET /api/sales/999999` with admin token → `404`. Body: `{"status":404,"error":"NOT_FOUND","message":"Sale not found: 999999","timestamp":...}`.

**API_019 — Non-admin delete → DEFECT** ❌
`testuser` JWT decodes to `roles=["ROLE_USER"]`. `DELETE /api/sales/{id}` with that token returned **`204 No Content`** and the sale was **actually removed** (subsequent admin GET → 404). Specification requires **`403 Forbidden`** and the sale must remain.
**Root cause:** `SecurityConfig` secures `/api/**` as `authenticated` (with `/api/auth/**` permitAll) but applies **no role restriction** on the delete path, and `SaleController.delete(...)` carries no `@PreAuthorize`. Any authenticated principal can therefore delete sales. **This is an authorization (privilege-escalation) vulnerability.**

**API_020 — Internal server error (500)** ⚠️ Not executable
The precondition is "backend service or database unavailable (simulated)". This cannot be created in a healthy environment without stopping MySQL or the app process, which would break every other endpoint and invalidate the rest of the run. The handler path exists (`GlobalExceptionHandler.handleGeneric` → 500), but the condition could not be triggered here. **Recommended approach:** use a mock/Mockito layer or a test profile that forces the service to throw, then assert `500` — outside the scope of a live black-box run.

---

## 4. UI Test Results (001–010)

> Driven through Chrome via Selenium. Login → `/ui/login`; Sales page → `/ui/sales`; Sell form → `/ui/sales/new`. Sorting verified against authoritative `GET /api/sales` order.

| ID | Test | Expected | Actual | Status |
|---|---|---|---|---|
| UI_001 | Create a sale | Created; stock reduced; redirect to Sales list | Row added; stock 40243→40236 (−7); redirected to `/ui/sales` | ✅ Pass |
| UI_002 | Quantity validation (>0) | "Quantity must be greater than 0"; not created | Qty 0 rejected; browser blocks with `min=1` ("Value must be greater than or equal to 1.") | ✅ Pass *(see note)* |
| UI_003 | Cancel button | Redirect to Sales list | Cancel → `/ui/sales` | ✅ Pass |
| UI_004 | Sort by Quantity | Sorted correctly | asc `[2,4,6,7,9,11,13,15,18,22]`; toggles to desc | ✅ Pass |
| UI_005 | Sort by Total Price | Sorted correctly | asc `[120…1320]`; toggles to desc | ✅ Pass |
| UI_006 | User can view Sales list | Page loads; list visible | 10 rows visible as `testuser`; no "Sell Plant" button (admin-only) | ✅ Pass |
| UI_007 | Default sort = Sold At desc | Newest first | UI row order == API `soldAt` desc order | ✅ Pass |
| UI_008 | Empty sales message | "No sales found" | Shown; 0 data rows | ✅ Pass |
| UI_009 | Sort by Sold At | Sorted correctly | `sortField=soldAt` in URL; UI order == API `soldAt` asc order | ✅ Pass |
| UI_010 | Pagination controls | Displayed & functional | 13 sales → controls `[Previous,1,2,Next]`; page 2 navigates (`page=1`) | ✅ Pass |

### Detail & Evidence

**UI_001 — Create a sale** ✅
Navigated to Sales → clicked **Sell Plant** → selected *rose* → entered quantity **7** → clicked **Sell**. Redirected to `/ui/sales`; a new row with quantity 7 / total 420.00 appeared; plant stock reduced **40243 → 40236** (−7).
*(Note: the form's submit button is labeled "Sell", which is the "Save" action described in the test step.)*

**UI_002 — Quantity validation** ✅ *(with note)*
Entered quantity **0** and clicked **Sell**. The number input has `min="1"`, so **HTML5 client-side validation blocks submission** and shows the browser-native message *"Value must be greater than or equal to 1."* — the sale is not created (validation intent satisfied). The exact specified text *"Quantity must be greater than 0"* is the **server-side** `@Min` message; it is confirmed present and returned by the API (see API_014), but on the UI the browser intercepts qty 0 before the request reaches the server.

**UI_003 — Cancel button** ✅
On the Sell form, clicked **Cancel** → redirected to `/ui/sales`.

**UI_004 — Sort by Quantity** ✅
Clicked the **Quantity** column header: rows sorted ascending `[2,4,6,7,9,11,13,15,18,22]`; a second click toggled to descending `[31,27,25,22,18,15,13,11,9,7]`.

**UI_005 — Sort by Total Price** ✅
Clicked **Total Price** header: ascending `[120,240,360,420,540,660,780,900,1080,1320]`; second click toggled to descending. (Total Price = plant price × quantity.)

**UI_006 — User can view Sales** ✅
Logged in as `testuser`, clicked **Sales** in the side navigation → Sales page loaded with 10 rows visible. The **Sell Plant** button is correctly absent for non-admin users (`th:if="${isAdmin}"`).

**UI_007 — Default sort Sold At desc** ✅
Opening `/ui/sales` with no parameters yielded rows in `soldAt` **descending** order. UI quantity order `[11,15,22,6,13,27,2,31,9,18]` matched the authoritative API order sorted by `soldAt` desc exactly.

**UI_008 — Empty sales message** ✅
With zero sales in the system, the table showed **"No sales found"** and no data rows.

**UI_009 — Sort by Sold At** ✅
Clicked the **Sold At** header (toggles default desc → asc). URL contained `sortField=soldAt`; UI quantity order `[7,25,4,18,9,31,2,27,13,6]` matched the API order sorted by `soldAt` ascending exactly. *(Verified by cross-referencing the API, since the on-screen `soldAt` is shown at minute precision.)*

**UI_010 — Pagination** ✅
With 13 sales (> page size of 10), pagination controls rendered as `[Previous, 1, 2, Next]`. Clicking page **2** navigated to `…?page=1` and loaded the second page.

---

## 5. Defects & Observations

### 🔴 DEFECT-01 — Non-admin can delete sales (API_019) — *High severity (authorization)*
- **Endpoint:** `DELETE /api/sales/{id}`
- **Observed:** `ROLE_USER` token → `204 No Content`, sale permanently deleted.
- **Expected:** `403 Forbidden`, sale retained.
- **Root cause:** No role check on the delete path — `SecurityConfig` only requires `authenticated` for `/api/**`, and `SaleController.delete` has no `@PreAuthorize("hasRole('ADMIN')")`.
- **Recommended fix:** Restrict the delete endpoint to ADMIN, e.g. method-level `@PreAuthorize("hasRole('ADMIN')")` (note `@EnableMethodSecurity` is already on `SecurityConfig`) or an HTTP-level rule `requestMatchers(HttpMethod.DELETE, "/api/sales/**").hasRole("ADMIN")`.

### 🟡 OBS-01 — UI quantity-validation message text (UI_002)
The browser's `min="1"` constraint blocks qty 0 with a native message before the server's `@Min` message ("Quantity must be greater than 0") can be shown. Functionally correct (invalid input rejected) but the user-visible text differs from the specification. Consider relying on server-side validation display, or aligning the client message, if the exact wording is a requirement.

### ⚪ OBS-02 — API_020 not executable in live black-box run
Simulating a backend/DB outage requires fault injection (mock/test profile), which is incompatible with a single live application instance. Recommend covering 500-handling in an isolated unit/integration test.

---

## 6. Reproduction Notes
- App started with: `java -jar application\qa-training-app.jar --spring.datasource.username=root --spring.datasource.password=<password>`.
- API suite and Selenium UI suite were run against `http://localhost:8080`.
- Sorting/pagination cases self-seed 12 sales (plant *rose*, distinct quantities) and verify against the authoritative API ordering.
- The original seed sales were consumed by API_012 (empty-list precondition); plant *rose* stock reflects the cumulative test sales.

---

*Report generated from automated execution against the running application. Application code was not modified during testing.*
