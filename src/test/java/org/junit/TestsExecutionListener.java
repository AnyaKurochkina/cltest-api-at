package org.junit;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import tests.suites.CacheService;
import tests.suites.Steps;

public class TestsExecutionListener implements TestExecutionListener {

    public void testPlanExecutionStarted(TestPlan testPlan) {
        CacheService.loadEntities(Steps.dataFolder + "/shareFolder/dataJson.json");
    }

    public void testPlanExecutionFinished(TestPlan testPlan) {
        CacheService.saveEntities(Steps.dataFolder + "/shareFolder/dataLog.json");
    }
}
