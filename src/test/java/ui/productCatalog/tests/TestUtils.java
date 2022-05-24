package ui.productCatalog.tests;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;

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
}
