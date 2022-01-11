package org.junit;

import core.helper.Configure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class EnvironmentCondition implements ExecutionCondition {

    @SneakyThrows
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final Optional<Method> methodOptional = context.getTestMethod();
        if(!methodOptional.isPresent())
            return ConditionEvaluationResult.enabled("enabled");
        if (methodOptional.orElseThrow(Exception::new).isAnnotationPresent(EnabledIfEnv.class)) {
            String env = methodOptional.orElseThrow(Exception::new).getAnnotation(EnabledIfEnv.class).value();
            if (!Configure.ENV.equals(env))
                return ConditionEvaluationResult.disabled("Тест отключен на стенде " + Configure.ENV);
        }
        return ConditionEvaluationResult.enabled("enabled");
    }
}
