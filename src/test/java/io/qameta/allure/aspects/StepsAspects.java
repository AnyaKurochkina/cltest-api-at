//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.qameta.allure.aspects;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.AspectUtils;
import io.qameta.allure.util.ResultsUtils;
import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import ru.testit.utils.Outcome;
import ru.testit.utils.StepNode;
import ru.testit.utils.StepUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Aspect
public class StepsAspects {
    private static final InheritableThreadLocal<AllureLifecycle> LIFECYCLE = new InheritableThreadLocal<AllureLifecycle>() {
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

    @Getter
    private static final InheritableThreadLocal<StepNode> currentStep;
    private static final InheritableThreadLocal<StepNode> previousStep;

    public StepsAspects() {
    }

    @Pointcut("@annotation(io.qameta.allure.Step)")
    public void withStepAnnotation() {
    }

    @Pointcut("execution(* *(..))")
    public void anyMethod() {
    }

    @Before("anyMethod() && withStepAnnotation()")
    public void stepStart(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Step step = (Step)methodSignature.getMethod().getAnnotation(Step.class);
        String uuid = UUID.randomUUID().toString();
        String name = AspectUtils.getName(step.value(), joinPoint);
        List<Parameter> parameters = AspectUtils.getParameters(methodSignature, joinPoint.getArgs());
        StepResult result = (new StepResult()).setName(name).setParameters(parameters);
        getLifecycle().startStep(uuid, result);
        startNestedStep(name, "");
    }

    public static void startNestedStep(final String title, final String description) {
//        if(currentStep.get() == null)
//            currentStep.set(new StepNode());
        final StepNode currStep = currentStep.get();
        final StepNode newStep = StepUtils.makeStepNode(title, description, currStep);
        newStep.setStartedOn(new Date());
        currStep.getChildrens().add(newStep);
        currentStep.set(newStep);
    }

    public static void finishNestedStep() {
        final StepNode currStep = currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.PASSED.getValue());
        currentStep.set(currStep.getParent());
    }

    public static void failedNestedStep(final Throwable throwable) {
        final StepNode currStep = currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.FAILED.getValue());
        currentStep.set(currStep.getParent());
    }

    @AfterThrowing(
            pointcut = "anyMethod() && withStepAnnotation()",
            throwing = "e"
    )
    public void stepFailed(Throwable e) {
        getLifecycle().updateStep((s) -> {
            s.setStatus((Status)ResultsUtils.getStatus(e).orElse(Status.BROKEN)).setStatusDetails((StatusDetails)ResultsUtils.getStatusDetails(e).orElse((StatusDetails) null));
        });
        getLifecycle().stopStep();
        failedNestedStep(e);
    }

    @AfterReturning(
            pointcut = "anyMethod() && withStepAnnotation()"
    )
    public void stepStop() {
        getLifecycle().updateStep((s) -> {
            s.setStatus(Status.PASSED);
        });
        getLifecycle().stopStep();
        finishNestedStep();
    }

    public static void setLifecycle(AllureLifecycle allure) {
        LIFECYCLE.set(allure);
    }

    public static AllureLifecycle getLifecycle() {
        return (AllureLifecycle)LIFECYCLE.get();
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

    static {
        currentStep = new InheritableThreadLocal<>();
        previousStep = new InheritableThreadLocal<>();
    }
}
