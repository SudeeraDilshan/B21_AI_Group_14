package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for /ui/sales (Sales list page).
 * Covers SRS §7.1: list, sort, pagination, empty state.
 */
public class SalesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- Navigation ---
    private static final By SELL_PLANT_BUTTON  = By.linkText("Sell Plant");
    private static final By SALES_NAV_LINK     = By.linkText("Sales");

    // --- Sort column headers (each is an <a> inside a <th> in sales.html) ---
    private static final By PLANT_NAME_HEADER  = By.xpath("//th//a[normalize-space(text())='Plant']");
    private static final By QUANTITY_HEADER    = By.xpath("//th//a[normalize-space(text())='Quantity']");
    private static final By TOTAL_PRICE_HEADER = By.xpath("//th//a[normalize-space(text())='Total Price']");
    private static final By SOLD_AT_HEADER     = By.xpath("//th//a[normalize-space(text())='Sold At']");

    // --- Table / state ---
    private static final By TABLE              = By.tagName("table");
    private static final By TABLE_ROWS         = By.cssSelector("table tbody tr");
    private static final By PAGINATION         = By.cssSelector("nav .pagination");

    public SalesPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ---- Navigation actions ----

    public void clickSellPlant() {
        wait.until(ExpectedConditions.elementToBeClickable(SELL_PLANT_BUTTON)).click();
        wait.until(ExpectedConditions.urlContains("/sales/new"));
    }

    public void clickSalesInNav() {
        wait.until(ExpectedConditions.elementToBeClickable(SALES_NAV_LINK)).click();
        wait.until(ExpectedConditions.urlContains("/sales"));
    }

    // ---- Sort header clicks ----

    public void clickPlantNameHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(PLANT_NAME_HEADER)).click();
        wait.until(ExpectedConditions.urlContains("sortField=plant.name"));
    }

    public void clickQuantityHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(QUANTITY_HEADER)).click();
    }

    public void clickTotalPriceHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(TOTAL_PRICE_HEADER)).click();
    }

    public void clickSoldAtHeader() {
        wait.until(ExpectedConditions.elementToBeClickable(SOLD_AT_HEADER)).click();
    }

    // ---- State queries ----

    public boolean isTableVisible() {
        return !driver.findElements(TABLE).isEmpty();
    }

    public boolean isPaginationDisplayed() {
        return !driver.findElements(PAGINATION).isEmpty();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Returns plant names (first <td> of each data row) in the order they appear
     * in the table. Used by the Plant-name sort assertion.
     */
    public List<String> getPlantNamesFromTable() {
        List<String> names = new ArrayList<>();
        for (WebElement row : driver.findElements(TABLE_ROWS)) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() > 1) { // skip the single-cell "No sales found" row
                names.add(cells.get(0).getText().trim().toLowerCase());
            }
        }
        return names;
    }
}
