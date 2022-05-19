package ru.testit.utils;

import java.lang.reflect.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.testit.services.*;
import ru.testit.junit5.*;
import ru.testit.model.request.*;
import ru.testit.annotations.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CreateTestItemRequestFactory {
    private static Map<UniqueTest, CreateTestItemRequest> createTestItemRequests;

    public CreateTestItemRequestFactory() {
        createTestItemRequests = new ConcurrentHashMap<>();
    }

    public void processTest(final Method method, String displayName, String configurationId, Set<String> tags) {
        final CreateTestItemRequest createTestItemRequest = new CreateTestItemRequest();
        final String externalId = this.extractExternalID(method, null);
        createTestItemRequest.setExternalId(externalId);
        createTestItemRequest.setProjectId(TestITClient.getProjectID());
        createTestItemRequest.setName(displayName);
//        createTestItemRequest.setClassName(method.getDeclaringClass().getSimpleName());
//        createTestItemRequest.setNameSpace((method.getDeclaringClass().getPackage() == null) ? null : method.getDeclaringClass().getPackage().getName());

        createTestItemRequest.setClassName(extractFeatureValue(method));
        createTestItemRequest.setNameSpace(extractEpicValue(method));

        createTestItemRequest.setTestPlanId(this.extractTestPlanId(method));
        createTestItemRequest.setLinks(this.extractLinks(method));
        createTestItemRequest.setLabels(this.extractLabels(tags));
        createTestItemRequests.put(new UniqueTest(externalId, configurationId), createTestItemRequest);
    }

//    public void processFinishLaunch(final HashMap<MethodType, StepNode> utilsMethodSteps, final HashMap<UniqueTest, StepNode> includedTests) {
//        for (UniqueTest test : this.createTestItemRequests.keySet()) {
//            final CreateTestItemRequest createTestItemRequest = this.createTestItemRequests.get(test);
//            final StepNode testParentStepNode = includedTests.get(test);
//            createTestItemRequest.setOutcome(Outcome.getByValue(testParentStepNode.getOutcome()));
//            this.processTestSteps(createTestItemRequest, testParentStepNode);
//            this.processUtilsSteps(createTestItemRequest, utilsMethodSteps);
//        }
//    }

    public void processFinishLaunchUniqueTest(final Map<ExMethodType, StepNode> utilsMethodSteps, final StepNode testParentStepNode, UniqueTest test) {
        final CreateTestItemRequest createTestItemRequest = createTestItemRequests.get(test);
        try {
            createTestItemRequest.setOutcome(Outcome.getByValue(testParentStepNode.getOutcome()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.processTestSteps(createTestItemRequest, testParentStepNode);
        this.processUtilsSteps(createTestItemRequest, utilsMethodSteps);
    }

    private void processTestSteps(final CreateTestItemRequest createTestItemRequest, final StepNode parentStep) {
        createTestItemRequest.setTitle(parentStep.getTitle());
        createTestItemRequest.setDescription(parentStep.getDescription());
        this.processStep(parentStep.getChildrens(), createTestItemRequest.getSteps());
    }

    private void processUtilsSteps(final CreateTestItemRequest createTestItemRequest, final Map<ExMethodType, StepNode> utilsMethodSteps) {
        for (final ExMethodType exMethodType : utilsMethodSteps.keySet()) {
            if (exMethodType.getMethodType() == MethodType.BEFORE_CLASS || exMethodType.getMethodType() == MethodType.BEFORE_METHOD) {
                this.processSetUpSteps(createTestItemRequest, utilsMethodSteps.get(exMethodType));
            } else {
                this.processTearDownSteps(createTestItemRequest, utilsMethodSteps.get(exMethodType));
            }
        }
    }

    private void processSetUpSteps(final CreateTestItemRequest createTestItemRequest, final StepNode stepNode) {
        final InnerItem setUp = new InnerItem();
        setUp.setTitle(stepNode.getTitle());
        setUp.setDescription(stepNode.getDescription());
        this.processStep(stepNode.getChildrens(), setUp.getSteps());
        createTestItemRequest.getSetUp().add(setUp);
    }

    private void processTearDownSteps(final CreateTestItemRequest createTestItemRequest, final StepNode stepNode) {
        final InnerItem tearDown = new InnerItem();
        tearDown.setTitle(stepNode.getTitle());
        tearDown.setDescription(stepNode.getDescription());
        this.processStep(stepNode.getChildrens(), tearDown.getSteps());
        createTestItemRequest.getTearDown().add(tearDown);
    }

    private void processStep(final List<StepNode> childrens, final List<InnerItem> steps) {
        for (final StepNode children : childrens) {
            final InnerItem step = new InnerItem();
            step.setTitle(children.getTitle());
            step.setDescription(children.getDescription());
            steps.add(step);
            if (!children.getChildrens().isEmpty()) {
                this.processStep(children.getChildrens(), step.getSteps());
            }
        }
    }

//    private String extractExternalID(final Method atomicTest) {
//        final ExternalId annotation = atomicTest.getAnnotation(ExternalId.class);
//        return (annotation != null) ? annotation.value() : null;
//    }

    private String extractExternalID(final Method currentTest, @Nullable String subId) {
        String className = currentTest.getDeclaringClass().getSimpleName();
        String methodName = currentTest.getName();
        String postfix = "";
        if (Objects.nonNull(subId))
            postfix = "#" + subId;
        return className + "#" + methodName + postfix;
    }

//    private String extractDisplayName(final Method atomicTest) {
//        final DisplayName annotation = atomicTest.getAnnotation(DisplayName.class);
//        return (annotation != null) ? annotation.value() : null;
//    }

    private List<String> extractTestPlanId(final Method method) {
        final TmsLinks tmsLinks = method.getAnnotation(TmsLinks.class);
        if(tmsLinks != null)
            return Arrays.stream(tmsLinks.value()).map(TmsLink::value).collect(Collectors.toList());
        final TmsLink annotation = method.getAnnotation(TmsLink.class);
        if(annotation != null)
            return Collections.singletonList(annotation.value());
        return null;
    }

    private List<InnerLink> extractLinks(final Method method) {
        final List<InnerLink> links = new LinkedList<InnerLink>();
        final Links linksAnnotation = method.getAnnotation(Links.class);
        if (linksAnnotation != null) {
            for (final Link link : linksAnnotation.links()) {
                links.add(this.makeInnerLink(link));
            }
        } else {
            final Link linkAnnotation = method.getAnnotation(Link.class);
            if (linkAnnotation != null) {
                links.add(this.makeInnerLink(linkAnnotation));
            }
        }
        return links;
    }

    private InnerLink makeInnerLink(final Link linkAnnotation) {
        final InnerLink innerLink = new InnerLink();
        innerLink.setTitle(linkAnnotation.title());
        innerLink.setDescription(linkAnnotation.description());
        innerLink.setUrl(linkAnnotation.url());
        innerLink.setType(linkAnnotation.type().getValue());
        return innerLink;
    }

    private List<Label> extractLabels(Set<String> tags) {
        final List<Label> labels = new LinkedList<>();
        for (String s : tags) {
                final Label label = new Label();
                label.setName(s);
                labels.add(label);
            }
        return labels;
    }

    public String extractEpicValue(final Method method){
        final Epic annotation = method.getDeclaringClass().getAnnotation(Epic.class);
        if (annotation != null) {
            return annotation.value();
        }
        return method.getDeclaringClass().getName();
    }

    public String extractFeatureValue(final Method method){
        final Feature annotation = method.getDeclaringClass().getAnnotation(Feature.class);
        if (annotation != null) {
            return annotation.value();
        }
        return method.getName();
    }

    public Collection<CreateTestItemRequest> getCreateTestRequests() {
        return this.createTestItemRequests.values();
    }

    public CreateTestItemRequest getCreateTestRequests(UniqueTest test) {
        return this.createTestItemRequests.get(test);
    }
}
