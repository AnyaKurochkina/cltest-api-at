package ui.uiExtesions;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ConfigExtension implements AfterEachCallback, BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        closeWebDriver();
    }

}
