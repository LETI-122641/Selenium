package iscteiul.ista.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DynamicLoadingPage {

    private final WebDriver driver;

    // Link para o Example 1
    public final By example1Link =
            By.linkText("Example 1: Element on page that is hidden");

    // Botão "Start"
    public final By startButton =
            By.cssSelector("#start button");

    // Elemento que aparece depois do loading
    public final By finishText =
            By.id("finish");

    public DynamicLoadingPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }
}
