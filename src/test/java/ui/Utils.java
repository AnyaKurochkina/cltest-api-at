package ui;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.selenide.LogType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

import java.util.StringJoiner;

public class Utils {
    @Attachment(type = "image/png", fileExtension = ".png")
    public static byte[] screenPage() {
        return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(type = "text/html", fileExtension = ".html")
    public static String sourcePage() {
        return WebDriverRunner.getWebDriver().getPageSource();
    }

    public static void attachRequests() {
        LogEntries logEntries = WebDriverRunner.getWebDriver().manage().logs().get(String.valueOf(LogType.BROWSER));
        StringJoiner str = new StringJoiner("\n");
        for (LogEntry entry : logEntries)
            str.add(String.format("[%s] %s: %s", entry.getLevel(), entry.getTimestamp(), entry.getMessage()));
        if(str.length() > 0){
            Allure.addAttachment("badRequests", "text/html", str.toString(), ".log");
        }
    }

    public static void attachFiles() {
        screenPage();
        sourcePage();
    }
}
