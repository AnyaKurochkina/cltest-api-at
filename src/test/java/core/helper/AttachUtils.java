package core.helper;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.selenide.LogType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import ru.testit.utils.StepNode;

import java.util.Objects;
import java.util.StringJoiner;

import static core.helper.Configure.getAppProp;

public class AttachUtils {
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
        if (str.length() > 0) {
            Allure.addAttachment("badRequests", "text/html", str.toString(), ".log");
        }
    }

    public static void attachLinkVideo() {
        if (Boolean.parseBoolean(getAppProp("webdriver.capabilities.enableVideo", "false"))) {
            String sessionId = ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
            String host = getAppProp("webdriver.remote.url");
            String title = "Video recording";
            String url = String.format("%s/video/%s.mp4", host.substring(0, host.length() - 7), sessionId);
            Allure.issue(title, url);
            StepNode stepNode = StepsAspects.getCurrentStep().get();
            if (Objects.nonNull(stepNode))
                StepsAspects.getCurrentStep().get().addLinkItem(new LinkItem(title, url, "", LinkType.RELATED));
        }
    }

    public static void attachFiles() {
        screenPage();
        sourcePage();
    }
}
