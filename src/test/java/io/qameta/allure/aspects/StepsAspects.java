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
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.AspectUtils;
import io.qameta.allure.util.ResultsUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import tests.Tests;

import java.util.List;
import java.util.UUID;

import static ru.testit.junit5.StepsAspects.*;

@Aspect
public class StepsAspects {
    private static final InheritableThreadLocal<AllureLifecycle> LIFECYCLE = new InheritableThreadLocal<AllureLifecycle>() {
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

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
        Step step = methodSignature.getMethod().getAnnotation(Step.class);
        String uuid = UUID.randomUUID().toString();
        String name = AspectUtils.getName(step.value(), joinPoint);
        List<Parameter> parameters = AspectUtils.getParameters(methodSignature, joinPoint.getArgs());
        StepResult result = (new StepResult()).setName(name).setParameters(parameters);
        getLifecycle().startStep(uuid, result);
        startNestedStep(name, "");
    }

    @AfterThrowing(
            pointcut = "anyMethod() && withStepAnnotation()",
            throwing = "e"
    )
    public void stepFailed(Throwable e) {
        getLifecycle().updateStep((s) -> s.setStatus(ResultsUtils.getStatus(e).orElse(Status.BROKEN)).setStatusDetails(ResultsUtils.getStatusDetails(e).orElse(null)));
        getLifecycle().stopStep();
        failedNestedStep();
    }

    @AfterReturning(
            pointcut = "anyMethod() && withStepAnnotation()"
    )
    public void stepStop() {
        getLifecycle().updateStep((s) -> s.setStatus(Status.PASSED));
        getLifecycle().stopStep();
        finishNestedStep();
    }

    public static void setLifecycle(AllureLifecycle allure) {
        LIFECYCLE.set(allure);
    }

    public static AllureLifecycle getLifecycle() {
        return LIFECYCLE.get();
    }

}
