package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object for /ui/sales/new (Sell Plant form).
 * Covers SRS §7.2: plant selection, quantity input, validation errors, error-on-same-page.
 */
public class SellPlantPage extends BasePage {

    // sale-form.html field identifiers
    private static final By PLANT_DROPDOWN       = By.id("plantId");
    private static final By QUANTITY_FIELD       = By.name("quantity");

    // sale-form.html has one submit button: <button class="btn btn-primary">Sell</button>
    private static final By SELL_BUTTON          = By.xpath("//button[normalize-space(text())='Sell']");
    private static final By CANCEL_BUTTON        = By.xpath("//*[contains(text(),'Cancel')]");

    // Validation: @NotNull on plantId renders into <div class="text-danger" th:errors="*{plantId}">
    private static final By PLANT_VALIDATION_ERR = By.cssSelector("div.text-danger");

    // Service errors (e.g. insufficient stock) are placed in model as "errorMessage"
    // and rendered in <div class="alert alert-danger alert-dismissible ...">
    private static final By ERROR_ALERT          = By.cssSelector(".alert-danger");

    public SellPlantPage(WebDriver driver) {
        super(driver);
    }

    // ---- Plant dropdown ----

    /** Select the plant at the given index (index 0 is the placeholder "-- Select Plant --"). */
    public void selectPlantByIndex(int index) {
        new Select(wait.until(ExpectedConditions.presenceOfElementLocated(PLANT_DROPDOWN)))
                .selectByIndex(index);
    }

    /** Reset the dropdown to the empty placeholder so @NotNull validation fires on submit. */
    public void clearPlantSelection() {
        new Select(driver.findElement(PLANT_DROPDOWN)).selectByValue("");
    }

    /**
     * Reads the currently selected option text ("Rose (Stock: 42)") and returns
     * the stock number, or 1_000_000 if the text can't be parsed.
     */
    public long getSelectedPlantStock() {
        String text = new Select(driver.findElement(PLANT_DROPDOWN))
                .getFirstSelectedOption().getText();
        Matcher m = Pattern.compile("Stock:\\s*(\\d+)").matcher(text);
        return m.find() ? Long.parseLong(m.group(1)) : 1_000_000L;
    }

    // ---- Quantity field ----

    public void enterQuantity(int qty) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(QUANTITY_FIELD));
        el.clear();
        el.sendKeys(String.valueOf(qty));
    }

    // ---- Form submission ----

    public void clickSell() {
        wait.until(ExpectedConditions.elementToBeClickable(SELL_BUTTON)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(CANCEL_BUTTON)).click();
    }

    // ---- Validation / error queries ----

    /**
     * Returns the text inside the Thymeleaf {@code th:errors="*{plantId}"} div.
     * The @NotNull message on SaleCreateDTO is "Plant is required".
     */
    public String getPlantValidationErrorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PLANT_VALIDATION_ERR))
                   .getText();
    }

    /**
     * Returns true when a .alert-danger div is visible on the form.
     * This is how the controller surfaces service-level errors (e.g. insufficient stock).
     */
    public boolean isErrorAlertDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_ALERT))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * HTML5 quantity validation (min="1"): checks for .invalid-feedback/.alert or the
     * browser-native validationMessage on the number input.
     */
    public boolean isQuantityValidationTriggered() {
        try {
            WebElement feedback = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".invalid-feedback, .alert")));
            return feedback.isDisplayed();
        } catch (Exception e) {
            String nativeMsg = driver.findElement(QUANTITY_FIELD).getAttribute("validationMessage");
            return nativeMsg != null && !nativeMsg.isEmpty();
        }
    }

    /**
     * Returns true when the Plant dropdown is present in the DOM — i.e. we are still
     * on the Sell Plant form rather than redirected to the Sales list.
     */
    public boolean isOnSellForm() {
        return !driver.findElements(PLANT_DROPDOWN).isEmpty();
    }
}
