package org.junit;

import core.helper.Configure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public class EnvironmentCondition implements ExecutionCondition {
    @SneakyThrows
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final Optional<Method> methodOptional = context.getTestMethod();
        if (!methodOptional.isPresent())
            return ConditionEvaluationResult.enabled("enabled");
        Method method = methodOptional.orElseThrow(Exception::new);
        Class<?> clazz = method.getDeclaringClass();
        String env = null;
        if (clazz.isAnnotationPresent(EnabledIfEnv.class)) {
            env = clazz.getAnnotation(EnabledIfEnv.class).value();
        }
        if (method.isAnnotationPresent(EnabledIfEnv.class)) {
            env = method.getAnnotation(EnabledIfEnv.class).value();
        }
        if (Objects.nonNull(env)) {
            if (!Configure.ENV.equals(env.toLowerCase()))
                return ConditionEvaluationResult.disabled("Тест отключен на стенде " + Configure.ENV);
        }
        env = null;
        if (clazz.isAnnotationPresent(DisabledIfEnv.class)) {
            env = clazz.getAnnotation(DisabledIfEnv.class).value();
        }
        if (method.isAnnotationPresent(DisabledIfEnv.class)) {
            env = method.getAnnotation(DisabledIfEnv.class).value();
        }
        if (Objects.nonNull(env)) {
            if (Configure.ENV.equals(env.toLowerCase()))
                return ConditionEvaluationResult.disabled("Тест отключен на стенде " + Configure.ENV);
        }
        return ConditionEvaluationResult.enabled("enabled");
    }
}
