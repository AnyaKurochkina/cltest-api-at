package ui.productCatalog.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import ui.productCatalog.pages.LoginPage;

import java.util.concurrent.TimeUnit;

import static core.helper.Configure.getAppProp;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract public class BaseTest {

    private static final String login = getAppProp("user.login");
    private static final String password = getAppProp("user.password");

    public void setUp() {
        WebDriverManager.chromedriver().setup();
        Configuration.browser = "chrome";
        Configuration.driverManagerEnabled = true;
        Configuration.browserSize = "1366x968";
        Configuration.headless = false;
        Configuration.timeout = 20000;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @BeforeEach
    public void init() {
        setUp();
        login();
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(login, password);
    }
}
