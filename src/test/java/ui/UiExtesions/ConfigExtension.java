package ui.UiExtesions;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import core.helper.Configure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ConfigExtension implements BeforeEachCallback, AfterEachCallback {
    private static final String URL = Configure.getAppProp("base.url");
    @Override
    public void beforeEach(ExtensionContext extensionContext) {
//        baseUrl = "http://10.89.10.10:5432/management";
        baseUrl = URL;
        Configuration.browserSize = "1530x870";
        Configuration.browserPosition = "2x2";
        Configuration.timeout = 15000;
        Configuration.driverManagerEnabled = false;
//        System.setProperty("webdriver.chrome.driver", "");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        closeWebDriver();
    }
}
