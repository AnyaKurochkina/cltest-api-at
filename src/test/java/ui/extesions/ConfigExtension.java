package ui.extesions;

import com.codeborne.selenide.Configuration;
import core.utils.DownloadingFilesUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static core.helper.AttachUtils.UImodifyThrowable;
import static org.junit.TestsExecutionListener.initDriver;

public class ConfigExtension implements AfterEachCallback, BeforeEachCallback, BeforeAllCallback, InvocationInterceptor {

    static {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        Configuration.browserCapabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        initDriver();
    }

    @Override
    @SneakyThrows
    public void afterEach(ExtensionContext extensionContext) {
        FileUtils.cleanDirectory(new File(DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH));
        closeWebDriver();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        initDriver();
    }

    @SneakyThrows
    public void interceptTestMethod(final InvocationInterceptor.Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        try {
            invocation.proceed();
        } catch (Throwable e) {
            throw UImodifyThrowable(e);
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
