package core.helper;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.LogType;
import lombok.SneakyThrows;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import ru.testit.utils.StepNode;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.StringJoiner;

import static core.helper.Configure.getAppProp;

public class AttachUtils {

    public static void attachRequests() {
        LogEntries logEntries = WebDriverRunner.getWebDriver().manage().logs().get(String.valueOf(LogType.BROWSER));
        StringJoiner str = new StringJoiner("\n");
        for (LogEntry entry : logEntries)
            str.add(String.format("[%s] %s: %s", entry.getLevel(), entry.getTimestamp(), entry.getMessage()));
        if (str.length() > 0) {
            Allure.addAttachment("badRequests", "text/html", str.toString(), ".log");
        }
    }

    public static Throwable UImodifyThrowable(Throwable throwable) {
        try {
            attachRequests();
            String videoUrl = getVideoUrl();
            if (throwable.getMessage().contains("Screenshot:")) {
                Allure.addAttachment("screenPage", "image/png",
                        new ByteArrayInputStream(((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)), ".png");
                Allure.addAttachment("screenPage", "text/html", WebDriverRunner.getWebDriver().getPageSource(), ".html");
                return throwable;
            }
            String videoRecordingTitle = "Video recording";
            StringBuilder newMessage = new StringBuilder();
            final String message = throwable.getMessage();
            String[] messageParts = splitMessage(message);

            newMessage.append(messageParts[0]).append("\nAttach files:");
            addScreenAttachments(newMessage);

            if (!videoUrl.isEmpty()) {
                newMessage.append("\n").append(videoRecordingTitle).append(": ").append(videoUrl);
            }
            newMessage.append("\n").append("Link: ").append(WebDriverRunner.getWebDriver().getCurrentUrl());
            newMessage.append(messageParts[1]);
            setThrowableDetailMessage(throwable, newMessage.toString());
            return throwable;
        } catch (Throwable e) {
            throwable.addSuppressed(e);
            return throwable;
        }
    }

    private static String getVideoUrl() {
        String videoUrl = "";
        if (Boolean.parseBoolean(getAppProp("webdriver.capabilities.enableVideo", "false"))) {
            String host = getAppProp("webdriver.remote.url");
            videoUrl = String.format("%s/video/%s.mp4", host.substring(0, host.length() - 7), getSessionId());
            Allure.issue("Video recording", videoUrl);
            StepNode stepNode = StepsAspects.getCurrentStep().get();
            if (stepNode != null) {
                stepNode.addLinkItem(new LinkItem("Video recording", videoUrl, "", LinkType.RELATED));
            }
        }
        return videoUrl;
    }

    private static String getSessionId() {
        return ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
    }

    public static String getUrlToDownloadedFileFromSelenoid(String fileName) {
        String host = getAppProp("webdriver.remote.url").substring(0, getAppProp("webdriver.remote.url").length() - 7);
        return String.format("%s/download/%s/%s", host, getSessionId(), fileName);
    }

    private static String[] splitMessage(String message) {
        String[] parts = message.split("==>");
        String partFirst = parts[0];
        String partSecond = (parts.length > 1) ? parts[1] : "";
        return new String[]{partFirst, partSecond};
    }

    @SneakyThrows
    private static void addScreenAttachments(StringBuilder newMessage) {
        Driver driver = WebDriverRunner.driver();
        Config config = driver.config();
        Screenshot screenshot = ScreenShotLaboratory.getInstance().takeScreenshot(driver, config.screenshots(), config.savePageSource());

        if (screenshot.getImage() != null) {
            Allure.addAttachment("screenPage", "image/png", new URL(screenshot.getImage()).openStream(), ".png");
        }

        if (screenshot.getSource() != null) {
            Allure.addAttachment("screenPage", "text/html", new URL(screenshot.getSource()).openStream(), ".html");
        }
        newMessage.append(screenshot.summary());
    }

    private static void setThrowableDetailMessage(Throwable throwable, String newMessage) throws NoSuchFieldException, IllegalAccessException {
        Field detailMessageField = Throwable.class.getDeclaredField("detailMessage");
        detailMessageField.setAccessible(true);
        detailMessageField.set(throwable, newMessage);
    }
}
