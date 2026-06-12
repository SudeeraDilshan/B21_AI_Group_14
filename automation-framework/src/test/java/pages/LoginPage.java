package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By USERNAME_FIELD  = By.name("username");
    private static final By PASSWORD_FIELD  = By.name("password");
    private static final By SUBMIT_BUTTON   = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void loginAs(String username, String password) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD)).sendKeys(username);
        driver.findElement(PASSWORD_FIELD).sendKeys(password);
        driver.findElement(SUBMIT_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }
}
