package ui.productCatalog.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ui.productCatalog.pages.LoginPage;

abstract public class BaseTest {
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        Configuration.browser = "chrome";
        Configuration.driverManagerEnabled = true;
        Configuration.browserSize = "1366x868";
        Configuration.headless = false;
        Configuration.timeout = 10000;
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
        loginPage.login("portal_admin", "portal_admin");
    }
}
