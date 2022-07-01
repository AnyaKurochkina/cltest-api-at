package ui.uiExtesions;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import ru.testit.junit5.RunningHandler;
import ui.Utils;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static core.helper.Configure.isIntegrationTestIt;

public class ConfigExtension implements AfterEachCallback, BeforeAllCallback, TestWatcher {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        closeWebDriver();
    }

    public void testFailed(final ExtensionContext context, Throwable e) {
        if (e.getMessage().contains("Screenshot: file:/"))
            return;
        Utils.attachFiles();
    }
}
