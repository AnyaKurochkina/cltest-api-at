package org.junit;


import core.helper.Configure;
import models.ObjectPoolService;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;


import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static core.helper.Configure.ENV;

@Log4j2
public class TestsExecutionListener implements TestExecutionListener {

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
        String fileSecret = Configure.getAppProp("data.folder") + "/shareFolder/secret.json";
        if (Files.exists(Paths.get(fileSecret)))
            ObjectPoolService.loadEntities(fileSecret);
        ObjectPoolService.loadEntities(Configure.getAppProp("data.folder") + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json");
    }

    @SneakyThrows
    public void testPlanExecutionFinished(TestPlan testPlan) {
        ObjectPoolService.saveEntities(Configure.getAppProp("data.folder") + "/shareFolder/logData.json");
        new File(Configure.getAppProp("allure.results")).mkdir();
        FileWriter fooWriter = new FileWriter(Configure.getAppProp("allure.results") + "environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
    }


}
