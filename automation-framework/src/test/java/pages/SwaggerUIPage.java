package pages;

import org.openqa.selenium.WebDriver;

public class SwaggerUIPage extends BasePage {

    private final String BASE_URL = "http://localhost:8080";

    public SwaggerUIPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(BASE_URL + "/swagger-ui.html");
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
