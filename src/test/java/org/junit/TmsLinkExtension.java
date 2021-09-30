package org.junit;

import core.helper.StringUtils;
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
        invocation.proceed();
        if (invocationContext.getExecutable().isAnnotationPresent(TmsLink.class)) {
            String id = invocationContext.getExecutable().getAnnotation(TmsLink.class).value();
            String subId = StringUtils.findByRegex("#(\\d+)\\]$", extensionContext.getUniqueId());
            AllureLifecycle allureLifecycle = getLifecycle();
            Link link = new Link();
            link.setName(id + "." + subId);
            link.setType("tms");
            link.setUrl("");
            allureLifecycle.getCurrentTestCase().ifPresent(i ->
                    allureLifecycle.updateTestCase(i, s -> s.setLinks(Collections.singletonList(link))));
        }
    }
}
