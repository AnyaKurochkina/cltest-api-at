package core;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Objects;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class WebElementActions {

    public void clickJS(WebElement webElement) {
        log.info("Клик на элемент с помощью JS скрипта: " + webElement);
        clickJS(getWebDriver(), webElement);
    }

    public void clickJS(WebDriver webDriver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].click();", element);
    }

    public void setAttributeJS(WebElement webElement, String attribute, String value) {
        log.info("Изменение атрибута с помощью JS скрипта: " + webElement);
        setAttributeJS(getWebDriver(), webElement, attribute, value);
    }

    public void setAttributeJS(WebDriver webDriver, WebElement element, String attribute, String value) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        Objects.requireNonNull(element.getAttribute(attribute), "У элемента " + element + " нет атрибута: " + attribute);
        js.executeScript("arguments[0].setAttribute('" + attribute + "','" + value + "')", element);
    }
}
