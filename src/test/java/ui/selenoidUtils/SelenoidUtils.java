package ui.selenoidUtils;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.InputStream;
import java.net.URL;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static core.helper.Configure.getAppProp;

@Log4j2
public class SelenoidUtils {

    private static boolean isRemote;

    @SneakyThrows
    public static void isRemote() {
        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote"))) {
            isRemote = true;
            log.info("Ui Тесты стартовали на selenoid сервере: " + getAppProp("webdriver.remote.url"));
            Configuration.remote = getAppProp("webdriver.remote.url");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("enableVideo", false);
            capabilities.setCapability("enableLog", true);
            capabilities.setVersion("91.0");
            capabilities.setCapability("logName", "lastSelenoid.log");
            Configuration.browserCapabilities = capabilities;
        } else {
            isRemote = false;
            log.info("Ui Тесты стартовали локально");
        }
    }
}
