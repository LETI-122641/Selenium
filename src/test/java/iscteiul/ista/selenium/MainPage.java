package iscteiul.ista.selenium;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.$;

// page_url = https://www.jetbrains.com/
public class MainPage {

    private final WebDriver driver;

    // Locators públicos para serem usados nos testes com $(...)
    public final By seeDeveloperToolsButton =
            By.cssSelector("[data-test-marker='Developer Tools']");

    public final By findYourToolsButton =
            By.cssSelector("[data-test='suggestion-action']");

    public final By toolsMenu =
            By.cssSelector("[data-test='main-menu-item'][data-test-marker='Developer Tools']");
      
    public final By searchButton =
            By.cssSelector("[data-test='site-header-search-action']");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    // Métodos de conveniência (se quiseres usar Page Object também em outros testes)

    public SelenideElement seeDeveloperToolsButton() {
        return $(seeDeveloperToolsButton);
    }

    public SelenideElement findYourToolsButton() {
        return $(findYourToolsButton);
    }

    public SelenideElement toolsMenu() {
        return $(toolsMenu);
    }

    public SelenideElement searchButton() {
        return $(searchButton);
    }
}
