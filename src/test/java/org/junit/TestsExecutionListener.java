package org.junit;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import core.CacheService;
import steps.Steps;

public class TestsExecutionListener implements TestExecutionListener {

    public void testPlanExecutionStarted(TestPlan testPlan) {
        CacheService.loadEntities(Steps.dataFolder + "/shareFolder/shareData.json");
    }

    public void testPlanExecutionFinished(TestPlan testPlan) {
        CacheService.saveEntities(Steps.dataFolder + "/shareFolder/logData.json");
    }
}
