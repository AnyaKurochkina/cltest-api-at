package ui.extesions;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.AttachUtils;
import core.helper.http.Http;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static core.helper.Configure.getAppProp;
import static org.junit.TestsExecutionListener.initDriver;

public class ConfigExtension implements AfterEachCallback, BeforeEachCallback, BeforeAllCallback, InvocationInterceptor {
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

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        initDriver();
    }

    public static Throwable attachFiles(Throwable throwable) {
        try {
            AttachUtils.attachLinkVideo();
            AttachUtils.attachRequests();
            AttachUtils.attachFiles();
        } catch (Throwable ex) {
            throwable.addSuppressed(ex);
        }
        return throwable;
    }

    @SneakyThrows
    public void interceptTestMethod(final InvocationInterceptor.Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        try {
            invocation.proceed();
        } catch (Throwable e) {
            throw attachFiles(e);
        }
//        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true"))) {
//            if(Boolean.parseBoolean(getAppProp("webdriver.capabilities.enableVideo", "false"))) {
//                String sessionId = ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
//                String host = getAppProp("webdriver.remote.url");
//                new Http(host.substring(0, host.length()-7))
//                        .setWithoutToken()
//                        .delete("/video/{}.mp4", sessionId);
//            }
//        }
    }

}
