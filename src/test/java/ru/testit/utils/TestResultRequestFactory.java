package ru.testit.utils;

import ru.testit.junit5.*;
import org.apache.commons.lang3.exception.*;
import ru.testit.model.request.*;
import ru.testit.services.*;

import java.nio.charset.StandardCharsets;
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
        String testResultId = TestITClient.sendTestResult(req);
        for(StepNode step : includedTests.values()) {
            Attachment log = new Attachment();
            log.setFileName("log-step.log");
            log.setBytes(step.getStepLog().getBytes(StandardCharsets.UTF_8));
            step.getAttachments().add(log);
            for(Attachment attachment : step.getAttachments()) {
                attachment.setId(TestITClient.sendAttachment(attachment, testResultId));
            }
        }
        req = new TestResultsRequest();
        final TestResultRequest currentTest2 = new TestResultRequest();
        currentTest2.setAutoTestExternalId(externalId);
        currentTest2.setConfigurationId(test.getConfigurationId());
        this.processTestSteps(currentTest2, includedTests.get(test));
        this.processUtilsMethodsSteps(currentTest2, utilsMethodSteps);
        req.getTestResults().add(currentTest2);
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
        testResult.getStepResults().addAll(innerResult.getStepResults());
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
    private InnerResult makeInnerResult(final StepNode stepNode, String testResultId) {
        final InnerResult innerResult = makeInnerResult(stepNode);
        List<Map<String, String>> attachmentList = new ArrayList<>();
        for(Attachment attachment : stepNode.getAttachments()) {
            attachment.setId(TestITClient.sendAttachment(attachment, testResultId));
            Map<String, String> attachmentsMap = new HashMap<>();
            attachmentsMap.put("id", attachment.getId());
            attachmentList.add(attachmentsMap);
        }
        innerResult.setAttachments(attachmentList);
        return innerResult;
    }
    private InnerResult makeInnerResult(final StepNode stepNode) {
        final InnerResult innerResult = new InnerResult();
        innerResult.setTitle(stepNode.getTitle());
        innerResult.setDescription(stepNode.getDescription());
        final Date startedOn = stepNode.getStartedOn();
        final Date completedOn = stepNode.getCompletedOn();
        if(Objects.nonNull(stepNode.getParameters()))
            innerResult.setParameters(stepNode.getParameters());
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
