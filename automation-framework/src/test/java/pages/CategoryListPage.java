package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CategoryListPage extends BasePage {

    private final String BASE_URL = "http://localhost:8080";

    // Locators
    private final By addCategoryBtn = By.xpath("//a[contains(text(), 'Add A Category')]");
    private final By editBtn = By.xpath("//a[@title='Edit']");
    private final By table = By.tagName("table");
    private final By tableHeader = By.tagName("thead");

    public CategoryListPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(BASE_URL + "/ui/categories");
    }

    public void clickEditExistingCategory() {
        wait.until(ExpectedConditions.presenceOfElementLocated(editBtn)).click();
    }

    public boolean isAddCategoryButtonVisible() {
        wait.until(ExpectedConditions.presenceOfElementLocated(table));
        List<WebElement> btns = driver.findElements(addCategoryBtn);
        return !btns.isEmpty();
    }

    public String getTableHeadersText() {
        wait.until(ExpectedConditions.presenceOfElementLocated(table));
        return driver.findElement(tableHeader).getText();
    }
}
