package iscteiul.ista.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class DropDownPage {

    private final WebDriver driver;
    private final By dropdown = By.id("dropdown");

    public DropDownPage(WebDriver driver) {
        this.driver = driver;
    }

    public void selectByVisibleText(String text) {
        Select select = new Select(driver.findElement(dropdown));
        select.selectByVisibleText(text);
    }

    public String getSelectedOption() {
        Select select = new Select(driver.findElement(dropdown));
        return select.getFirstSelectedOption().getText();
    }
}
