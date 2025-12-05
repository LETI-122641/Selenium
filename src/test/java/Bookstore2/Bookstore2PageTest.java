package Bookstore2;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Bookstore2PageTest {

    private Bookstore2Page page;

    @BeforeAll
    static void setUpAll() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10000; // 10s
        // se quiseres ver o browser aberto no fim:
        // Configuration.holdBrowserOpen = true;
        // se quiseres em modo headless:
        // Configuration.headless = true;
    }

    @BeforeEach
    public void setUp() {
        page = new Bookstore2Page();
    }

    @AfterEach
    public void tearDown() {
        closeWebDriver();
    }

    @Test
    void login_NewCategory() {
        page.openPage()
                .loginAsAdmin()
                .clickAdminButton()
                .goToAdminPage()
                .addNewCategory("Banana");
    }
}
