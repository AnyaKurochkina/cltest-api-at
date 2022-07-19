package ui.uiExtesions;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import ui.Utils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Level;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ConfigExtension implements AfterEachCallback, BeforeAllCallback, InvocationInterceptor {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        Configuration.browserCapabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        closeWebDriver();
    }

    @SneakyThrows
    public void interceptTestMethod(final InvocationInterceptor.Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        try {
            invocation.proceed();
        } catch (Throwable e) {
            Utils.attachRequests();
            if (Objects.nonNull(e.getMessage()))
                if (!e.getMessage().contains("Screenshot: file:/"))
                    Utils.attachFiles();
            throw e;
        }
    }

}
