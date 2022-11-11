package ui.extesions;

import com.codeborne.selenide.Configuration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import core.helper.AttachUtils;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ConfigExtension implements AfterEachCallback, BeforeAllCallback, InvocationInterceptor {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
//        SelenideLogger.addListener("AllureSelenide",
//                new AllureSelenide().screenshots(false).savePageSource(false));
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
            try {
                AttachUtils.attachRequests();
                AttachUtils.attachFiles();
            } catch (Throwable ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }

}
