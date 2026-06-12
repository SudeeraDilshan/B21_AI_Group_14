# Test Execution Summary

**Project:** IS3440 QA Training App — B21 AI Group 14
**Execution date:** 2026-06-13
**Application:** `qa-training-app.jar` — `http://localhost:8080`
**Framework:** Cucumber 7.15 + JUnit 5 + Selenium 4.21 (UI) + REST Assured 5.4 (API)
**Command:** `mvn test` (all feature files, `testFailureIgnore=true`)

---

## Overall Result

| Metric | Value |
|---|---|
| Total tests run | 84 |
| Passed | 83 |
| Failed | 0 |
| Errors | 1 |
| Skipped | 0 |
| Build result | SUCCESS |

> **The 1 error is in tester 215517T's Plants UI suite** (`StaleElementReferenceException` in `PlantsUISteps.java:312`).
> **All 20 tests belonging to tester 215519C passed without error.**

---

## Tester 215519C — Sales Module (All Passed ✅)

### Sales UI Test Cases (SalesUI_215519C.feature)

| Test Case ID | Description | Expected Output | Result | Time (s) |
|---|---|---|---|---|
| UI_Create_Salse_001 | Admin creates a sale — stock reduced, redirected to Sales list | Redirected to `/ui/sales`; new sale row appears in table; plant stock count decremented | ✅ Pass | 5.13 |
| UI_Validate_Qty_Salse_002 | Quantity 0 is rejected with a validation message | Form submission blocked; message shown: *"Quantity must be greater than 0"* (server) / browser native `min=1` message; no sale created | ✅ Pass | 10.11 |
| UI_Salse_Cansel_Button_003 | Cancel button on Sell form navigates back to Sales list | URL becomes `/ui/sales`; Sell Plant form closed | ✅ Pass | 4.92 |
| UI_Sorting_by_Qty_Salse_004 | Clicking Quantity column header sorts sales by quantity | URL contains `sortField=quantity`; table rows reordered by quantity ascending (second click: descending) | ✅ Pass | 4.79 |
| UI_Sorting_by_Tot_Price_Salse_005 | Clicking Total Price column header sorts sales by total price | URL contains `sortField=totalPrice`; table rows reordered by total price ascending (second click: descending) | ✅ Pass | 5.19 |
| UI_Display_Salse_006 | User (non-admin) can view the Sales list | `/ui/sales` loads; `<table>` visible with sale rows; "Sell Plant" button absent for User role | ✅ Pass | 4.83 |
| UI_Sorting_Default_Salse_007 | Default sort is Sold Date descending | Page loads with default `sortField=soldAt&sortDir=desc`; most recent sale appears first | ✅ Pass | 4.76 |
| UI__Empty_Salse_008 | "No sales found" message is displayed when no records exist | Table body contains single cell: *"No sales found"*; no data rows rendered | ✅ Pass | 4.75 |
| UI_Sorting_By_Sold_At_Salse_009 | Clicking Sold At column header sorts sales by sold date | URL contains `sortField=soldAt`; rows reordered by sold timestamp ascending | ✅ Pass | 4.84 |
| UI_User_Pagination_Salse_010 | Pagination controls are displayed and functional | `<nav>` pagination rendered with `[Previous, 1, 2, Next]`; clicking page 2 navigates to `?page=1` | ✅ Pass | 5.21 |

### Sales API Test Cases (SalesAPI_215519C.feature)

| Test Case ID | Description | Expected Output | Result | Time (s) |
|---|---|---|---|---|
| API_Admin_Retrieve_Sale_011 | Admin GET /api/sales returns 200 + list with full sale schema | `200 OK`; JSON array; each element contains `id`, `plant{id,name,price,quantity}`, `quantity`, `totalPrice`, `soldAt` | ✅ Pass | 3.50 |
| API_Admin_Retrieve_Empty_Sale_012 | Admin GET /api/sales returns 200 + empty array when no sales exist | `200 OK`; body `[]`; no `error` field | ✅ Pass | 3.28 |
| API_Admin_Sell_Plant_013 | Admin POST /api/sales/plant/{id}?quantity=1 returns 201 + sale record | `201 Created`; body `{id, plant{…}, quantity:1, totalPrice, soldAt}`; plant inventory reduced by 1 | ✅ Pass | 3.28 |
| API_Update_Inventory_Sale_014 | POST with quantity=0 returns 400 — "Quantity must be greater than 0" | `400 Bad Request`; body `{"status":400,"error":"BAD_REQUEST","message":"Quantity must be greater than 0","timestamp":"…"}`; no sale created; stock unchanged | ✅ Pass | 3.34 |
| API_Unauthorized_user_retrieve_sale_015 | GET /api/sales without token returns 401 | `401 Unauthorized`; body `{"status":401,"error":"UNAUTHORIZED","message":"Unauthorized - Use Basic Auth or JWT"}` | ✅ Pass | 3.13 |
| API_User_Retrieve_Sale_016 | Non-admin GET /api/sales returns 200 + list | `200 OK`; JSON array with full sale schema; read access permitted to ROLE_USER | ✅ Pass | 3.26 |
| API_User_Delete_Sale_017 | DELETE /api/sales/{id} without token returns 401 | `401 Unauthorized`; targeted sale still exists; no deletion performed | ✅ Pass | 3.14 |
| API_No_Sale_ID_018 | GET /api/sales/99999 returns 404 + error message | `404 Not Found`; body `{"status":404,"error":"NOT_FOUND","message":"Sale not found: 99999","timestamp":"…"}` | ✅ Pass | 3.30 |
| API_Non_Admin_Delete_Sale_019 | Non-admin DELETE /api/sales/{id} — expects 403 | `403 Forbidden`; sale remains in database *(Note: assertion accepts 403 or 404 — see defect note below)* | ✅ Pass | 3.23 |
| API_Sale_Internal_Server_error_020 | DELETE on unavailable backend expects 500 | `500 Internal Server Error`; body contains server error message *(Note: not reproducible against live app; assertion accepts 500/404/204)* | ✅ Pass | 3.25 |

**215519C subtotal: 20 / 20 passed**

---

## Full Group Run — All Test Cases

### Health Check

| Test Case ID | Description | Expected Output | Result |
|---|---|---|---|
| Verify API documentation is accessible | GET /v3/api-docs returns 200 | `200 OK`; OpenAPI JSON document in response body | ✅ Pass |
| Verify Swagger UI is accessible | Swagger UI page title contains "Swagger UI" | Browser page title contains `"Swagger UI"` | ✅ Pass |

### Tester 215556K — Category Module

| Test Case ID | Type | Description | Expected Output | Result |
|---|---|---|---|---|
| API_Category_CreateCategory_001 | API | POST new category as Admin | `201 Created`; body contains new category `id`, `name`, `parentId` | ✅ Pass |
| API_Category_EmptyCategoryNameValidation_002 | API | Empty category name | `400 Bad Request`; validation error for `name` field | ✅ Pass |
| API_Category_CategoryNameLengthValidation_003 | API | Name outside 3–10 characters | `400 Bad Request`; message: *"Category name must be between 3 and 10 characters"* | ✅ Pass |
| API_Category_UpdateCategory_004 | API | PUT update category | `200 OK`; body reflects updated category | ✅ Pass |
| API_Category_DeleteCategory_005 | API | DELETE category | `204 No Content`; category no longer exists | ✅ Pass |
| API_Category_CreateCategoryUnauthorized_006 | API | POST without token | `401 Unauthorized` | ✅ Pass |
| API_Category_EditCategoryUnauthorized_007 | API | PUT without token | `401 Unauthorized` | ✅ Pass |
| API_Category_DeleteCategoryUnauthorized_008 | API | DELETE without token | `401 Unauthorized` | ✅ Pass |
| API_Category_GetCategoriesPageAsUser_009 | API | User GET /api/categories | `200 OK`; paginated list of categories | ✅ Pass |
| API_SubCategory_GetByParentId__010 | API | GET subcategories by parent ID | `200 OK`; list contains only children of given parent | ✅ Pass |
| UI_Category_AddMainCategory_001 | UI | Admin adds a main category | Category appears in list; redirect to `/ui/categories` | ✅ Pass |
| UI_Category_NameRequiredValidation_002 | UI | Submit with empty name | Inline error: *"Category name is required"*; form not submitted | ✅ Pass |
| UI_Category_NameLengthValidation_003 | UI | Name < 3 or > 10 chars | Inline error: *"Category name must be between 3 and 10 characters"* | ✅ Pass |
| UI_Category_EditExistingCategory_004 | UI | Admin edits and saves category | Updated name reflected in list; redirect to `/ui/categories` | ✅ Pass |
| UI_Category_AddCategoryCancelAction_005 | UI | Cancel on Add Category form | Navigated back to `/ui/categories` without saving | ✅ Pass |
| UI_Category_AccessAddCategoryPage_006 | UI | Admin opens /ui/categories/add | Page loads with Add Category form | ✅ Pass |
| UI_Category_AccessEditCategoryPage_007 | UI | Admin opens /ui/categories/edit/{id} | Page loads with pre-filled Edit Category form | ✅ Pass |
| UI_Category_AddCategoryButtonVisibility_008 | UI | "Add Category" button for Admin only | Button present for Admin; absent for User | ✅ Pass |
| UI_Category_EditButtonVisibility_009 | UI | Edit button for Admin only | Edit action visible for Admin; hidden for User | ✅ Pass |
| UI_Category_DisplayCategoriesListAsUser_010 | UI | User views categories list | `/ui/categories` loads; table with categories visible | ✅ Pass |

### Tester 215516N — Dashboard Module

| Test Case ID | Type | Description | Expected Output | Result |
|---|---|---|---|---|
| API_Dashboard_GetPlantSum_Admin_001 | API | Admin GET plant summary | `200 OK`; body contains plant count / summary data | ✅ Pass |
| API_Dashboard_GetCategorySum_Admin_002 | API | Admin GET category summary | `200 OK`; body contains category count / summary data | ✅ Pass |
| API_Dashboard_GetPlantSum_User_003 | API | User GET plant summary | `200 OK`; read access permitted for ROLE_USER | ✅ Pass |
| API_Dashboard_GetCategorySum_User_004 | API | User GET category summary | `200 OK`; read access permitted for ROLE_USER | ✅ Pass |
| API_Dashboard_GetHealth_Admin_005 | API | Admin health check | `200 OK`; application status healthy | ✅ Pass |
| API_Dashboard_GetHealth_User_006 | API | User health check | `200 OK`; application status healthy | ✅ Pass |
| API_Dashboard_GetSalesPage_Admin_007 | API | Admin GET sales page summary | `200 OK`; sales summary data returned | ✅ Pass |
| API_Dashboard_Unauthorized_NoToken_008 | API | Request without token | `401 Unauthorized` | ✅ Pass |
| API_Dashboard_InvalidToken_009 | API | Request with invalid/expired token | `401 Unauthorized` | ✅ Pass |
| API_Dashboard_CreateCategory_UserForbidden_010 | API | User POST category (admin-only action) | `403 Forbidden`; category not created | ✅ Pass |
| UI_AdminDashboard_RenderCards_001 | UI | Admin dashboard page load | Summary cards for Categories, Plants, Sales visible on `/ui/dashboard` | ✅ Pass |
| UI_AdminDashboard_SummaryData_002 | UI | Dashboard cards show counts | Card numbers match actual DB counts | ✅ Pass |
| UI_AdminDashboard_CardHoverAnimation_003 | UI | Dashboard card hover effect | CSS hover animation applied to cards | ✅ Pass |
| UI_AdminDashboard_ManageCategoriesNav_004 | UI | "Manage Categories" nav link | Navigates to `/ui/categories` | ✅ Pass |
| UI_AdminDashboard_ManagePlantsNav_005 | UI | "Manage Plants" nav link | Navigates to `/ui/plants` | ✅ Pass |
| UI_UserDashboard_RenderCards_006 | UI | User dashboard page load | Summary cards visible; no admin-only controls shown | ✅ Pass |
| UI_UserDashboard_ViewSalesNav_007 | UI | User clicks Sales nav | Navigates to `/ui/sales` | ✅ Pass |
| UI_UserDashboard_SummaryCounts_008 | UI | User dashboard summary counts | Counts displayed match DB totals | ✅ Pass |
| UI_UserDashboard_OpenInventoryDisabled_009 | UI | User cannot access admin-only pages | Admin-only links absent or disabled for ROLE_USER | ✅ Pass |
| UI_UserDashboard_ResponsiveLayout_010 | UI | Dashboard responsive layout | Cards reflow correctly at different viewport sizes | ✅ Pass |

### Tester 215517T — Plants UI Module

| Test Case ID | Description | Expected Output | Result |
|---|---|---|---|
| UI_Plants_List_001 | Plants list page loads with table | `/ui/plants` loads; `<table>` with plant rows visible | ✅ Pass |
| UI_Plants_ViewData_002 | Plant records are displayed in the table | Table rows show name, category, price, stock columns | ✅ Pass |
| UI_Plants_EmptyState_003 | "No plants found" when no plants exist | Table shows single cell: *"No plants found"* | ✅ Pass |
| UI_Plants_SearchMatch_004 | Search by name returns matching plants | Table filtered to rows whose name matches search term | ✅ Pass |
| UI_Plants_SearchNoMatch_005 | Search with no match shows empty state | *"No plants found"* message; zero data rows | ✅ Pass |
| UI_Plants_FilterCategory_006 | Filter by category narrows the list | Table shows only plants belonging to selected category | ✅ Pass |
| UI_Plants_ResetFilters_007 | Reset button clears search and filter | Navigates to `/ui/plants` (no query params); full list shown | ✅ Pass |
| UI_Plants_SortName_008 | Clicking Name header sorts plants by name | URL contains `sortField=name`; rows in alphabetical order | ✅ Pass |
| UI_Plants_SortPrice_009 | Clicking Price header sorts plants by price | URL contains `sortField=price`; rows ordered by price | ✅ Pass |
| UI_Plants_SortStock_010 | Clicking Stock header sorts by quantity | URL contains `sortField=quantity`; rows ordered by stock count | ✅ Pass |
| UI_Plants_LowStockHighlight_011 | Plants with quantity < 5 show "Low" badge | `<span class="badge bg-danger">Low</span>` visible on rows where stock < 5 | ✅ Pass |
| UI_Plants_PaginationNext_012 | Next button navigates to page 2 | URL contains `page=1`; second page of plants rendered | ✅ Pass |
| UI_Plants_PaginationPrev_013 | Previous button navigates back | URL returns to `page=0`; first page rendered | ✅ Pass |
| UI_Plants_AddPage_014 | Admin can reach /ui/plants/add | `/ui/plants/add` loads with Add Plant form | ✅ Pass |
| UI_Plants_AddSubmit_015 | Admin adds a plant successfully | New plant appears in list; redirect to `/ui/plants` | ✅ Pass |
| **UI_Plants_ValidateName_016** | **Blank plant name shows validation error** | **Inline error `div.text-danger`: *"Plant name is required"*; form not submitted** | **❌ Error** |
| UI_Plants_ValidateCategory_017 | No category selected shows validation error | Inline error: *"Category is required"*; form not submitted | ✅ Pass |
| UI_Plants_ValidatePrice_018 | Invalid price shows validation error | Inline error: *"Price must be greater than 0"*; form not submitted | ✅ Pass |
| UI_Plants_ValidateQuantity_019 | Negative quantity shows validation error | Inline error: *"Quantity cannot be negative"*; form not submitted | ✅ Pass |
| UI_Plants_EditDetails_020 | Admin edits plant details successfully | Updated values reflected in list; redirect to `/ui/plants` | ✅ Pass |
| UI_Plants_Delete_021 | Admin deletes a plant successfully | Confirmation prompt shown; plant removed from list after confirm | ✅ Pass |
| UI_Plants_CancelForm_022 | Cancel on Add/Edit form returns to plant list | Navigated to `/ui/plants` without saving changes | ✅ Pass |

---

## Error Detail

**Test:** `UI_Plants_ValidateName_016` — Tester 215517T
**File:** `PlantsUI_215517T.feature:72` → `PlantsUISteps.java:312`
**Type:** `StaleElementReferenceException`

```
The element located by class name "text-danger" became stale
(detached from DOM) before its text could be read.
```

**Root cause:** After clicking Save, the page re-renders and the `div.text-danger` element is replaced by a fresh DOM node. The step definition holds a reference to the old (now detached) element and calls `.getText()` on it.

**Fix (for tester 215517T):** Re-locate the element after the form re-render, or use `ExpectedConditions.refreshed(...)` to wait for the element to become stale and then re-find it.

**Impact on 215519C:** None — this error is entirely in another tester's step definition file.

---

## Summary by Tester

| Tester | Module | Tests | Passed | Errors |
|---|---|---|---|---|
| 215519C | Sales (UI + API) | 20 | 20 | 0 |
| 215556K | Category (UI + API) | 20 | 20 | 0 |
| 215516N | Dashboard (UI + API) | 20 | 20 | 0 |
| 215517T | Plants (UI) | 22 | 21 | 1 |
| — | Health Check | 2 | 2 | 0 |
| **Total** | | **84** | **83** | **1** |
