package org.example.healthbook;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected final String BASE_URL = "http://localhost:5173/";


    @BeforeClass
    @Parameters("browser")
    public void setupClass(@Optional("chrome") String browser) {
        if (browser.toLowerCase().equals("chrome")) {
            driver = WebDriverManager.chromedriver().create();
        } else if (browser.toLowerCase().equals("firefox")) {
            driver = WebDriverManager.firefoxdriver().create();
        } else if (browser.toLowerCase().equals("edge")) {
            driver = WebDriverManager.edgedriver().create();
        } else if (browser.toLowerCase().equals("opera")) {
            driver = WebDriverManager.operadriver().create();
        } else if (browser.toLowerCase().equals("safari")) {
            driver = WebDriverManager.safaridriver().create();
        } else if (browser.toLowerCase().equals("ie")) {
            driver = WebDriverManager.iedriver().create();
        } else if (browser.toLowerCase().equals("chromium")) {
            driver = WebDriverManager.chromiumdriver().create();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }
}
