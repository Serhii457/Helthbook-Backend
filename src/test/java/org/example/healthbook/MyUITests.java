package org.example.healthbook;

import org.example.healthbook.pageobjects.HomePage;
import org.example.healthbook.pageobjects.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class MyUITests extends BaseTest {

    private HomePage home;
    private LoginPage login;

    @BeforeClass
    public void initPages() {
        home = new HomePage(driver);
        login = new LoginPage(driver);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @Test
    public void testNavbarIsVisible() {
        home.open(BASE_URL);
        Assert.assertTrue(home.isNavbarDisplayed(), " Навбар має відображатися");
    }

    @Test
    public void testAppointmentButtonClick() {
        home.open(BASE_URL);
        home.clickAppointmentButton();
        Assert.assertTrue(driver.getPageSource().contains("Запис"), " Форма запису не відкрилась!");
    }

    @Test
    public void testLoginNavigation() {
        home.open(BASE_URL);
        home.clickLoginLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), " Не перейшли на сторінку логіну");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        login.open(BASE_URL + "login");
        login.enterUsername("wrongUser");
        login.enterPassword("wrongPass");
        login.clickLogin();

        String errorText = driver.findElement(By.cssSelector("div.alert.alert-danger")).getText();
        Assert.assertTrue(errorText.contains("Невірний логін або пароль"),
                "Має бути повідомлення про невірні дані");
    }

    @Test
    public void testLoginWithValidCredentials() {
        LoginPage login = new LoginPage(driver);

        login.open(BASE_URL + "login");
        login.enterUsername("admin");
        login.enterPassword("admin123");
        login.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement adminPanelButton = wait.until(d -> d.findElement(By.linkText("Адмін панель")));

        Assert.assertTrue(adminPanelButton.isDisplayed(), "Після входу в Navbar має з'явитись кнопка 'Адмін панель'");
    }
}
