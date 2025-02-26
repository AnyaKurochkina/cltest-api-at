package ru.testit.junit5;

import core.helper.Configure;
import core.helper.http.Http;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.engine.descriptor.MethodExtensionContext;
import org.junit.jupiter.engine.descriptor.TestTemplateInvocationTestDescriptor;
import org.junit.jupiter.params.ParameterizedTestInvocationContext;
import org.opentest4j.TestAbortedException;
import ru.testit.properties.TestProperties;
import ru.testit.services.TestITClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static core.helper.Configure.isIntegrationTestIt;

@Log4j2
public class JUnit5EventListener implements Extension, BeforeAllCallback, AfterAllCallback, InvocationInterceptor, TestWatcher, BeforeTestExecutionCallback {
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
        this.startUtilMethod(MethodType.BEFORE_CLASS, invocationContext, extensionContext);
        this.finishUtilMethod(MethodType.BEFORE_CLASS, invocation, extensionContext, invocationContext);
    }

    public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.BEFORE_METHOD, invocationContext, extensionContext);
        this.finishUtilMethod(MethodType.BEFORE_METHOD, invocation, extensionContext, invocationContext);
    }


    @SneakyThrows
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
//        if (isIntegrationTestIt()) {
//            RunningHandler.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), TestITClient.getConfigurationId(), extensionContext.getTags());
//            extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), TestITClient.getConfigurationId());
//        }
        try {
            if (Configure.isTestItCreateAutotest)
                invocation.skip();
            else {
//                trowIfBeforeFail(extensionContext);
                invocation.proceed();
            }
        } catch (Throwable throwable) {
//            if (isIntegrationTestIt())
//                RunningHandler.finishTest(extensionContext.getRequiredTestMethod(), throwable, /*getSubId(extensionContext)*/ TestITClient.getConfigurationId());
//            throw new Exception(throwable.getMessage());
//            if(Objects.nonNull(throwable.getCause()))
//                throw throwable.getCause();
//            else
                throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

//    @SneakyThrows
//    public void trowIfBeforeFail(ExtensionContext extensionContext) {
//        if (testFail.containsKey(extensionContext.getUniqueId())) {
//            throw testFail.get(extensionContext.getUniqueId());
//        }
//    }

    public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
//        Entity entity = (Entity) invocationContext.getArguments().stream().filter(o -> Entity.class.isAssignableFrom(o.getClass())).findFirst().orElse(null);
//        if (isIntegrationTestIt() && entity != null) {
//            RunningHandler.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), entity.getConfigurationId(), extensionContext.getTags());
//            extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), entity.getConfigurationId());
//        }
        try {
            if (Configure.isTestItCreateAutotest)
                invocation.skip();
            else {
//                trowIfBeforeFail(extensionContext);
                invocation.proceed();
            }
        } catch (Throwable throwable) {
//            if (isIntegrationTestIt() && entity != null)
//                RunningHandler.finishTest(extensionContext.getRequiredTestMethod(), throwable, entity.getConfigurationId());
            throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        if (!isIntegrationTestIt())
            return;
        List<String> list = TestProperties.getInstance()
                .getConfigurationIds(RunningHandler.extractExternalID(context.getRequiredTestMethod(), null));

        for (String configuration : list) {
            RunningHandler.startTest(context.getRequiredTestMethod(), context.getDisplayName(), configuration, context.getTags());
            RunningHandler.finishTest(context.getRequiredTestMethod(), new TestAbortedException(reason.orElse("Тест отключен")), configuration, context);
            RunningHandler.endTest(context.getRequiredTestMethod(), configuration);
        }
    }

    public void testSuccessful(final ExtensionContext context) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), null, configurationId, context);
        RunningHandler.endTest(context.getRequiredTestMethod(), configurationId);
    }

    public void testAborted(final ExtensionContext context, final Throwable cause) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), cause, configurationId, context);
        RunningHandler.endTest(context.getRequiredTestMethod(), configurationId);
    }

    public void testFailed(final ExtensionContext context, Throwable cause) {
        if (!isIntegrationTestIt())
            return;
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        RunningHandler.finishTest(context.getRequiredTestMethod(), cause, configurationId, context);
        RunningHandler.endTest(context.getRequiredTestMethod(), configurationId);
    }

    public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.AFTER_METHOD, invocationContext, extensionContext);
        this.finishUtilMethod(MethodType.AFTER_METHOD, invocation, extensionContext, invocationContext);
    }

    public void interceptAfterAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        this.startUtilMethod(MethodType.AFTER_CLASS, invocationContext, extensionContext);
        this.finishUtilMethod(MethodType.AFTER_CLASS, invocation, extensionContext, invocationContext);
    }

    private void startUtilMethod(final MethodType methodType, final ReflectiveInvocationContext<Method> context, ExtensionContext extensionContext) {
        if (!isIntegrationTestIt())
            return;
        RunningHandler.startUtilMethod(methodType, (Method) context.getExecutable(), extensionContext);
    }

//    private static final Map<String, Throwable> testFail = new ConcurrentHashMap<>();

    @SneakyThrows
    private void finishUtilMethod(final MethodType methodType, final Invocation<Void> invocation, ExtensionContext context, final ReflectiveInvocationContext<Method> invocationContext) {
        String testName = "";
        if (methodType.equals(MethodType.BEFORE_METHOD) || methodType.equals(MethodType.AFTER_METHOD))
            testName = context.getUniqueId();
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
//            if(methodType == MethodType.BEFORE_METHOD)
//                testFail.put(context.getUniqueId(), throwable);
            throw throwable;
        } finally {
            Http.removeFixedRole();
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        if (isIntegrationTestIt()) {
            String configurationId = TestITClient.getConfigurationId();
            if (extensionContext.getUniqueId().contains("test-template-invocation:")) {
                TestTemplateInvocationTestDescriptor testDescriptor =
                        (TestTemplateInvocationTestDescriptor) ((MethodExtensionContext) extensionContext).getTestDescriptor2();
                Field field;
                try {
                    field = TestTemplateInvocationTestDescriptor.class.getDeclaredField("invocationContext");
                    field.setAccessible(true);
                    ParameterizedTestInvocationContext in = (ParameterizedTestInvocationContext) field.get(testDescriptor);
                    Entity entity = (Entity) Arrays.stream(in.getArgument()).filter(o ->
                            Entity.class.isAssignableFrom(o.getClass())).findFirst().orElseThrow(Exception::new);
                    configurationId = entity.getConfigurationId();
                } catch (Exception e) {
                    log.error("beforeTestExecution()", e);
                }
            }
            RunningHandler.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), configurationId, extensionContext.getTags());
            extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), configurationId);
        }
    }

//    @Override
//    public void afterEach(ExtensionContext extensionContext) throws Exception {
//        String configurationId = (String) extensionContext.getStore(configurationSpace).get(extensionContext.getUniqueId());
//        RunningHandler.endTest(extensionContext);
//    }
}
