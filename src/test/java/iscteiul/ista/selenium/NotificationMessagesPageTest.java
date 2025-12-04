package iscteiul.ista.selenium;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationMessagesPageTest {

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10_000;
    }

    @Test
    void shouldShowAllowedNotificationMessages() {
        NotificationMessagesPage page = new NotificationMessagesPage()
                .openPage()
                .triggerNotification();

        // a mensagem aparece
        page.notification().shouldBe(visible);

        String msg = page.notification().getText();

        // o site alterna aleatoriamente entre 2-3 mensagens válidas
        assertTrue(
                msg.contains("Action successful") ||
                        msg.contains("Action unsuccesful, please try again") ||
                        msg.contains("Action unsuccessful, please try again")
        );
    }

    @Test
    void shouldCloseNotification() {
        NotificationMessagesPage page = new NotificationMessagesPage()
                .openPage()
                .triggerNotification();

        page.notification().shouldBe(visible);
        page.closeNotification();

        // depois de clicar no X, a flash deve desaparecer
        page.notification().should(disappear);
    }
}
