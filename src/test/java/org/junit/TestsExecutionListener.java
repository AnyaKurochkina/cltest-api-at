package org.junit;


import api.routes.Api;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.http.Path;
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
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.codeborne.selenide.Configuration.baseUrl;
import static core.helper.Configure.*;

@Log4j2
public class TestsExecutionListener implements TestExecutionListener {
    private static final String URL = getAppProp("base.url");
    public static final String responseTimeLog = "logs/ResponseTime.log";

    public void testPlanExecutionStarted(TestPlan testPlan) {
    }

    @SneakyThrows
    public static void initDriver() {
        //###Config for Ui###
        if (getAppProp("webdriver.path") != null) {
            String DRIVER_PATH = new File(getAppProp("webdriver.path")).getAbsolutePath();
            System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
        }

        baseUrl = URL;
        isRemote();
        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true")))
            Configuration.startMaximized = true;
        else
            Configuration.startMaximized = Boolean.parseBoolean(Configure.getAppProp("webdriver.maximized", "false"));
        Configuration.pageLoadTimeout = 60000;
        Configuration.timeout = 50000;
        Configuration.driverManagerEnabled = false;
        Configuration.browser = "chrome";
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.content_settings.exceptions.clipboard", getClipBoardSettingsMap());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-web-security");
        options.addArguments("--allow-external-pages");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-site-isolation-trials");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--start-maximized");
        options.addExtensions(new File("src/test/resources/requestLogger.crx"));
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

    public static void loadSecretJson() {
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
//        if (Configure.isIntegrationTestIt())
//            RunningHandler.finishLaunch();
//        ObjectPoolService.saveEntities(Configure.getAppProp("data.folder") + "/shareFolder/logData.json");
//        new File(Configure.getAppProp("allure.results")).mkdir();
//        FileWriter fooWriter = new FileWriter(Configure.getAppProp("allure.results") + "environment.properties", false);
//        fooWriter.write("ENV=" + ENV);
//        fooWriter.close();
//        System.out.println("##teamcity[publishArtifacts 'logs => logs']");
//        System.out.println("##teamcity[publishArtifacts 'target/swagger-coverage-output => swagger-coverage-output.zip']");
    }

    @SneakyThrows
    public static void initApiRoutes(){
        List<Class<? extends Api>> classes = getSubclasses(Api.class);
        for (Class<? extends Api> clazz : classes) {
            Api api = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Api.Route.class)) {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        Api.Route route = field.getAnnotation(Api.Route.class);
                        field.setAccessible(true);
                        try {
                            field.set(null, new Path(route.method(), route.path(), route.status(), api.url()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static List<Class<? extends Api>> getSubclasses(Class<? extends Api> superClass) throws ClassNotFoundException {
        List<Class<? extends Api>> classes = new ArrayList<>();
        String packageName = superClass.getPackage().getName();
        String path = packageName.replace('.', '/');
        java.net.URL url = ClassLoader.getSystemClassLoader().getResource(path);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if(file.isDirectory())
                continue;
            String className = file.getName().substring(0, file.getName().length() - 6);
            Class<?> clazz = Class.forName(packageName + "." + className);
            if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
                classes.add((Class<? extends Api>) clazz);
            }
        }
        return classes;
    }


}
