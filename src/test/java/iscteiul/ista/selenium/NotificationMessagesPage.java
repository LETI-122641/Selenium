package iscteiul.ista.selenium;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class NotificationMessagesPage {

    private final SelenideElement clickHereLink = $("#content a");
    private final SelenideElement flash = $("#flash");
    private final SelenideElement closeButton = $("#flash .close");

    public NotificationMessagesPage openPage() {
        open("https://the-internet.herokuapp.com/notification_message_rendered");
        return this;
    }

    public NotificationMessagesPage triggerNotification() {
        clickHereLink.shouldBe(visible).click();
        return this;
    }

    public SelenideElement notification() {
        return flash;
    }

    public NotificationMessagesPage closeNotification() {
        closeButton.shouldBe(visible).click();
        return this;
    }
}
