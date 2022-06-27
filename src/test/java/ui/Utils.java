package ui;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Utils {
    @Attachment(type = "image/png", fileExtension = ".png")
    public static byte[] AttachScreen() {
        return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(type = "text/html", fileExtension = ".html")
    public static String AttachPage() {
        return WebDriverRunner.getWebDriver().getPageSource();
    }

    public static void attachFiles() {
        AttachScreen();
        AttachPage();
    }
}
