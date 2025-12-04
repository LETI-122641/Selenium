package iscteiul.ista.selenium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingTest {

    private WebDriver driver;
    private DynamicLoadingPage dynamicLoadingPage;

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10_000;

        SelenideLogger.addListener("allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false));
    }

    @BeforeEach
    void setUp() {
        // página principal do exemplo de conteúdo dinâmico
        open("https://the-internet.herokuapp.com/dynamic_loading");

        driver = WebDriverRunner.getWebDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        dynamicLoadingPage = new DynamicLoadingPage(driver);
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @Test
    void dynamicLoadingExample1_shouldShowHelloWorld() {
        // 1) abrir o Example 1
        $(dynamicLoadingPage.example1Link)
                .shouldBe(visible)
                .click();

        // 2) clicar no botão Start
        $(dynamicLoadingPage.startButton)
                .shouldBe(visible)
                .click();

        // 3) esperar que o texto final apareça e validar
        SelenideElement finish = $(dynamicLoadingPage.finishText)
                .shouldBe(visible)
                .shouldHave(text("Hello World!"));

        assertEquals("Hello World!", finish.getText());
    }
}
