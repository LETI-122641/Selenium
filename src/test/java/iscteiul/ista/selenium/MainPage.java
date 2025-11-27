package iscteiul.ista.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// page_url = https://www.jetbrains.com/
public class MainPage {

    // Card "Developer Tools" na homepage
    @FindBy(xpath = "//*[@data-test-marker='Developer Tools']")
    public WebElement seeDeveloperToolsButton;

    // Botão / link "Find your tools"
    @FindBy(css = "[data-test='suggestion-link']")
    public WebElement findYourToolsButton;

    // Item de menu "Developer Tools" na barra superior
    @FindBy(xpath = "//div[@data-test='main-menu-item' and @data-test-marker='Developer Tools']")
    public WebElement toolsMenu;

    // Botão de pesquisa no header
    @FindBy(css = "[data-test='site-header-search-action']")
    public WebElement searchButton;

    public MainPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}
