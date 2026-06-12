package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    private final String BASE_URL = "http://localhost:8080";

    // Locators
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginBtn = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(BASE_URL + "/ui/login");
    }

    public void loginAs(String username, String password) {
        open();
        try {
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
            WebElement passwordField = driver.findElement(passwordInput);
            WebElement loginButton = driver.findElement(loginBtn);

            usernameField.sendKeys(username);
            passwordField.sendKeys(password);
            loginButton.click();
            
            // Wait for dashboard to load after login
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dashboard-card")));
        } catch (Exception e) {
            System.out.println("Login page might be structured differently or already logged in: " + e.getMessage());
        }
    }
}
