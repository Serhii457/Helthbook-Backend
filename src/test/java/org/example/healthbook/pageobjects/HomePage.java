package org.example.healthbook.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private WebDriver driver;

    @FindBy(id = "navbarNav")
    private WebElement navbar;

    @FindBy(xpath = "//button[contains(text(),'Запис на прийом')]")
    private WebElement appointmentButton;

    @FindBy(xpath = "//a[contains(text(),'Вхід')]")
    private WebElement loginLink;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void open(String url) {
        driver.get(url);
    }

    public boolean isNavbarDisplayed() {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(navbar))
                .isDisplayed();
    }

    public void clickAppointmentButton() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(appointmentButton));
        appointmentButton.click();
    }

    public void clickLoginLink() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(loginLink));
        loginLink.click();
    }
}