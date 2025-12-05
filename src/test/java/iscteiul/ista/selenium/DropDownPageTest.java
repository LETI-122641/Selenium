package iscteiul.ista.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DropDownPageTest {

    private WebDriver driver;
    private WebElement dropdownElement;
    private DropDownPage page;

    @BeforeEach
    void setUp() {
        driver = mock(WebDriver.class);
        dropdownElement = mock(WebElement.class);

        // Sempre que o código chamar driver.findElement(By.id("dropdown")),
        // vai devolver este mock de WebElement
        when(driver.findElement(By.id("dropdown"))).thenReturn(dropdownElement);

        page = new DropDownPage(driver);
    }

    @Test
    void testSelectByVisibleText_ChamaSelectComTextoCorreto() {
        String expectedText = "Option 1";

        // Intercetar a criação de new Select(...)
        try (MockedConstruction<Select> mocked =
                     mockConstruction(Select.class)) {

            page.selectByVisibleText(expectedText);

            // Obter a instância de Select que foi criada dentro do método
            Select selectMock = mocked.constructed().get(0);

            // Verificar que selectByVisibleText foi chamado com o texto certo
            verify(selectMock, times(1)).selectByVisibleText(expectedText);

            // Também podemos garantir que o driver procurou o elemento correto
            verify(driver, times(1)).findElement(By.id("dropdown"));
        }
    }

    @Test
    void testGetSelectedOption_DevolveTextoDaOpcaoSelecionada() {
        String expectedText = "Option 2";

        try (MockedConstruction<Select> mocked =
                     mockConstruction(Select.class, (selectMock, context) -> {
                         // Mock da opção selecionada
                         WebElement optionMock = mock(WebElement.class);
                         when(optionMock.getText()).thenReturn(expectedText);
                         when(selectMock.getFirstSelectedOption()).thenReturn(optionMock);
                     })) {

            String selected = page.getSelectedOption();

            // Verificar o valor retornado
            assertEquals(expectedText, selected);

            // Obter a instância de Select que foi criada
            Select selectMock = mocked.constructed().get(0);
            verify(selectMock, times(1)).getFirstSelectedOption();

            verify(driver, times(1)).findElement(By.id("dropdown"));
        }
    }
}