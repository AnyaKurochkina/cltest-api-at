package ui.uiExtesions;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.selenoidUtils.SelenoidUtils;

import java.io.File;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static core.helper.Configure.getAppProp;

import static ui.selenoidUtils.SelenoidUtils.isRemote;

public class ConfigExtension implements  BeforeEachCallback, AfterEachCallback {

    private static final String DRIVER_PATH = new File(getAppProp("driver.path")).getAbsolutePath();
    private static final String URL = getAppProp("base.url");

    @SneakyThrows
    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
        isRemote();
        baseUrl = URL;
        Configuration.browserSize = "1530x870";
        Configuration.browserPosition = "2x2";
        Configuration.timeout = 15000;
        Configuration.driverManagerEnabled = false;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        SelenoidUtils selenoidUtils = new SelenoidUtils();
        String sessionId = selenoidUtils.getSessionId();
        closeWebDriver();
        selenoidUtils.isVideo(sessionId);
    }
}
