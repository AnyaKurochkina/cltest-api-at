package org.junit;

import lombok.SneakyThrows;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import core.CacheService;
import steps.Steps;

import java.io.FileWriter;

import static core.helper.Configure.*;

public class TestsExecutionListener implements TestExecutionListener {

    public void testPlanExecutionStarted(TestPlan testPlan) {
        CacheService.loadEntities(Steps.dataFolder + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json");
    }

    @SneakyThrows
    public void testPlanExecutionFinished(TestPlan testPlan) {
        CacheService.saveEntities(Steps.dataFolder + "/shareFolder/logData.json");
        FileWriter fooWriter = new FileWriter(RESOURCE_PATH + "/environment.properties", false);
        fooWriter.write("ENV=" + ENV);
        fooWriter.close();
    }
}
