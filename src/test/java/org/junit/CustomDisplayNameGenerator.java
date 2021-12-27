package org.junit;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Method;

public class CustomDisplayNameGenerator extends DisplayNameGenerator.Standard {
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        String name = super.generateDisplayNameForMethod(testClass, testMethod);
        if (testMethod.isAnnotationPresent(Story.class))
            name = testMethod.getAnnotation(Story.class).value();
        else if (testMethod.isAnnotationPresent(ParameterizedTest.class)) {
            String nameParametrized = testMethod.getAnnotation(ParameterizedTest.class).name();
            int index = nameParametrized.indexOf('{');
            if(index > 0) {
                nameParametrized = nameParametrized.substring(0, index);
                name = nameParametrized;
            }
        }
        return name;
    }
}
