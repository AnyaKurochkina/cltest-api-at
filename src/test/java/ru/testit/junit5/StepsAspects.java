//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.testit.junit5;

import lombok.Getter;
import org.aspectj.lang.annotation.Aspect;
import ru.testit.utils.Outcome;
import ru.testit.utils.StepNode;
import ru.testit.utils.StepUtils;

import java.util.Date;
import java.util.Objects;

import static core.helper.Configure.isIntegrationTestIt;

@Aspect
public class StepsAspects {
    @Getter
    private static final InheritableThreadLocal<StepNode> currentStep;
    private static final InheritableThreadLocal<StepNode> previousStep;


    public static void startNestedStep(final String title, final String description) {
        if (Objects.isNull(currentStep.get()))
            return;
        if (!isIntegrationTestIt())
            return;
        final StepNode currStep = currentStep.get();
        final StepNode newStep = StepUtils.makeStepNode(title, description, currStep);
        newStep.setStartedOn(new Date());
        currStep.getChildrens().add(newStep);
        currentStep.set(newStep);
    }

    public static void finishNestedStep() {
        if (Objects.isNull(currentStep.get()))
            return;
        if (!isIntegrationTestIt())
            return;
        final StepNode currStep = currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.PASSED.getValue());
        currentStep.set(currStep.getParent());
    }

    public static void failedNestedStep() {
        if (Objects.isNull(currentStep.get()))
            return;
        if (!isIntegrationTestIt())
            return;
        final StepNode currStep = currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.FAILED.getValue());
        currentStep.set(currStep.getParent());
    }

    //    @Pointcut("@annotation(addLink)")
//    public void withAddLinkAnnotation(final AddLink addLink) {
//    }

    //    @Pointcut("args(linkItem)")
//    public void hasLinkArg(final LinkItem linkItem) {
//    }

    //    @Before(value = "withAddLinkAnnotation(addLink) && hasLinkArg(linkItem) && anyMethod()", argNames = "addLink, link")
//    public void startAddLink(final AddLink addLink, final LinkItem linkItem) {
//        final StepNode stepNode = currentStep.get();
//        stepNode.getLinkItems().add(linkItem);
//    }

    public static void setStepNodes(final StepNode parentNode) {
        previousStep.set(currentStep.get());
        currentStep.set(parentNode);
    }

    public static void returnStepNode() {
        currentStep.set(previousStep.get());
        previousStep.set(currentStep.get());
    }

    public static void removeCurrentStep() {
        currentStep.remove();
    }

    static {
        currentStep = new InheritableThreadLocal<>();
        previousStep = new InheritableThreadLocal<>();
    }
}
