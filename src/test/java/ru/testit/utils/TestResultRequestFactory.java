package ru.testit.utils;

import ru.testit.junit5.*;
import org.apache.commons.lang3.exception.*;
import ru.testit.model.request.*;
import ru.testit.services.*;

import java.util.*;

public class TestResultRequestFactory {
    private TestResultsRequest request;

    public void processFinishLaunch(final HashMap<MethodType, StepNode> utilsMethodSteps, final HashMap<UniqueTest, StepNode> includedTests) {
        this.request = new TestResultsRequest();
        for (final UniqueTest test : includedTests.keySet()) {
            final String externalId = test.getExternalId();
            final TestResultRequest currentTest = new TestResultRequest();
            currentTest.setAutoTestExternalId(externalId);
            currentTest.setConfigurationId(test.getConfigurationId());
            this.processTestSteps(currentTest, includedTests.get(test));
            this.processUtilsMethodsSteps(currentTest, utilsMethodSteps);
            this.request.getTestResults().add(currentTest);
        }
    }

    public void processFinishLaunchUniqueTest(final UniqueTest test, final HashMap<MethodType, StepNode> utilsMethodSteps, final HashMap<UniqueTest, StepNode> includedTests) {
        TestResultsRequest req = new TestResultsRequest();
        final String externalId = test.getExternalId();
        final TestResultRequest currentTest = new TestResultRequest();
        currentTest.setAutoTestExternalId(externalId);
        currentTest.setConfigurationId(test.getConfigurationId());
        this.processTestSteps(currentTest, includedTests.get(test));
        this.processUtilsMethodsSteps(currentTest, utilsMethodSteps);
        req.getTestResults().add(currentTest);
        TestITClient.sendTestResult(req);
    }

    private void processUtilsMethodsSteps(final TestResultRequest currentTest, final HashMap<MethodType, StepNode> utilsMethodSteps) {
        for (final MethodType methodType : utilsMethodSteps.keySet()) {
            if (methodType == MethodType.BEFORE_CLASS || methodType == MethodType.BEFORE_METHOD) {
                this.processSetUpSteps(currentTest, utilsMethodSteps.get(methodType));
            } else {
                this.processTearDownSteps(currentTest, utilsMethodSteps.get(methodType));
            }
        }
    }

    public void processTestSteps(final TestResultRequest testResult, final StepNode parentStep) {
//        testResult.setConfigurationId(TestITClient.getConfigurationId());
        final Date startedOn = parentStep.getStartedOn();
        final Date completedOn = parentStep.getCompletedOn();
        testResult.setStartedOn(startedOn);
        testResult.setCompletedOn(completedOn);
        testResult.setDuration((int) (completedOn.getTime() - startedOn.getTime()));
        testResult.setOutcome(parentStep.getOutcome());
        final Throwable failureReason = parentStep.getFailureReason();
        if (failureReason != null) {
            testResult.setMessage(failureReason.getMessage());
            testResult.setTraces(ExceptionUtils.getStackTrace(failureReason));
        }
        testResult.getLinks().addAll(this.makeInnerLinks(parentStep.getLinkItems()));
        final InnerResult innerResult = this.makeInnerResult(parentStep);
        this.processStep(testResult, parentStep.getChildrens(), innerResult.getStepResults());
        testResult.getStepResults().add(innerResult);
    }

    private void processSetUpSteps(final TestResultRequest testResult, final StepNode parentStep) {
        final InnerResult innerResult = this.makeInnerResult(parentStep);
        this.processStep(testResult, parentStep.getChildrens(), innerResult.getStepResults());
        testResult.getSetupResults().add(innerResult);
    }

    private void processTearDownSteps(final TestResultRequest testResult, final StepNode parentStep) {
        final InnerResult innerResult = this.makeInnerResult(parentStep);
        this.processStep(testResult, parentStep.getChildrens(), innerResult.getStepResults());
        testResult.getTeardownResults().add(innerResult);
    }

    private InnerResult makeInnerResult(final StepNode stepNode) {
        final InnerResult innerResult = new InnerResult();
        innerResult.setTitle(stepNode.getTitle());
        innerResult.setDescription(stepNode.getDescription());
        final Date startedOn = stepNode.getStartedOn();
        final Date completedOn = stepNode.getCompletedOn();

        innerResult.setParameters(InnerResult.parametersMap.get());
        innerResult.setStartedOn(startedOn);
        innerResult.setCompletedOn(completedOn);
        innerResult.setDuration((int) (completedOn.getTime() - startedOn.getTime()));
        innerResult.setOutcome(stepNode.getOutcome());
        return innerResult;
    }

    private List<InnerLink> makeInnerLinks(final List<LinkItem> linkItems) {
        final List<InnerLink> innerLinks = new LinkedList<InnerLink>();
        for (final LinkItem linkItem : linkItems) {
            final InnerLink innerLink = new InnerLink();
            innerLink.setUrl(linkItem.getUrl());
            innerLink.setTitle(linkItem.getTitle());
            innerLink.setDescription(linkItem.getDescription());
            innerLink.setType((linkItem.getType() != null) ? linkItem.getType().getValue() : null);
            innerLinks.add(innerLink);
        }
        return innerLinks;
    }

    private void processStep(final TestResultRequest testResult, final List<StepNode> childrens, final List<InnerResult> steps) {
        for (final StepNode children : childrens) {
            testResult.getLinks().addAll(this.makeInnerLinks(children.getLinkItems()));
            final InnerResult stepResult = this.makeInnerResult(children);
            steps.add(stepResult);
            if (!children.getChildrens().isEmpty()) {
                this.processStep(testResult, children.getChildrens(), stepResult.getStepResults());
            }
        }
    }

    public TestResultsRequest getTestResultRequest() {
        return this.request;
    }
}
