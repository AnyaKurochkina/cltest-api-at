package ru.testit.junit5;

import core.helper.Configure;
import core.helper.http.Http;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.junit.jupiter.api.extension.*;
import ru.testit.services.TestITClient;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static core.helper.Configure.isIntegrationTestIt;

@Log4j2
public class JUnit5EventListener implements Extension, BeforeAllCallback, AfterAllCallback, InvocationInterceptor, TestWatcher {
    public static final RunningHandler HANDLER = new RunningHandler();
    private static final ExtensionContext.Namespace configurationSpace = ExtensionContext.Namespace.create(JUnit5EventListener.class);

    static {
        if (Configure.isIntegrationTestIt())
            JUnit5EventListener.HANDLER.startLaunch();
    }

    public void beforeAll(final ExtensionContext context) {
//        if (!isIntegrationTestIt())
//            return;
//        JUnit5EventListener.HANDLER.startLaunch();
    }

    public void afterAll(final ExtensionContext context) {
//        if (!isIntegrationTestIt())
//            return;
//        JUnit5EventListener.HANDLER.finishLaunch();
    }

    public void interceptBeforeAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        this.startUtilMethod(MethodType.BEFORE_CLASS, invocationContext);
        this.finishUtilMethod(MethodType.BEFORE_CLASS, invocation, extensionContext);
    }

    public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.BEFORE_METHOD, invocationContext);
        this.finishUtilMethod(MethodType.BEFORE_METHOD, invocation, extensionContext);
    }

    @SneakyThrows
    public void trowIfBeforeFail(ExtensionContext extensionContext) {
        ExtensionContext parent = extensionContext.getParent().orElse(null);
        if (Objects.nonNull(parent)) {
            if (classFail.containsKey(parent.getUniqueId())) {
                throw classFail.get(parent.getUniqueId());
            }
        }
        if (classFail.containsKey(extensionContext.getUniqueId())) {
            throw classFail.get(extensionContext.getUniqueId());
        }
    }

    @SneakyThrows
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        if (isIntegrationTestIt()) {
            RunningHandler.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), TestITClient.getConfigurationId(), extensionContext.getTags());
            extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), TestITClient.getConfigurationId());
        }
        try {
            if (Configure.isTestItCreateAutotest)
                invocation.skip();
            else {
                trowIfBeforeFail(extensionContext);
                invocation.proceed();
            }
        } catch (Throwable throwable) {
            if (isIntegrationTestIt())
                RunningHandler.finishTest(extensionContext.getRequiredTestMethod(), throwable, /*getSubId(extensionContext)*/ TestITClient.getConfigurationId());
//            throw new Exception(throwable.getMessage());
            throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

    public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        Entity entity = (Entity) invocationContext.getArguments().stream().filter(o -> Entity.class.isAssignableFrom(o.getClass())).findFirst().orElse(null);
        if (isIntegrationTestIt() && entity != null) {
            RunningHandler.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), entity.getConfigurationId(), extensionContext.getTags());
            extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), entity.getConfigurationId());
        }
        try {
            if (Configure.isTestItCreateAutotest)
                invocation.skip();
            else {
                trowIfBeforeFail(extensionContext);
                invocation.proceed();
            }
        } catch (Throwable throwable) {
            if (isIntegrationTestIt() && entity != null)
                RunningHandler.finishTest(extensionContext.getRequiredTestMethod(), throwable, entity.getConfigurationId());
            throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        //TODO: доделать
    }

    public void testSuccessful(final ExtensionContext context) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), null, configurationId);
    }

    public void testAborted(final ExtensionContext context, final Throwable cause) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), cause, configurationId);
    }

    public void testFailed(final ExtensionContext context, final Throwable cause) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), cause, configurationId);
    }

    public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.AFTER_METHOD, invocationContext);
        this.finishUtilMethod(MethodType.AFTER_METHOD, invocation, extensionContext);
    }

    public void interceptAfterAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.AFTER_CLASS, invocationContext);
        this.finishUtilMethod(MethodType.AFTER_CLASS, invocation, extensionContext);
    }

    private void startUtilMethod(final MethodType methodType, final ReflectiveInvocationContext<Method> context) {
        if (!isIntegrationTestIt())
            return;
        RunningHandler.startUtilMethod(methodType, (Method) context.getExecutable());
    }

    public static final Map<String, Throwable> classFail = new ConcurrentHashMap<>();

    @SneakyThrows
    private void finishUtilMethod(final MethodType methodType, final Invocation<Void> invocation, ExtensionContext context) {
        try {
            if (Configure.isTestItCreateAutotest)
                invocation.skip();
            else
                invocation.proceed();
            if (isIntegrationTestIt())
                RunningHandler.finishUtilMethod(methodType, null);
        } catch (Throwable throwable) {
            if (isIntegrationTestIt())
                RunningHandler.finishUtilMethod(methodType, throwable);
            if(methodType == MethodType.BEFORE_CLASS || methodType == MethodType.BEFORE_METHOD)
                classFail.put(context.getUniqueId(), throwable);
            else throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

}
