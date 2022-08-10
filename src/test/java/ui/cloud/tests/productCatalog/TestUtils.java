package ui.cloud.tests.productCatalog;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static void wait(int ms)
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void scrollToTheBottom() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void scrollToTheTop() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
    }

    public static void scroll(int pixels) {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, "+pixels+")");
    }
}
