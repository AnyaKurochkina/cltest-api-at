package org.junit;

import core.helper.StringUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.TmsLink;
import io.qameta.allure.model.Link;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Collections;

import static io.qameta.allure.Allure.getLifecycle;

public class TmsLinkExtension implements InvocationInterceptor {
    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        Allure.story(extensionContext.getParent().orElseThrow(Exception::new).getDisplayName());
        invocation.proceed();
        if (invocationContext.getExecutable().isAnnotationPresent(TmsLink.class)) {
            String id = invocationContext.getExecutable().getAnnotation(TmsLink.class).value();
            String subId = StringUtils.findByRegex("#(\\d+)\\]$", extensionContext.getUniqueId());
            AllureLifecycle allureLifecycle = getLifecycle();
            Link link = new Link();
            link.setName(id + "#" + subId);
            link.setType("tms");
            link.setUrl("");
            allureLifecycle.getCurrentTestCase().ifPresent(i ->
                    allureLifecycle.updateTestCase(i, s -> s.setLinks(Collections.singletonList(link))));
            allureLifecycle.getCurrentTestCase().ifPresent(i ->
                    allureLifecycle.updateTestCase(i, s -> s.setName(extensionContext.getDisplayName().replaceAll("super=\\w+\\(", "("))));
        }
    }

    @Override
    public void interceptAfterEachMethod(InvocationInterceptor.Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                          ExtensionContext extensionContext) throws Throwable {
        invocation.proceed();
//        String className = extensionContext.getTestClass().orElseThrow(Exception::new).getSimpleName();
//        String methodName = extensionContext.getTestMethod().orElseThrow(Exception::new).getName();
//        Allure.tms(className + "#" + methodName, "");
    }
}
