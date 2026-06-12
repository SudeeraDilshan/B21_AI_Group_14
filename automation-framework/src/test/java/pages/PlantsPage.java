package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class PlantsPage extends BasePage {

    private final String PATH = "/ui/plants";

    // Locators
    private final By tableLocator = By.tagName("table");
    private final By theadLocator = By.tagName("thead");
    private final By tbodyLocator = By.tagName("tbody");
    private final By searchNameLocator = By.name("name");
    private final By categorySelectLocator = By.name("categoryId");
    private final By searchButtonLocator = By.xpath("//button[contains(text(), 'Search')]");
    private final By resetButtonLocator = By.xpath("//a[contains(text(), 'Reset')]");
    private final By nameHeaderLocator = By.xpath("//th/a[contains(text(), 'Name')]");
    private final By priceHeaderLocator = By.xpath("//th/a[contains(text(), 'Price')]");
    private final By stockHeaderLocator = By.xpath("//th/a[contains(text(), 'Stock')]");
    private final By nextButtonLocator = By.xpath("//a[contains(text(), 'Next')]");
    private final By previousButtonLocator = By.xpath("//a[contains(text(), 'Previous')]");
    private final By lowStockBadgeLocator = By.xpath("//span[contains(@class, 'bg-danger') and contains(text(), 'Low')]");

    public PlantsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForTableToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(tableLocator));
    }

    public String getHeaderText() {
        return driver.findElement(theadLocator).getText();
    }

    public String getBodyText() {
        return driver.findElement(tbodyLocator).getText();
    }

    public void enterSearchName(String name) {
        wait.until(ExpectedConditions.presenceOfElementLocated(searchNameLocator)).sendKeys(name);
    }

    public void clickSearch() {
        driver.findElement(searchButtonLocator).click();
    }

    public void selectCategoryByIndex(int index) {
        WebElement selectElem = wait.until(ExpectedConditions.presenceOfElementLocated(categorySelectLocator));
        Select categorySelect = new Select(selectElem);
        if (categorySelect.getOptions().size() > index) {
            categorySelect.selectByIndex(index);
        }
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButtonLocator)).click();
    }

    public void clickNameHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(nameHeaderLocator)).click();
    }

    public void clickPriceHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(priceHeaderLocator)).click();
    }

    public void clickStockHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(stockHeaderLocator)).click();
    }

    public void clickNext() {
        driver.findElement(nextButtonLocator).click();
    }

    public void clickPrevious() {
        driver.findElement(previousButtonLocator).click();
    }

    public List<WebElement> getLowStockBadges() {
        return driver.findElements(lowStockBadgeLocator);
    }

    public boolean isTablePresent() {
        return !driver.findElements(tableLocator).isEmpty();
    }
}
