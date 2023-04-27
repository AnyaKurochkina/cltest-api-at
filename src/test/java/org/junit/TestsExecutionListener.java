package org.junit;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.utils.Encrypt;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.ObjectPoolService;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.testit.junit5.RunningHandler;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Configuration.baseUrl;
import static core.helper.Configure.*;

@Log4j2
public class TestsExecutionListener implements TestExecutionListener {
    private static final String URL = getAppProp("base.url");
    public static final String responseTimeLog = "logs/ResponseTime.log";

    @SneakyThrows
    public void testPlanExecutionStarted(TestPlan testPlan) {
        //####Config for Ui###
        Files.deleteIfExists(Paths.get(responseTimeLog));

        String fileSecret = Configure.getAppProp("data.folder") + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json";
        if (Files.exists(Paths.get(fileSecret)))
            ObjectPoolService.loadEntities(DataFileHelper.read(fileSecret));
        loadSecretJson();
    }

    @SneakyThrows
    public static void initDriver() {
        //###Config for Ui###
        if (getAppProp("webdriver.path") != null) {
            String DRIVER_PATH = new File(getAppProp("webdriver.path")).getAbsolutePath();
            System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
            System.setProperty("chromeoptions.args", "\"--disable-notifications\",\"--disable-web-security\",\"--allow-external-pages\",\"--disable-gpu\",\"--no-sandbox\",\"--disable-browser-side-navigation\"");
        }

        baseUrl = URL;
        isRemote();
        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true")))
            Configuration.startMaximized = true;
        else
            Configuration.startMaximized = Boolean.parseBoolean(Configure.getAppProp("webdriver.maximized", "false"));
        Configuration.pageLoadTimeout = 60000;
        Configuration.timeout = 45000;
        Configuration.driverManagerEnabled = false;
        Configuration.browser = "chrome";
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.content_settings.exceptions.clipboard", getClipBoardSettingsMap());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-external-pages");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-site-isolation-trials");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("prefs", prefs);

        synchronized (TestsExecutionListener.class) {
            Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }

        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true"))) {
            RemoteWebDriver driver = new RemoteWebDriver(new java.net.URL(Configuration.remote), Configuration.browserCapabilities);
            driver.setFileDetector(new LocalFileDetector());
            WebDriverRunner.setWebDriver(driver);
        }
    }

    private static Map<String, Object> getClipBoardSettingsMap() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("last_modified", String.valueOf(System.currentTimeMillis()));
        map.put("setting", 1);
        Map<String, Object> cbPreference = new HashMap<>();
        cbPreference.put("[*.],*", map);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValueAsString(cbPreference);
        return cbPreference;
    }

    public void loadSecretJson() {
        String secret = System.getProperty("secret");
        if (secret == null)
            secret = Configure.getAppProp("secret");
        String file = Configure.getAppProp("data.folder") + "/shareFolder/" + "secret.bin";
        if (!Files.exists(Paths.get(file)) || secret == null)
            return;
        ObjectPoolService.loadEntities(Encrypt.Aes256Decode(Base64.getDecoder().decode(DataFileHelper.read(file)), secret));
    }

    public static void isRemote() {
        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true"))) {
            Assertions.assertNotNull(getAppProp("webdriver.remote.url"), "Не указан webdriver.remote.url");
            log.info("Ui Тесты стартовали на selenoid сервере");
            Configuration.remote = getAppProp("webdriver.remote.url");
            Map<String, String> capabilitiesProp = getAppPropStartWidth("webdriver.capabilities.");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilitiesProp.forEach((k, v) -> {
                String prop = k.replaceAll("webdriver.capabilities.", "");
                if (v.equals("true") || v.equals("false"))
                    capabilities.setCapability(prop, Boolean.parseBoolean(v));
                else
                    capabilities.setCapability(prop, v);
            });
            Configuration.browserCapabilities = capabilities;
        } else {
            log.info("Ui Тесты стартовали локально");
        }
    }

    @SneakyThrows
    public void testPlanExecutionFinished(TestPlan testPlan) {
        if (Configure.isIntegrationTestIt())
            RunningHandler.finishLaunch();
        ObjectPoolService.saveEntities(Configure.getAppProp("data.folder") + "/shareFolder/logData.json");
        new File(Configure.getAppProp("allure.results")).mkdir();
        FileWriter fooWriter = new FileWriter(Configure.getAppProp("allure.results") + "environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
        System.out.println("##teamcity[publishArtifacts 'logs => logs']");
        System.out.println("##teamcity[publishArtifacts 'target/swagger-coverage-output => swagger-coverage-output.zip']");
    }
}
