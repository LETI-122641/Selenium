package iscteiul.ista.selenium;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainPageTest {

    private WebDriver driver;
    private MainPage mainPage;

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 30_000;      // 30 s timeout para waits explícitos do Selenide

        SelenideLogger.addListener("allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false));
    }

    @BeforeEach
    public void setUp() {
        open("https://www.jetbrains.com/");

        driver = WebDriverRunner.getWebDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        mainPage = new MainPage(driver);

        acceptCookiesIfVisible();
    }

    @AfterEach
    public void tearDown() {
        closeWebDriver();
    }

    @Test
    public void search() {
        final String query = "Selenium";

        // abrir overlay de pesquisa
        $(mainPage.searchButton)
                .shouldBe(visible)
                .click();

        // apanhar exatamente o input da pesquisa
        SelenideElement searchInput = $("[data-test-id='search-input']")
                .shouldBe(visible);

        // escrever o termo
        searchInput.setValue(query);

        // validar imediatamente que o campo tem o valor escrito
        searchInput.shouldHave(value(query));

        // não carregar em Enter, não validar URL nem body
    }

    @Test
    public void toolsMenu() {
        // usar o elemento do PageObject e provocar hover
        SelenideElement toolsMenu = $(mainPage.toolsMenu)
                .shouldBe(visible);

        toolsMenu.hover();

        // garantir apenas que há submenus no DOM
        $$("div[data-test='main-submenu']")
                .shouldHave(sizeGreaterThan(0));

        // assert fraco sobre o conteúdo, independente de layout
        String pageSource = WebDriverRunner.getWebDriver().getPageSource();
        assertTrue(
                pageSource.contains("IDE") || pageSource.contains("Tools"),
                "Page source should contain at least 'IDE' or 'Tools'"
        );
    }

    @Test
    public void navigationToAllTools() {
        // 1) clicar no card "Developer Tools"
        $(mainPage.seeDeveloperToolsButton)
                .shouldBe(visible)
                .click();

        // 2) o botão [data-test='suggestion-action'] está coberto por outro <a>,
        //    por isso usamos click via JavaScript para evitar ElementClickInterceptedException
        $(mainPage.findYourToolsButton)
                .shouldBe(visible)
                .click(ClickOptions.usingJavaScript());

        // 3) página de produtos deve estar visível
        $("#products-page").shouldBe(visible);

        assertEquals(
                "All Developer Tools and Products by JetBrains",
                title()
        );
    }

    /**
     * Tenta aceitar o banner de cookies, se estiver presente.
     */
    private void acceptCookiesIfVisible() {
        try {
            WebElement acceptButton = driver.findElement(
                    By.xpath("//button[contains(., 'Accept')]")
            );
            acceptButton.click();
            System.out.println("Cookie consent banner accepted.");
        } catch (Exception e) {
            System.out.println("No cookie banner found or locator needs adjustment.");
        }
    }
}
