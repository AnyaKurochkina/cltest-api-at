package org.junit;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.utils.Encrypt;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import models.ObjectPoolService;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import ru.testit.junit5.RunningHandler;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static com.codeborne.selenide.Configuration.baseUrl;
import static core.helper.Configure.ENV;
import static core.helper.Configure.getAppProp;
import static ui.selenoidUtils.SelenoidUtils.isRemote;

//@Log4j2
public class TestsExecutionListener implements TestExecutionListener {
    private static final String URL = getAppProp("base.url");

    @SneakyThrows
    public void testPlanExecutionStarted(TestPlan testPlan) {
        //###Config for Ui###
        if(getAppProp("driver.path") != null) {
            String DRIVER_PATH = new File(getAppProp("driver.path")).getAbsolutePath();
            System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
            baseUrl = URL;
            isRemote();
            Configuration.browserSize = "1530x870";
            Configuration.browserPosition = "2x2";
            Configuration.timeout = 20000;
            Configuration.driverManagerEnabled = false;
        }
        //####Config for Ui###

        String fileSecret = Configure.getAppProp("data.folder") + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json";
        if (Files.exists(Paths.get(fileSecret)))
            ObjectPoolService.loadEntities(DataFileHelper.read(fileSecret));
        loadSecretJson();
    }

    public void loadSecretJson() {
        String secret = System.getProperty("secret");
        if(secret == null)
            secret = Configure.getAppProp("secret");
        String file = Configure.getAppProp("data.folder") + "/shareFolder/" + "secret.bin";
        if (!Files.exists(Paths.get(file)) || secret == null)
            return;
        ObjectPoolService.loadEntities(Encrypt.Aes256Decode(Base64.getDecoder().decode(DataFileHelper.read(file)), secret));
    }

    @SneakyThrows
    public void testPlanExecutionFinished(TestPlan testPlan) {
        if(Configure.isIntegrationTestIt())
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
