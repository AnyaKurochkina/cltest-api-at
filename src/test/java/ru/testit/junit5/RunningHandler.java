package ru.testit.junit5;

import io.qameta.allure.aspects.StepsAspects;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import ru.testit.annotations.Description;
import ru.testit.annotations.Title;
import ru.testit.model.response.StartLaunchResponse;
import ru.testit.services.TestITClient;
import ru.testit.utils.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class RunningHandler
{
    private static final TestITClient testITClient = new TestITClient();
    private final CreateTestItemRequestFactory createTestItemRequestFactory = new CreateTestItemRequestFactory();
    private final TestResultRequestFactory testResultRequestFactory = new TestResultRequestFactory();
    private static final Map<MethodType, StepNode> utilsMethodSteps = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final ConcurrentHashMap<UniqueTest, StepNode> includedTests = new ConcurrentHashMap<>();
    private static final List<UniqueTest> alreadyFinished = Collections.synchronizedList(new LinkedList<>());

    
    public synchronized void startLaunch() {
        String testRunId = System.getProperty("testRunId");
        if(Objects.nonNull(testRunId)){
            TestITClient.startLaunchResponse = new StartLaunchResponse();
            TestITClient.startLaunchResponse.setId(testRunId);
            TestITClient.sendStartTestRun();
            return;
        }
        TestITClient.startLaunch();
    }
    
    public synchronized void finishLaunch() {
//        this.createTestItemRequestFactory.processFinishLaunch(this.utilsMethodSteps, this.includedTests);
//        this.testITClient.sendTestItems(this.createTestItemRequestFactory.getCreateTestRequests());
        this.testResultRequestFactory.processFinishLaunch(utilsMethodSteps, includedTests);
//        this.testITClient.finishLaunch(this.testResultRequestFactory.getTestResultRequest());
        testITClient.sendCompleteTestRun();
    }
    
    public synchronized void startTest(Method currentTest, String displayName, String configurationId) {
        this.createTestItemRequestFactory.processTest(currentTest, displayName, configurationId);
        final StepNode parentStep = new StepNode();
        parentStep.setTitle(displayName);
        parentStep.setDescription(this.extractDescription(currentTest));
        parentStep.setStartedOn(new Date());
        log.info("startTest " + new UniqueTest(extractExternalID(currentTest, null), configurationId));
        //TODO: UUID
        includedTests.put(new UniqueTest(extractExternalID(currentTest, null), configurationId), parentStep);
        StepsAspects.setStepNodes(parentStep);
    }
    
    public synchronized void finishTest(final Method atomicTest, final Throwable thrown, String configurationId) {
//        final String externalId = extractExternalID(atomicTest, subId);
        UniqueTest test = new UniqueTest(extractExternalID(atomicTest, null), configurationId);
        if (alreadyFinished.contains(test)) {
            return;
        }
        final StepNode parentStep = includedTests.get(new UniqueTest(extractExternalID(atomicTest, null), configurationId));
        if (parentStep != null) {
            parentStep.setOutcome((thrown == null) ? Outcome.PASSED.getValue() : Outcome.FAILED.getValue());
            parentStep.setFailureReason(thrown);
            parentStep.setCompletedOn(new Date());
        }
        alreadyFinished.add(test);
        this.createTestItemRequestFactory.processFinishLaunchUniqueTest(utilsMethodSteps, includedTests, test);
        testITClient.sendTestItemsUniqueTest(this.createTestItemRequestFactory.getCreateTestRequests(test));
        this.testResultRequestFactory.processFinishLaunchUniqueTest(test, utilsMethodSteps, includedTests);
    }
    
    public synchronized void startUtilMethod(final MethodType currentMethod, final Method method) {
        final StepNode parentStep = new StepNode();
        parentStep.setTitle(this.extractTitle(method));
        parentStep.setDescription(this.extractDescription(method));
        parentStep.setStartedOn(new Date());
        utilsMethodSteps.putIfAbsent(currentMethod, parentStep);
        StepsAspects.setStepNodes(parentStep);
    }
    
    public synchronized void finishUtilMethod(final MethodType currentMethod, final Throwable thrown) {
        final StepNode parentStep = utilsMethodSteps.get(currentMethod);
        parentStep.setOutcome((thrown == null) ? Outcome.PASSED.getValue() : Outcome.FAILED.getValue());
        parentStep.setCompletedOn(new Date());
        if (currentMethod == MethodType.BEFORE_METHOD) {
            StepsAspects.returnStepNode();
        }
    }
    
    private synchronized String extractDescription(final Method currentTest) {
        final Description annotation = currentTest.getAnnotation(Description.class);
        return (annotation != null) ? annotation.value() : null;
    }
    
    private synchronized String extractTitle(final Method currentTest) {
        final Title annotation = currentTest.getAnnotation(Title.class);
        return (annotation != null) ? annotation.value() : null;
    }
    
    public static String extractExternalID(final Method currentTest, @Nullable String subId) {
        String className = currentTest.getDeclaringClass().getSimpleName();
        String methodName = currentTest.getName();
        String postfix = "";
        if(Objects.nonNull(subId))
            postfix = "#" + subId;
        return className + "#" + methodName + postfix;
    }
}
