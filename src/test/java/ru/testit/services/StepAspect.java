package ru.testit.services;

import core.helper.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.function.Executable;
import ru.testit.annotations.*;
//import org.aspectj.lang.*;
//import org.aspectj.lang.reflect.*;
import java.util.*;
import ru.testit.utils.*;
//import org.aspectj.lang.annotation.*;

//@Aspect
public class StepAspect
{
    @Getter
    private static final InheritableThreadLocal<StepNode> currentStep;
    private static final InheritableThreadLocal<StepNode> previousStep;

//    @Pointcut("@annotation(step)")
    public void withStepAnnotation(final Step step) {
    }

//    @Pointcut("execution(* *.*(..))")
    public void anyMethod() {
    }



    public static void step(String title, String arg1, Executable executable){
        step(StringUtils.format(title, arg1), executable);
    }

    public static void step(String title, String arg1, String arg2, Executable executable){
        step(StringUtils.format(title, arg1, arg2), executable);
    }

    public static void step(String title, String arg1, String arg2, String arg3, Executable executable){
        step(StringUtils.format(title, arg1, arg2, arg3), executable);
    }

    public static void step(String title, String arg1, String arg2, String arg3, String arg4, Executable executable){
        step(StringUtils.format(title, arg1, arg2, arg3, arg4), executable);
    }

    @SneakyThrows
    public static void step(String title, Executable executable){
        if(currentStep.get() == null)
            currentStep.set(new StepNode());
        startNestedStep(title, "");
        try {
            executable.execute();
        } catch (Throwable e) {
            failedNestedStep(e);
            throw e;
        }
        finishNestedStep();
    }

//    @Before("anyMethod() && withStepAnnotation(step)")
    public static void startNestedStep(final String title, final String description) {
        final StepNode currStep = StepAspect.currentStep.get();
        final StepNode newStep = StepUtils.makeStepNode(title, description, currStep);
        newStep.setStartedOn(new Date());
        currStep.getChildrens().add(newStep);
        StepAspect.currentStep.set(newStep);
    }

//    @AfterReturning(value = "anyMethod() && withStepAnnotation(step)", argNames = "step")
    public static void finishNestedStep() {
        final StepNode currStep = StepAspect.currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.PASSED.getValue());
        StepAspect.currentStep.set(currStep.getParent());
    }

//    @AfterThrowing(value = "anyMethod() && withStepAnnotation(step)", throwing = "throwable", argNames = "step,throwable")
    public static void failedNestedStep(final Throwable throwable) {
        final StepNode currStep = StepAspect.currentStep.get();
        currStep.setCompletedOn(new Date());
        currStep.setOutcome(Outcome.FAILED.getValue());
        StepAspect.currentStep.set(currStep.getParent());
    }

//    @Pointcut("@annotation(addLink)")
    public void withAddLinkAnnotation(final AddLink addLink) {
    }

//    @Pointcut("args(linkItem)")
    public void hasLinkArg(final LinkItem linkItem) {
    }

//    @Before(value = "withAddLinkAnnotation(addLink) && hasLinkArg(linkItem) && anyMethod()", argNames = "addLink, link")
    public void startAddLink(final AddLink addLink, final LinkItem linkItem) {
        final StepNode stepNode = StepAspect.currentStep.get();
        stepNode.getLinkItems().add(linkItem);
    }

    public static void setStepNodes(final StepNode parentNode) {
        StepAspect.previousStep.set(StepAspect.currentStep.get());
        StepAspect.currentStep.set(parentNode);
    }

    public static void returnStepNode() {
        StepAspect.currentStep.set(StepAspect.previousStep.get());
        StepAspect.previousStep.set(StepAspect.currentStep.get());
    }

    static {
        currentStep = new InheritableThreadLocal<>();
        previousStep = new InheritableThreadLocal<>();
    }
}
