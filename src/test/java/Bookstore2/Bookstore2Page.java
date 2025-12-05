package Bookstore2;

import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

// page_url = https://vaadin-bookstore-example.demo.vaadin.com/
public class Bookstore2Page {

    private static final String URL = "https://vaadin-bookstore-example.demo.vaadin.com/";
    private static final long TIMEOUT_MS = 10000; // 10s

    static {
        Configuration.timeout = TIMEOUT_MS;
    }

    @Step("Abrir a aplicação Bookstore")
    public Bookstore2Page openPage() {
        open(URL);
        return this;
    }

    /**
     * Login como admin.
     * Usa primeiro seletores “normais” e, se não der, faz fallback em JS.
     */
    @Step("Fazer login como admin")
    public Bookstore2Page loginAsAdmin() {
        // tenta inputs normais
        SelenideElement username = findVisible(
                "#username",
                "input[name='username']",
                "input[id*='user']",
                "input[placeholder*='User']",
                "input[type='text']"
        );

        SelenideElement password = findVisible(
                "#password",
                "input[name='password']",
                "input[id*='pass']",
                "input[placeholder*='Pass']",
                "input[type='password']"
        );

        if (username != null && password != null) {
            username.click();
            username.setValue("admin");

            password.click();
            password.setValue("admin");

            SelenideElement submit = findVisible(
                    "button[type='submit']",
                    "button:has-text('Log in')",
                    "button:has-text('Login')",
                    "vaadin-button[title='Log in']",
                    "vaadin-button"
            );

            if (submit != null) {
                submit.click();
            } else {
                password.pressEnter();
            }

            sleep(1500);
            return this;
        }

        // fallback em JS
        boolean jsResult = (Boolean) executeJavaScript(
                "(() => {\n" +
                        "  const selectorsU = ['#username','input[name=\"username\"]','input[id*=user]','input[placeholder*=\"User\"]','input[type=text]'];\n" +
                        "  const selectorsP = ['#password','input[name=\"password\"]','input[id*=pass]','input[placeholder*=\"Pass\"]','input[type=password]'];\n" +
                        "  function findFirst(selectors){ for(const s of selectors){ const e = document.querySelector(s); if(e) return e; } return null; }\n" +
                        "  const u = findFirst(selectorsU);\n" +
                        "  const p = findFirst(selectorsP);\n" +
                        "  if(!u || !p) return false;\n" +
                        "  u.focus(); u.value = 'admin'; u.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                        "  p.focus(); p.value = 'admin'; p.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                        "  const submit = document.querySelector('button[type=submit], button[title=\"Log in\"]');\n" +
                        "  if(submit){ submit.click(); return true; }\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter'}));\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keypress',{key:'Enter'}));\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keyup',{key:'Enter'}));\n" +
                        "  return true;\n" +
                        "})();"
        );

        if (!jsResult) {
            WebDriver drv = WebDriverRunner.getWebDriver();
            throw new IllegalStateException("Não foi possível fazer login. URL atual: " + drv.getCurrentUrl());
        }

        sleep(1500);
        return this;
    }

    @Step("Clicar no menu lateral Admin")
    public Bookstore2Page clickAdminButton() {
        // procura qualquer elemento de menu cujo texto visível seja exatamente "Admin"
        SelenideElement admin = $x(
                "//*[normalize-space()='Admin' and " +
                        " (self::a or self::span or self::div or self::vaadin-tab or self::vaadin-item)]"
        );

        admin.shouldBe(visible).click();  // Selenide espera até ficar visível
        return this;
    }

    @Step("Ir para a página Admin")
    public Bookstore2Page goToAdminPage() {
        // Clica no menu lateral "Admin"
        SelenideElement adminMenu = $x(
                "//aside//*[normalize-space()='Admin' or normalize-space()=' Admin '][1]"
        );
        if (!adminMenu.exists()) {
            adminMenu = $x("//*[normalize-space()='Admin'][1]");
        }
        adminMenu.shouldBe(visible).click();

        // Aguarda o conteúdo da página Admin
        // Opção 1: título "Hello Admin"
        $x("//h2[normalize-space()='Hello Admin']").shouldBe(visible);

        // (Opcional) validar também o botão "Add New Category"
        $x("//*[normalize-space()='Add New Category']").shouldBe(visible);

        return this;
    }

    @Step("Adicionar nova categoria: {categoryName}")
    public Bookstore2Page addNewCategory(String categoryName) {

        // 1) Clica no botão "Add New Category"
        $x("//*[normalize-space()='Add New Category']")
                .shouldBe(visible)
                .click();

        // 2) Dá um micro tempo para o campo ganhar foco
        sleep(300);

        // 3) Escreve no elemento que está com foco e dá ENTER
        WebDriver driver = WebDriverRunner.getWebDriver();
        WebElement focused = driver.switchTo().activeElement();

        actions()
                .click(focused)              // garante que estamos mesmo nesse campo
                .sendKeys(categoryName)      // escreve o texto (ex: "Banana")
                .sendKeys(Keys.ENTER)        // confirma
                .perform();

        return this;
    }

    private SelenideElement findVisible(String... selectors) {
        for (String sel : selectors) {
            try {
                SelenideElement el = $(sel);
                if (el.exists() && el.is(visible)) return el;
            } catch (Exception ignored) {
                // se o seletor for inválido (:has-text em versões antigas, etc) ignoramos
            }
        }
        return null;
    }
}
