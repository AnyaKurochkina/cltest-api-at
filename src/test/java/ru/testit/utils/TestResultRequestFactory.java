package ru.testit.utils;

import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.testit.junit5.MethodType;
import ru.testit.model.request.*;
import ru.testit.services.LinkItem;
import ru.testit.services.TestITClient;

import java.util.*;

public class TestResultRequestFactory {
    private TestResultsRequest request;

    public void processFinishLaunch(final Map<MethodType, StepNode> utilsMethodSteps, final Map<UniqueTest, StepNode> includedTests) {
        this.request = new TestResultsRequest();
        for (final UniqueTest test : includedTests.keySet()) {
            final String externalId = test.getExternalId();
            final TestResultRequest currentTest = new TestResultRequest();
            currentTest.setAutoTestExternalId(externalId);
            currentTest.setConfigurationId(test.getConfigurationId());
            this.processTestSteps(currentTest, includedTests.get(test), null);
            this.processUtilsMethodsSteps(currentTest, utilsMethodSteps);
            this.request.getTestResults().add(currentTest);
        }
    }

    public void processFinishLaunchUniqueTest(final UniqueTest test, final Map<MethodType, StepNode> utilsMethodSteps, StepNode step) {
        TestResultsRequest req = new TestResultsRequest();
        final String externalId = test.getExternalId();
        final TestResultRequest currentTest = new TestResultRequest();
        currentTest.setAutoTestExternalId(externalId);
        currentTest.setConfigurationId(test.getConfigurationId());
        this.processTestSteps(currentTest, step, null);
        this.processUtilsMethodsSteps(currentTest, utilsMethodSteps);
        if (currentTest.getMessage() != null)
            currentTest.setMessage(currentTest.getMessage().replaceAll("\n", "\t \n"));
        req.getTestResults().add(currentTest);
        String testResultId = TestITClient.sendTestResult(req);

//        if (Tests.isAttachLog()) {
//            Attachment log = new Attachment();
//            log.setFileName("test-log.log");
//            log.setBytes(Tests.getAttachLog().getBytes());
//            if (log.getBytes().length > 0)
//                step.getAttachments().add(log);

//        String stepId = Allure.getLifecycle().getCurrentTestCaseOrStep().orElse(null);
//        if (Objects.nonNull(stepId)) {
//            final List<io.qameta.allure.model.Attachment> attachments = new ArrayList<>();
//            Allure.getLifecycle().updateTestCase(stepId, s -> attachments.addAll(s.getAttachments()));
//            for (io.qameta.allure.model.Attachment attachment : attachments) {
//                Attachment attach = new Attachment();
//                attach.setFileName(attachment.getName());
//                attach.setBytes(DataFileHelper.readBytes(Configure.getAppProp("allure.results") + attachments.get(0).getSource()));
//                step.getAttachments().add(attach);
//            }
//        }


//        List<Map<String, String>> attachmentList = new ArrayList<>();
        Iterator<Attachment> iterator = UniqueTest.getAndClearAttachmentList().iterator();
        while (iterator.hasNext()) {
            Attachment attachment = iterator.next();
            attachment.setId(TestITClient.sendAttachment(attachment, testResultId));

//            Map<String, String> attachmentsMap = new HashMap<>();
//            attachmentsMap.put("id", attachment.getId());
//            attachmentList.add(attachmentsMap);
        }
//        currentTest.setAttachments(attachmentList);

//        req = new TestResultsRequest();
//        final TestResultRequest currentTest2 = new TestResultRequest();
//        currentTest2.setAutoTestExternalId(externalId);
//        currentTest2.setConfigurationId(test.getConfigurationId());
//        this.processTestSteps(currentTest2, includedTests.get(test), testResultId);
//        this.processUtilsMethodsSteps(currentTest2, utilsMethodSteps);
//        req.getTestResults().add(currentTest2);
//        TestITClient.sendTestResult(req);
    }

    private List<Attachment> getAttachmentList(StepNode stepNode){
        List<Attachment> list = new ArrayList<>(stepNode.getAttachments());
        stepNode.getChildrens().forEach(c -> list.addAll(getAttachmentList(c)));
        return list;
    }

    private void processUtilsMethodsSteps(final TestResultRequest currentTest, final Map<MethodType, StepNode> utilsMethodSteps) {
        for (final MethodType exMethodType : utilsMethodSteps.keySet()) {
            if (exMethodType == MethodType.BEFORE_CLASS || exMethodType == MethodType.BEFORE_METHOD) {
                this.processSetUpSteps(currentTest, utilsMethodSteps.get(exMethodType));
            } else {
                this.processTearDownSteps(currentTest, utilsMethodSteps.get(exMethodType));
            }
        }
    }

    public void processTestSteps(final TestResultRequest testResult, final StepNode parentStep, String testResultId) {
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
        final InnerResult innerResult;
        innerResult = this.makeInnerResult(parentStep);
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

    private InnerResult makeInnerResult(final StepNode stepNode) {
        final InnerResult innerResult = new InnerResult();

//        List<Map<String, String>> attachmentList = new ArrayList<>();
//        for (Attachment attachment : stepNode.getAttachments()) {
//            Map<String, String> attachmentsMap = new HashMap<>();
//            attachmentsMap.put("id", attachment.getId());
//            attachmentList.add(attachmentsMap);
//        }
//        innerResult.setAttachments(attachmentList);
        innerResult.setTitle(stepNode.getTitle());
        innerResult.setDescription(stepNode.getDescription());
        final Date startedOn = stepNode.getStartedOn();
        final Date completedOn = stepNode.getCompletedOn();
        if (Objects.nonNull(stepNode.getParameters()))
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
        List<Map<String, String>> attachmentList = new ArrayList<>();
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
