import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckSalesSorting {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Chrome...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("http://localhost:8080/ui/login");
            System.out.println("Logging in...");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username"))).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("admin123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.urlContains("/dashboard"));
            System.out.println("Logged in.");

            driver.get("http://localhost:8080/ui/sales");
            wait.until(ExpectedConditions.urlContains("/sales"));
            System.out.println("On sales page.");

            WebElement qtyHeader = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//th[contains(., 'Quantity')]")));
            qtyHeader.click();
            Thread.sleep(1000);
            System.out.println("URL after clicking Quantity: " + driver.getCurrentUrl());
            
            WebElement soldAtHeader = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//th[contains(., 'Sold At')]")));
            soldAtHeader.click();
            Thread.sleep(1000);
            System.out.println("URL after clicking Sold At: " + driver.getCurrentUrl());

            WebElement sellBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sell Plant")));
            sellBtn.click();
            Thread.sleep(1000);
            System.out.println("URL after clicking Sell Plant: " + driver.getCurrentUrl());
            
            WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("quantity")));
            qty.clear();
            qty.sendKeys("0");
            
            WebElement submitBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sell') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'save') or @type='submit' or contains(@class, 'btn-primary')]")));
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();
            Thread.sleep(1000);
            System.out.println("URL after submitting quantity 0: " + driver.getCurrentUrl());
            
            try {
                WebElement invalidFeedback = driver.findElement(By.cssSelector(".invalid-feedback, .alert"));
                System.out.println("Validation message: " + invalidFeedback.getText());
            } catch (Exception e) {
                System.out.println("No validation message found.");
            }

        } finally {
            driver.quit();
        }
    }
}
