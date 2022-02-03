package org.junit;


import core.helper.Configure;
import core.helper.DataFileHelper;
import core.utils.Encrypt;
import models.ObjectPoolService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import ru.testit.junit5.JUnit5EventListener;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static core.helper.Configure.ENV;

//@Log4j2
public class TestsExecutionListener implements TestExecutionListener {
    @SneakyThrows
    public void testPlanExecutionStarted(TestPlan testPlan) {
        String fileSecret = Configure.getAppProp("data.folder") + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json";
        if (Files.exists(Paths.get(fileSecret)))
            ObjectPoolService.loadEntities(DataFileHelper.read(fileSecret));
        loadSecretJson();
    }

    public void loadSecretJson() throws Exception {
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
        JUnit5EventListener.HANDLER.finishLaunch();
        ObjectPoolService.saveEntities(Configure.getAppProp("data.folder") + "/shareFolder/logData.json");
        new File(Configure.getAppProp("allure.results")).mkdir();
        FileWriter fooWriter = new FileWriter(Configure.getAppProp("allure.results") + "environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
        System.out.println("##teamcity[publishArtifacts 'logs => logs']");
    }


}
