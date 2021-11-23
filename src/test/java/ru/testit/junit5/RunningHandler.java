package ru.testit.junit5;

import java.lang.reflect.*;
import java.util.*;
import ru.testit.services.*;
import ru.testit.utils.*;
import ru.testit.annotations.*;

public class RunningHandler
{
    private TestITClient testITClient;
    private CreateTestItemRequestFactory createTestItemRequestFactory;
    private TestResultRequestFactory testResultRequestFactory;
    private LinkedHashMap<MethodType, StepNode> utilsMethodSteps;
    private HashMap<String, StepNode> includedTests;
    private List<String> alreadyFinished;
    
    public RunningHandler() {
        this.createTestItemRequestFactory = new CreateTestItemRequestFactory();
        this.testResultRequestFactory = new TestResultRequestFactory();
        this.utilsMethodSteps = new LinkedHashMap<MethodType, StepNode>();
        this.includedTests = new HashMap<String, StepNode>();
        this.alreadyFinished = new LinkedList<String>();
        this.testITClient = new TestITClient();
    }
    
    public void startLaunch() {
        this.testITClient.startLaunch();
    }
    
    public void finishLaunch() {
        this.createTestItemRequestFactory.processFinishLaunch(this.utilsMethodSteps, this.includedTests);
        this.testITClient.sendTestItems(this.createTestItemRequestFactory.getCreateTestRequests());
        this.testResultRequestFactory.processFinishLaunch(this.utilsMethodSteps, this.includedTests);
        this.testITClient.finishLaunch(this.testResultRequestFactory.getTestResultRequest());
    }
    
    public void startTest(final Method currentTest) {
        this.createTestItemRequestFactory.processTest(currentTest);
        final StepNode parentStep = new StepNode();
        parentStep.setTitle(this.extractTitle(currentTest));
        parentStep.setDescription(this.extractDescription(currentTest));
        parentStep.setStartedOn(new Date());
        this.includedTests.put(this.extractExternalID(currentTest), parentStep);
        StepAspect.setStepNodes(parentStep);
    }
    
    public void finishTest(final Method atomicTest, final Throwable thrown) {
        final String externalId = this.extractExternalID(atomicTest);
        if (this.alreadyFinished.contains(externalId)) {
            return;
        }
        final StepNode parentStep = this.includedTests.get(externalId);
        if (parentStep != null) {
            parentStep.setOutcome((thrown == null) ? Outcome.PASSED.getValue() : Outcome.FAILED.getValue());
            parentStep.setFailureReason(thrown);
            parentStep.setCompletedOn(new Date());
        }
        this.alreadyFinished.add(externalId);
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
    
    private String extractExternalID(final Method currentTest) {
        final ExternalId annotation = currentTest.getAnnotation(ExternalId.class);
        return (annotation != null) ? annotation.value() : null;
    }
}
