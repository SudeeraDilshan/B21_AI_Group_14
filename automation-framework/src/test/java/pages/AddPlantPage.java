package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AddPlantPage extends BasePage {

    private final By nameInputLocator = By.name("name");
    private final By categorySelectLocator = By.name("categoryId");
    private final By priceInputLocator = By.name("price");
    private final By quantityInputLocator = By.name("quantity");

    public AddPlantPage(WebDriver driver) {
        super(driver);
    }

    public void waitForFormToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(nameInputLocator));
    }

    public boolean isNameFieldPresent() {
        return !driver.findElements(nameInputLocator).isEmpty();
    }

    public boolean isCategoryFieldPresent() {
        return !driver.findElements(categorySelectLocator).isEmpty();
    }

    public boolean isPriceFieldPresent() {
        return !driver.findElements(priceInputLocator).isEmpty();
    }

    public boolean isQuantityFieldPresent() {
        return !driver.findElements(quantityInputLocator).isEmpty();
    }
}
