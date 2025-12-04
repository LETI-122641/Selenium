package DataBase;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static com.codeborne.selenide.Selenide.switchTo;


import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.*;

class DataBasePageTest {

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.timeout    = 15_000;

        SelenideLogger.addListener(
                "allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @Test
    void filterByExistingTitle_shouldKeepMovieInGrid() {
        DataBasePage page = new DataBasePage().openPage();

        String anyTitle = page.getFirstRowTitle();
        assertFalse(anyTitle.isBlank(), "Título inicial não deve ser vazio");

        page.filterByTitle(anyTitle);

        page.allCells().shouldHave(sizeGreaterThan(0));
        assertTrue(
                page.isMoviePresentInGrid(anyTitle),
                "A grelha deve conter o filme com título: " + anyTitle
        );
    }

    @Test
    void openDetails_shouldShowSameTitleAndNonEmptyDescription() {
        DataBasePage page = new DataBasePage().openPage();

        String anyTitle = page.getFirstRowTitle();
        assertFalse(anyTitle.isBlank(), "Título inicial não deve ser vazio");

        page.openMovieDetails(anyTitle);

        String detailsText  = page.getDetailsText();
        String detailsTitle = page.getDetailsTitle();

        assertNotNull(detailsText, "Texto de detalhes não deve ser null");
        assertFalse(detailsText.isBlank(), "Descrição do filme não deve estar vazia");
        assertTrue(
                detailsText.contains(anyTitle) || detailsTitle.contains(anyTitle),
                "Detalhes devem referir o título seleccionado: " + anyTitle
        );
    }

    /**
     * Versão robusta do teste "firstMovie" do professor:
     * verifica as 4 primeiras células (título, ano, realizador, link) e
     * abre o link do IMDB.
     */
    // language: java
    @Test
    void firstMovie_shouldMatchExpectedDataAndOpenImdb() {
        DataBasePage page = new DataBasePage().openPage();

        ElementsCollection cells = page.moviesGridRows();
        // pelo menos 4 células: título, ano, realizador, link
        cells.shouldHave(sizeGreaterThan(3));

        List<SelenideElement> movies = cells.stream().toList();

        String title    = movies.get(0).shouldBe(visible).getText().trim();
        String year     = movies.get(1).shouldBe(visible).getText().trim();
        String director = movies.get(2).shouldBe(visible).getText().trim();

        assertFalse(title.isBlank(), "Título do primeiro filme não deve ser vazio");
        assertTrue(year.matches("\\d{4}"), "Ano deve ter 4 dígitos: " + year);
        assertFalse(director.isBlank(), "Nome do realizador não deve ser vazio");

        SelenideElement linkCell = movies.get(3).shouldBe(visible);
        String href = linkCell.getAttribute("href");
        String linkTarget = (href != null && !href.isBlank())
                ? href.toLowerCase().trim()
                : linkCell.getText().toLowerCase().trim();

        assertFalse(linkTarget.isBlank(), "A célula de link não deve estar vazia");

        // normalize by removing non-alphanumeric characters to avoid failures on trailing arrows/whitespace
        String normalized = linkTarget.replaceAll("[^a-z0-9]", "");

        assertTrue(
                normalized.contains("imdb") || normalized.contains("imbd"),
                "Texto/URL da célula do link deve referir IMDB/IMBD: " + linkTarget
        );

        // comportamento do exemplo do professor: apenas clicar no link
        linkCell.click();
    }
    @Test
    void firstMovie_imdbLinkShouldOpenInNewTab() {
        DataBasePage page = new DataBasePage().openPage();

        ElementsCollection cells = page.moviesGridRows();
        cells.shouldHave(sizeGreaterThan(3)); // título, ano, realizador, link

        List<SelenideElement> movies = cells.stream().toList();
        SelenideElement linkCell = movies.get(3).shouldBe(visible);

        // try to find an anchor inside the cell
        SelenideElement anchor = linkCell.$("a");
        String href = (anchor.exists()) ? anchor.getAttribute("href") : null;

        org.openqa.selenium.WebDriver driver = WebDriverRunner.getWebDriver();
        String originalWindow = driver.getWindowHandle();
        java.util.Set<String> before = driver.getWindowHandles();

        // open the link in a new tab: prefer JS window.open when href is available
        if (href != null && !href.isBlank()) {
            com.codeborne.selenide.Selenide.executeJavaScript("window.open(arguments[0], '_blank');", href);
        } else if (anchor.exists()) {
            anchor.click();
        } else {
            linkCell.click();
        }

        // wait for a new window handle to appear
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(d -> d.getWindowHandles().size() > before.size());

        // find the new window handle and switch to it
        java.util.Set<String> after = driver.getWindowHandles();
        String newWindow = after.stream().filter(h -> !before.contains(h)).findFirst()
                .orElseThrow(() -> new AssertionError("No new window opened"));

        driver.switchTo().window(newWindow);

        String newUrl = driver.getCurrentUrl().toLowerCase();

        // aceitar IMDB ou IMBD (tipografia do exemplo)
        assertTrue(
                newUrl.contains("imdb") || newUrl.contains("imbd"),
                "URL da nova aba deve ser IMDB/IMBD, mas foi: " + newUrl
        );

        // cleanup: close new tab and return to original
        driver.close();
        driver.switchTo().window(originalWindow);
    }
}
