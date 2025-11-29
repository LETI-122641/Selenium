package iscteiul.ista.selenium;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class InputsPage {

    private final SelenideElement numberInput = $("input");

    public InputsPage openPage() {
        open("https://the-internet.herokuapp.com/inputs");
        return this;
    }

    public InputsPage setValue(String value) {
        numberInput.shouldBe(visible).setValue(value);
        return this;
    }

    public String getValue() {
        return numberInput.getValue();
    }

    public InputsPage incrementOnce() {
        numberInput.sendKeys(Keys.ARROW_UP);
        return this;
    }

    public InputsPage decrementOnce() {
        numberInput.sendKeys(Keys.ARROW_DOWN);
        return this;
    }

    // ← novo getter para o teste
    public SelenideElement getNumberInput() {
        return numberInput;
    }
}
