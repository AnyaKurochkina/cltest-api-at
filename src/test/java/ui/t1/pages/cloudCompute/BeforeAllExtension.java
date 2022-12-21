package ui.t1.pages.cloudCompute;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ru.testit.annotations.BeforeAll;
import ui.extesions.InterceptTestExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BeforeAllExtension implements InvocationInterceptor {
    private static final List<String> runBeforeAll = Collections.synchronizedList(new ArrayList<>());

    @SneakyThrows
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        if (!runBeforeAll.contains(extensionContext.getParent().orElseThrow(Exception::new).getUniqueId())) {
            Method before = Arrays.stream(extensionContext.getRequiredTestClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(BeforeAll.class))
                    .findFirst()
                    .orElseThrow(() -> new Exception("В классе нет методов с BeforeAll"));
            before.setAccessible(true);
            InterceptTestExtension.invoke(before, extensionContext.getRequiredTestInstance(), InterceptTestExtension.getDisplayName(before));
            runBeforeAll.add(extensionContext.getParent().orElseThrow(Exception::new).getUniqueId());
        }
        invocation.proceed();
    }
}
