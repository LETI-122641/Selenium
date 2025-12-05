package Add_Product;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.AddProductPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class AddProductTest {

    private static final String DEFAULT_PRICE = "12.34";
    private static final String DEFAULT_STOCK = "10";
    private static final String DEFAULT_AVAILABILITY = "Available";
    private static final String DEFAULT_CATEGORY = "Romance";

    private AddProductPage page;

    @BeforeAll
    static void globalSetup() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10_000; // 10s
        // se quiseres ver o browser aberto no fim:
        Configuration.holdBrowserOpen = true;
        // se quiseres em modo headless:
        // Configuration.headless = true;
    }

    @BeforeEach
    void setUp() {
        page = new AddProductPage();
    }

    @Test
    void login_criarNovoProduto() {
        String productName = "Livro de Teste - " + System.currentTimeMillis();

        page.openPage()
                .loginAsAdmin()
                .openAddProductForm()
                .fillNewProductForm(
                        productName,
                        DEFAULT_PRICE,
                        DEFAULT_STOCK,
                        DEFAULT_AVAILABILITY,
                        DEFAULT_CATEGORY
                );
    }

    @AfterEach
    void tearDown() {
        // Se estiveres a debugar, comenta esta linha para o browser não fechar
        closeWebDriver();
    }
}
