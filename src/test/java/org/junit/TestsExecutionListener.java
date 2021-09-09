package org.junit;

import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import core.CacheService;
import steps.Steps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import static core.helper.Configure.ENV;

@Log4j2
public class TestsExecutionListener implements TestExecutionListener {

    public void testPlanExecutionStarted(TestPlan testPlan) {
        List<Class<?>> classes = ClassFinder.find("tests");
        Set<String> values = new HashSet<>();
        for(Class<?> c : classes){
            for(Method method : c.getDeclaredMethods()){
                if(method.isAnnotationPresent(TmsLink.class)){
                    String value = method.getAnnotation(TmsLink.class).value();
                    if(values.contains(value)) {
                        log.error(String.format("Значение '%s' у аннотации TmsLink не уникально", value));
                        System.exit(1);
                    }
                    values.add(value);
                }
            }
        }
        CacheService.loadEntities(Steps.dataFolder + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json");
    }

    @SneakyThrows
    public void testPlanExecutionFinished(TestPlan testPlan) {
        CacheService.saveEntities(Steps.dataFolder + "/shareFolder/logData.json");
        FileWriter fooWriter = new FileWriter("target/allure-results/environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
    }




}
