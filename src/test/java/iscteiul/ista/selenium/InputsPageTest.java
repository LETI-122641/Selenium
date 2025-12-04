package iscteiul.ista.selenium;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.value;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputsPageTest {

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10_000;
    }

    @Test
    void shouldAcceptNumericInputAndIncrement() {
        InputsPage page = new InputsPage()
                .openPage()
                .setValue("10");

        // valida valor escrito
        page.getNumberInput().shouldHave(value("10"));

        // incrementa uma vez com seta ↑
        page.incrementOnce();

        // confirma que passou para 11
        assertEquals("11", page.getValue());
    }

    @Test
    void shouldIgnoreNonNumericInput() {
        InputsPage page = new InputsPage()
                .openPage()
                .setValue("abc");

        String current = page.getValue();
        assertEquals(false, "abc".equals(current));
    }
}
