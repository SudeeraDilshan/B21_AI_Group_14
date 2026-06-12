package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CategoryFormPage extends BasePage {

    private final String BASE_URL = "http://localhost:8080";

    // Locators
    private final By nameInput = By.name("name");
    private final By saveBtn = By.cssSelector("button[type='submit']");
    private final By cancelBtn = By.xpath("//a[contains(text(), 'Cancel')]");
    private final By successAlert = By.className("alert-success");
    private final By validationError = By.className("invalid-feedback");

    public CategoryFormPage(WebDriver driver) {
        super(driver);
    }

    public void openAddPage() {
        driver.get(BASE_URL + "/ui/categories/add");
    }

    public void openEditPage(int id) {
        driver.get(BASE_URL + "/ui/categories/edit/" + id);
    }

    public void enterName(String name) {
        WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(nameInput));
        nameField.clear();
        if (name != null && !name.isEmpty()) {
            nameField.sendKeys(name);
        }
    }

    public void clearName() {
        enterName("");
    }

    public void clickSave() {
        driver.findElement(saveBtn).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
    }

    public boolean hasSuccessMessage() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(successAlert));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasValidationErrors() {
        try {
            return wait.until(d -> d.findElements(validationError).stream().anyMatch(e -> e.getText().length() > 0));
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
