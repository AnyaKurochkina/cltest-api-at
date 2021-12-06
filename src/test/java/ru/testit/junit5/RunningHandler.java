package ru.testit.junit5;

import java.lang.reflect.*;
import java.util.*;

import core.helper.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.testit.model.response.StartLaunchResponse;
import ru.testit.services.*;
import ru.testit.utils.*;
import ru.testit.annotations.*;

public class RunningHandler
{
    private TestITClient testITClient;
    private CreateTestItemRequestFactory createTestItemRequestFactory;
    private TestResultRequestFactory testResultRequestFactory;
    private LinkedHashMap<MethodType, StepNode> utilsMethodSteps;
    private HashMap<UniqueTest, StepNode> includedTests;
    private List<UniqueTest> alreadyFinished;
    
    public RunningHandler() {
        this.createTestItemRequestFactory = new CreateTestItemRequestFactory();
        this.testResultRequestFactory = new TestResultRequestFactory();
        this.utilsMethodSteps = new LinkedHashMap<MethodType, StepNode>();
        this.includedTests = new HashMap<UniqueTest, StepNode>();
        this.alreadyFinished = new LinkedList<UniqueTest>();
        this.testITClient = new TestITClient();
    }
    
    public void startLaunch() {
        String testRunId = System.getProperty("testrun");
        if(Objects.nonNull(testRunId)){
            TestITClient.startLaunchResponse = new StartLaunchResponse();
            TestITClient.startLaunchResponse.setId(testRunId);
            return;
        }
        this.testITClient.startLaunch();
    }
    
    public void finishLaunch() {
//        this.createTestItemRequestFactory.processFinishLaunch(this.utilsMethodSteps, this.includedTests);
//        this.testITClient.sendTestItems(this.createTestItemRequestFactory.getCreateTestRequests());
        this.testResultRequestFactory.processFinishLaunch(this.utilsMethodSteps, this.includedTests);
//        this.testITClient.finishLaunch(this.testResultRequestFactory.getTestResultRequest());
        this.testITClient.sendCompleteTestRun();
    }
    
    public void startTest(Method currentTest, String displayName, String configurationId) {
        this.createTestItemRequestFactory.processTest(currentTest, displayName, configurationId);
        final StepNode parentStep = new StepNode();
        parentStep.setTitle(displayName);
        parentStep.setDescription(this.extractDescription(currentTest));
        parentStep.setStartedOn(new Date());

        //TODO: UUID
        this.includedTests.put(new UniqueTest(extractExternalID(currentTest, null), configurationId), parentStep);
        StepAspect.setStepNodes(parentStep);
    }
    
    public void finishTest(final Method atomicTest, final Throwable thrown, String configurationId) {
//        final String externalId = extractExternalID(atomicTest, subId);
        UniqueTest test = new UniqueTest(extractExternalID(atomicTest, null), configurationId);
        if (this.alreadyFinished.contains(test)) {
            return;
        }
        final StepNode parentStep = this.includedTests.get(new UniqueTest(extractExternalID(atomicTest, null), configurationId));
        if (parentStep != null) {
            parentStep.setOutcome((thrown == null) ? Outcome.PASSED.getValue() : Outcome.FAILED.getValue());
            parentStep.setFailureReason(thrown);
            parentStep.setCompletedOn(new Date());
        }
        this.alreadyFinished.add(test);
        this.createTestItemRequestFactory.processFinishLaunchUniqueTest(this.utilsMethodSteps, this.includedTests, test);
        this.testITClient.sendTestItemsUniqueTest(this.createTestItemRequestFactory.getCreateTestRequests(test));
        this.testResultRequestFactory.processFinishLaunchUniqueTest(test, this.utilsMethodSteps, this.includedTests);
    }
    
    public void startUtilMethod(final MethodType currentMethod, final Method method) {
        final StepNode parentStep = new StepNode();
        parentStep.setTitle(this.extractTitle(method));
        parentStep.setDescription(this.extractDescription(method));
        parentStep.setStartedOn(new Date());
        this.utilsMethodSteps.putIfAbsent(currentMethod, parentStep);
        StepAspect.setStepNodes(parentStep);
    }
    
    public void finishUtilMethod(final MethodType currentMethod, final Throwable thrown) {
        final StepNode parentStep = this.utilsMethodSteps.get(currentMethod);
        parentStep.setOutcome((thrown == null) ? Outcome.PASSED.getValue() : Outcome.FAILED.getValue());
        parentStep.setCompletedOn(new Date());
        if (currentMethod == MethodType.BEFORE_METHOD) {
            StepAspect.returnStepNode();
        }
    }
    
    private String extractDescription(final Method currentTest) {
        final Description annotation = currentTest.getAnnotation(Description.class);
        return (annotation != null) ? annotation.value() : null;
    }
    
    private String extractTitle(final Method currentTest) {
        final Title annotation = currentTest.getAnnotation(Title.class);
        return (annotation != null) ? annotation.value() : null;
    }
    
    public static String extractExternalID(final Method currentTest, @Nullable String subId) {
        String className = currentTest.getDeclaringClass().getSimpleName();
        String methodName = currentTest.getName();
        String postfix = "";
        if(Objects.nonNull(subId))
            postfix = "#" + subId;
        return className + "." + methodName + postfix;
    }
}
