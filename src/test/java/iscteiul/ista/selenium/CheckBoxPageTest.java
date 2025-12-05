package iscteiul.ista.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckBoxPageTest {
    private WebDriver driver;
    private WebElement cb1;
    private WebElement cb2;
    private CheckBoxPage page;

    @BeforeEach
    void setUp() {
        driver = Mockito.mock(WebDriver.class);

        cb1 = Mockito.mock(WebElement.class);
        cb2 = Mockito.mock(WebElement.class);

        when(driver.findElements(By.cssSelector("#checkboxes input[type='checkbox']")))
                .thenReturn(List.of(cb1, cb2));

        page = new CheckBoxPage(driver);
    }

    @Test
    void testSelectFirstCheckbox_WhenNotSelected_ClicksIt() {
        when(cb1.isSelected()).thenReturn(false);

        page.selectFirstCheckbox();

        verify(cb1, times(1)).click();
    }

    @Test
    void testSelectFirstCheckbox_WhenAlreadySelected_DoesNotClick() {
        when(cb1.isSelected()).thenReturn(true);

        page.selectFirstCheckbox();

        verify(cb1, never()).click();
    }

    @Test
    void testIsFirstCheckboxSelected() {
        when(cb1.isSelected()).thenReturn(true);

        assertTrue(page.isFirstCheckboxSelected());
    }

    @Test
    void testIsSecondCheckboxSelected() {
        when(cb2.isSelected()).thenReturn(false);

        assertFalse(page.isSecondCheckboxSelected());
    }
}