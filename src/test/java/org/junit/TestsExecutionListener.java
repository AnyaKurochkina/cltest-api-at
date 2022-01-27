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


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static core.helper.Configure.ENV;

//@Log4j2
public class TestsExecutionListener implements TestExecutionListener {
//    private static final Logger log = LogManager.getLogger("LogTest");
    @SneakyThrows
    public void testPlanExecutionStarted(TestPlan testPlan) {
//        List<Class<?>> classes = ClassFinder.find("tests");
//        Set<Float> values = new HashSet<>();
//        for (Class<?> c : classes) {
//            for (Method method : c.getDeclaredMethods()) {
//                if (method.isAnnotationPresent(TmsLink.class)) {
//                    Float value = Float.valueOf(method.getAnnotation(TmsLink.class).value());
//                    if (values.contains(value)) {
//                        log.error(String.format("Значение '%s' у аннотации TmsLink не уникально", value));
//                        System.exit(1);
//                    }
//                    values.add(value);
//                }
//            }
//        }
//        log.info("tmsLink max {}", Math.round(Collections.max(values)));
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
        ObjectPoolService.saveEntities(Configure.getAppProp("data.folder") + "/shareFolder/logData.json");
        new File(Configure.getAppProp("allure.results")).mkdir();
        FileWriter fooWriter = new FileWriter(Configure.getAppProp("allure.results") + "environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
        System.out.println("##teamcity[publishArtifacts 'logs => logs']");
    }


}
