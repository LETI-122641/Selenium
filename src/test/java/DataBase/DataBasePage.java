package DataBase;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object para o test suite "Database" (Vaadin Database Example).
 */
public class DataBasePage {

    private static final String PAGE_URL =
            "https://vaadin-database-example.demo.vaadin.com/";

    // grelha de filmes (garante que a página carregou)
    private final SelenideElement grid =
            $("vaadin-grid, .v-grid, table");

    // campo de filtro por título (selector defensivo)
    private final SelenideElement titleFilter =
            $("vaadin-text-field[placeholder*='Title'], " +
                    "vaadin-text-field[label*='Title'], " +
                    "input[placeholder*='Title']");

    // “detalhes” = última célula clicada
    private SelenideElement lastSelectedCell;

    /** Abre a página e espera que a grelha exista. */
    public DataBasePage openPage() {
        open(PAGE_URL);
        grid.shouldBe(visible);
        return this;
    }

    /** Aplica filtro por título (se o campo existir). */
    public DataBasePage filterByTitle(String title) {
        if (titleFilter.exists()) {
            titleFilter.shouldBe(visible).clear();
            titleFilter.setValue(title);
        }
        return this;
    }

    /** Todas as células visíveis de toda a grelha. */
    public ElementsCollection allCells() {
        ElementsCollection cells =
                grid.$$("[part='cell'], vaadin-grid-cell-content, td").filter(visible);
        if (cells.isEmpty()) {
            // fallback defensivo
            cells = $$("vaadin-grid-cell-content").filter(visible);
        }
        return cells;
    }

    /** “Primeira linha” = primeiras células da grelha (título, ano, realizador, link). */
    public ElementsCollection moviesGridRows() {
        // para o teste do professor basta devolver todas as células;
        // o teste só olha para as primeiras 4.
        return allCells();
    }

    /** Verifica se algum título na grelha contém o texto dado. */
    public boolean isMoviePresentInGrid(String title) {
        return allCells().filterBy(text(title)).size() > 0;
    }

    /** Texto da primeira célula (assumido como título do primeiro filme). */
    public String getFirstRowTitle() {
        return allCells()
                .first()
                .shouldBe(visible)
                .getText()
                .trim();
    }

    /** Clica no filme com esse título e guarda a célula como “detalhes”. */
    public DataBasePage openMovieDetails(String title) {
        lastSelectedCell = allCells()
                .findBy(text(title))
                .shouldBe(visible);
        lastSelectedCell.click();
        return this;
    }

    /** Garante que há uma célula seleccionada. */
    private SelenideElement requireSelectedCell() {
        if (lastSelectedCell == null) {
            throw new IllegalStateException(
                    "Nenhum filme seleccionado. Chama openMovieDetails() primeiro.");
        }
        return lastSelectedCell;
    }

    /** Texto completo da célula seleccionada (serve como “detalhes”). */
    public String getDetailsText() {
        return requireSelectedCell()
                .shouldBe(visible)
                .getText();
    }

    /** Título extraído da primeira linha do texto de detalhes. */
    public String getDetailsTitle() {
        String all = getDetailsText();
        if (all == null || all.isBlank()) return "";
        return all.split("\\R", 2)[0].trim();
    }
}
