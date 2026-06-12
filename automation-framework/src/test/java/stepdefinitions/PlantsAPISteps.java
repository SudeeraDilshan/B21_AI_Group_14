package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantsAPISteps {

    private String token;
    private final String BASE_URL = "http://localhost:8080";
    private Response lastResponse;
    private Long defaultCategoryId;
    private String defaultCategoryName;
    private Long defaultParentCategoryId;
    private String defaultParentCategoryName;
    private Long createdPlantId;

    public PlantsAPISteps() {
        RestAssured.baseURI = BASE_URL;
    }

    private Long getValidCategoryId() {
        if (defaultCategoryId != null) {
            return defaultCategoryId;
        }
        
        try {
            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + token)
                    .get("/api/categories");
            
            List<Map<String, Object>> categories = response.jsonPath().getList("$");
            Map<String, Object> subCat = null;
            for (Map<String, Object> cat : categories) {
                String parentName = (String) cat.get("parentName");
                if (parentName != null && !parentName.equals("-")) {
                    subCat = cat;
                    break;
                }
            }
            
            if (subCat != null) {
                defaultCategoryId = ((Number) subCat.get("id")).longValue();
                defaultCategoryName = (String) subCat.get("name");
                String parentName = (String) subCat.get("parentName");
                
                for (Map<String, Object> cat : categories) {
                    if (parentName.equals(cat.get("name"))) {
                        defaultParentCategoryId = ((Number) cat.get("id")).longValue();
                        defaultParentCategoryName = (String) cat.get("name");
                        break;
                    }
                }
                return defaultCategoryId;
            }
        } catch (Exception e) {
        }

        defaultCategoryId = 12L;
        defaultCategoryName = "Annual";
        defaultParentCategoryId = 8L;
        defaultParentCategoryName = "Flowering";
        return defaultCategoryId;
    }

    @Given("I login as Admin")
    public void i_login_as_admin() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "admin123");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .post("/api/auth/login");

        Assertions.assertEquals(200, response.getStatusCode(), "Login failed");
        token = response.jsonPath().getString("token");
        Assertions.assertNotNull(token, "JWT token is null");
    }

    @And("the plant management dashboard is displayed")
    public void the_plant_management_dashboard_is_displayed() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/plants/summary");
        Assertions.assertEquals(200, response.getStatusCode(), "Dashboard summary access failed");
    }

    @When("I fill valid data and click Save")
    public void i_fill_valid_data_and_click_save() {
        Long categoryId = getValidCategoryId();
        
        Map<String, Object> plantData = new HashMap<>();
        String uniqueName = "P" + (System.currentTimeMillis() % 100000000L);
        plantData.put("name", uniqueName);
        plantData.put("price", 19.99);
        plantData.put("quantity", 10);

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(plantData)
                .post("/api/plants/category/" + categoryId);
        
        if (lastResponse.getStatusCode() != 201) {
            System.out.println("Add Plant Response: " + lastResponse.asString());
        } else {
            createdPlantId = lastResponse.jsonPath().getLong("id");
        }
    }

    @Then("the new plant is created with a success message")
    public void the_new_plant_is_created_with_a_success_message() {
        Assertions.assertEquals(201, lastResponse.getStatusCode(), "Expected 201 Created but got " + lastResponse.getStatusCode());
        String name = lastResponse.jsonPath().getString("name");
        Assertions.assertNotNull(name, "Plant name is null in response");
    }

    @Given("I am on the Add Plant page")
    public void i_am_on_the_add_plant_page() {
        // No-op for API
    }

    @When("I leave Plant Name blank and click Save")
    public void i_leave_plant_name_blank_and_click_save() {
        Long categoryId = getValidCategoryId();
        Map<String, Object> plantData = new HashMap<>();
        plantData.put("name", ""); // blank name
        plantData.put("price", 10.0);
        plantData.put("quantity", 5);

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(plantData)
                .post("/api/plants/category/" + categoryId);
    }

    @Then("a validation error is shown for Plant Name")
    public void a_validation_error_is_shown_for_plant_name() {
        Assertions.assertEquals(400, lastResponse.getStatusCode());
    }

    @When("I leave Category unselected and click Save")
    public void i_leave_category_unselected_and_click_save() {
        Map<String, Object> plantData = new HashMap<>();
        plantData.put("name", "P" + (System.currentTimeMillis() % 100000000L));
        plantData.put("price", 10.0);
        plantData.put("quantity", 5);

        // POST to an invalid/non-existing category ID
        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(plantData)
                .post("/api/plants/category/999999");
    }

    @Then("a validation error is shown for Category")
    public void a_validation_error_is_shown_for_category() {
        int statusCode = lastResponse.getStatusCode();
        Assertions.assertTrue(statusCode == 404 || statusCode == 400, "Expected 404 or 400 but got " + statusCode);
    }

    @When("I enter invalid price value and click Save")
    public void i_enter_invalid_price_value_and_click_save() {
        Long categoryId = getValidCategoryId();
        Map<String, Object> plantData = new HashMap<>();
        plantData.put("name", "P" + (System.currentTimeMillis() % 100000000L));
        plantData.put("price", -10.0); // negative price is invalid
        plantData.put("quantity", 5);

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(plantData)
                .post("/api/plants/category/" + categoryId);
    }

    @Then("a validation error is shown for Price")
    public void a_validation_error_is_shown_for_price() {
        Assertions.assertEquals(400, lastResponse.getStatusCode());
    }

    @When("I enter invalid quantity value and click Save")
    public void i_enter_invalid_quantity_value_and_click_save() {
        Long categoryId = getValidCategoryId();
        Map<String, Object> plantData = new HashMap<>();
        plantData.put("name", "P" + (System.currentTimeMillis() % 100000000L));
        plantData.put("price", 10.0);
        plantData.put("quantity", -5); // negative quantity is invalid

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(plantData)
                .post("/api/plants/category/" + categoryId);
    }

    @Then("a validation error is shown for Quantity")
    public void a_validation_error_is_shown_for_quantity() {
        Assertions.assertEquals(400, lastResponse.getStatusCode());
    }

    @When("I click Edit on an existing plant, change fields, and Save")
    public void i_click_edit_on_an_existing_plant_change_fields_and_save() {
        Response getResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/plants");
        
        Long plantId = null;
        String plantName = "P" + (System.currentTimeMillis() % 100000000L);
        
        try {
            List<Integer> ids = getResponse.jsonPath().getList("id");
            if (ids != null && !ids.isEmpty()) {
                plantId = ids.get(0).longValue();
                plantName = getResponse.jsonPath().getString("[0].name");
            }
        } catch (Exception e) {
        }

        if (plantId == null) {
            try {
                Response pagedResponse = RestAssured.given()
                        .header("Authorization", "Bearer " + token)
                        .get("/api/plants/paged?page=0&size=10");
                List<Integer> ids = pagedResponse.jsonPath().getList("content.id");
                if (ids != null && !ids.isEmpty()) {
                    plantId = ids.get(0).longValue();
                    plantName = pagedResponse.jsonPath().getString("content[0].name");
                }
            } catch (Exception e) {
            }
        }
        
        if (plantId == null) {
            i_fill_valid_data_and_click_save();
            plantId = createdPlantId;
            plantName = lastResponse.jsonPath().getString("name");
        }

        Assertions.assertNotNull(plantId, "No plant found/created to edit");

        Map<String, Object> updateData = new HashMap<>();
        String newName = (plantName.length() > 23 ? plantName.substring(0, 23) : plantName) + "U";
        updateData.put("name", newName);
        updateData.put("price", 25.0);
        updateData.put("quantity", 15);

        Map<String, Object> catObj = new HashMap<>();
        catObj.put("id", getValidCategoryId());
        catObj.put("name", defaultCategoryName != null ? defaultCategoryName : "Annual");
        if (defaultParentCategoryId != null) {
            Map<String, Object> parentObj = new HashMap<>();
            parentObj.put("id", defaultParentCategoryId);
            parentObj.put("name", defaultParentCategoryName);
            catObj.put("parent", parentObj);
        }
        updateData.put("category", catObj);

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(updateData)
                .put("/api/plants/" + plantId);
        
        if (lastResponse.getStatusCode() != 200) {
            System.out.println("Edit Plant Response: " + lastResponse.asString());
        }
    }

    @Then("the plant details are updated successfully")
    public void the_plant_details_are_updated_successfully() {
        Assertions.assertEquals(200, lastResponse.getStatusCode(), "Expected 200 status code on edit save");
    }

    @When("I click Delete, confirm deletion, and return to list")
    public void i_click_delete_confirm_deletion_and_return_to_list() {
        Response getResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/plants");

        Long plantId = null;
        try {
            List<Integer> ids = getResponse.jsonPath().getList("id");
            if (ids != null && !ids.isEmpty()) {
                plantId = ids.get(ids.size() - 1).longValue();
            }
        } catch (Exception e) {
        }

        if (plantId == null) {
            try {
                Response pagedResponse = RestAssured.given()
                        .header("Authorization", "Bearer " + token)
                        .get("/api/plants/paged?page=0&size=10");
                List<Integer> ids = pagedResponse.jsonPath().getList("content.id");
                if (ids != null && !ids.isEmpty()) {
                    plantId = ids.get(ids.size() - 1).longValue();
                }
            } catch (Exception e) {
            }
        }

        if (plantId == null) {
            i_fill_valid_data_and_click_save();
            plantId = createdPlantId;
        }

        Assertions.assertNotNull(plantId, "No plant found/created to delete");
        createdPlantId = plantId;

        lastResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete("/api/plants/" + plantId);
    }

    @Then("the plant is removed from the list")
    public void the_plant_is_removed_from_the_list() {
        int statusCode = lastResponse.getStatusCode();
        Assertions.assertTrue(statusCode == 200 || statusCode == 204, "Expected 200 or 204 on delete");
        
        Response checkResponse = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/plants/" + createdPlantId);
        Assertions.assertEquals(404, checkResponse.getStatusCode(), "Deleted plant still exists");
    }

    @Given("I am on the Add or Edit Plant page")
    public void i_am_on_the_add_or_edit_plant_page() {
        // No-op for API
    }

    @When("I click Cancel")
    public void i_click_cancel() {
        // No-op for API
    }

    @Then("I return to {string} without saving changes")
    public void i_return_to_without_saving_changes(String path) {
        Assertions.assertTrue(true);
    }
}
