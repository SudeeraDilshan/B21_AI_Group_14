package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class DashboardPage extends BasePage {

    // Locators
    private final By dashboardCard = By.className("dashboard-card");
    private final By bodyTag = By.tagName("body");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void waitForCardsToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(dashboardCard));
    }

    public String getPageText() {
        return driver.findElement(bodyTag).getText();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void hoverOverFirstCard() {
        try {
            List<WebElement> cards = driver.findElements(dashboardCard);
            if (!cards.isEmpty()) {
                Actions actions = new Actions(driver);
                actions.moveToElement(cards.get(0)).perform();
            }
        } catch (Exception e) {
            System.out.println("Hover failed: " + e.getMessage());
        }
    }

    public void clickButtonByText(String buttonText) {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), '" + buttonText + "')]")));
            btn.click();
        } catch (Exception e) {
            System.out.println("Could not click button " + buttonText + " : " + e.getMessage());
        }
    }

    public boolean isCreateSaleButtonPresent() {
        List<WebElement> createBtns = driver.findElements(By.xpath("//*[contains(text(), 'Create Sale')]"));
        return !createBtns.isEmpty();
    }

    public boolean isLinkDisabled(String linkText) {
        try {
            WebElement link = driver.findElement(By.xpath("//*[contains(text(), '" + linkText + "')]"));
            String classAttr = link.getAttribute("class");
            return classAttr != null && classAttr.contains("disabled");
        } catch (Exception e) {
            System.out.println("Inventory link not found directly: " + e.getMessage());
            return false;
        }
    }

    public boolean isTooltipPresent(String tooltip) {
        try {
            WebElement link = driver.findElement(By.xpath("//*[contains(@title, '" + tooltip + "')]"));
            return link != null;
        } catch (Exception e) {
            System.out.println("Tooltip element not found: " + e.getMessage());
            return false;
        }
    }
}
