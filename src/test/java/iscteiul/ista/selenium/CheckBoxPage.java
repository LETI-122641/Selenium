package iscteiul.ista.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckBoxPage {

    private final WebDriver driver;
    private final By checkboxes = By.cssSelector("#checkboxes input[type='checkbox']");

    public CheckBoxPage(WebDriver driver) {
        this.driver = driver;
    }

    private List<WebElement> allCheckboxes() {
        return driver.findElements(checkboxes);
    }

    public void selectFirstCheckbox() {
        WebElement cb = allCheckboxes().get(0);
        if (!cb.isSelected()) {
            cb.click();
        }
    }

    public boolean isFirstCheckboxSelected() {
        return allCheckboxes().get(0).isSelected();
    }

    public boolean isSecondCheckboxSelected() {
        return allCheckboxes().get(1).isSelected();
    }
}
