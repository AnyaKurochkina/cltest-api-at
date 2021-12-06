package ru.testit.junit5;

import core.helper.StringUtils;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import ru.testit.services.TestITClient;

import java.lang.reflect.*;
import java.util.Objects;

@Log4j2
public class JUnit5EventListener implements Extension, BeforeAllCallback, AfterAllCallback, InvocationInterceptor, TestWatcher
{
    private static final RunningHandler HANDLER;
    private static final ExtensionContext.Namespace configurationSpace = ExtensionContext.Namespace.create(JUnit5EventListener.class);
    
    public void beforeAll(final ExtensionContext context) {
        JUnit5EventListener.HANDLER.startLaunch();
    }
    
    public void afterAll(final ExtensionContext context) {
        JUnit5EventListener.HANDLER.finishLaunch();
    }
    
    public void interceptBeforeAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        this.startUtilMethod(MethodType.BEFORE_CLASS, invocationContext);
        this.finishUtilMethod(MethodType.BEFORE_CLASS, invocation);
    }
    
    public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        this.startUtilMethod(MethodType.BEFORE_METHOD, invocationContext);
        this.finishUtilMethod(MethodType.BEFORE_METHOD, invocation);
    }
    
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
//        String className = extensionContext.getRequiredTestMethod().getDeclaringClass().getSimpleName();
//        String methodName = extensionContext.getRequiredTestMethod().getName();
        JUnit5EventListener.HANDLER.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), /*getSubId(extensionContext)*/ TestITClient.getConfigurationId());
        extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), TestITClient.getConfigurationId());
        try {
            invocation.proceed();
        }
        catch (Throwable throwable) {
            JUnit5EventListener.HANDLER.finishTest(extensionContext.getRequiredTestMethod(), throwable, /*getSubId(extensionContext)*/ TestITClient.getConfigurationId());
            throw new Exception(throwable.getMessage());
        }
    }

    private String getSubId(final ExtensionContext extensionContext){
        if(!extensionContext.getRequiredTestMethod().isAnnotationPresent(Test.class))
            return StringUtils.findByRegex("#(\\d+)\\]$", extensionContext.getUniqueId());
        return null;
    }

    public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        Entity entity = (Entity) invocationContext.getArguments().stream().filter(o -> Entity.class.isAssignableFrom(o.getClass())).findFirst().orElseThrow(Exception::new);
        JUnit5EventListener.HANDLER.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), entity.getConfigurationId());
        extensionContext.getStore(configurationSpace).put(extensionContext.getUniqueId(), entity.getConfigurationId());
        try {
            invocation.proceed();
        }
        catch (Throwable throwable) {
            JUnit5EventListener.HANDLER.finishTest(extensionContext.getRequiredTestMethod(), throwable, entity.getConfigurationId());
            throw throwable;
        }
    }
    
    public void testSuccessful(final ExtensionContext context) {
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), null, configurationId);
    }
    
    public void testAborted(final ExtensionContext context, final Throwable cause) {
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), cause, configurationId);
    }
    
    public void testFailed(final ExtensionContext context, final Throwable cause) {
        String configurationId = (String) context.getStore(configurationSpace).get(context.getUniqueId());
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), cause, configurationId);
    }
    
    public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        this.startUtilMethod(MethodType.AFTER_METHOD, invocationContext);
        this.finishUtilMethod(MethodType.AFTER_METHOD, invocation);
    }
    
    public void interceptAfterAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        this.startUtilMethod(MethodType.AFTER_CLASS, invocationContext);
        this.finishUtilMethod(MethodType.AFTER_CLASS, invocation);
    }
    
    private void startUtilMethod(final MethodType methodType, final ReflectiveInvocationContext<Method> context) {
        JUnit5EventListener.HANDLER.startUtilMethod(methodType, (Method)context.getExecutable());
    }
    
    private void finishUtilMethod(final MethodType methodType, final Invocation<Void> invocation) throws Exception {
        try {
            invocation.proceed();
            JUnit5EventListener.HANDLER.finishUtilMethod(methodType, null);
        }
        catch (Throwable throwable) {
            JUnit5EventListener.HANDLER.finishUtilMethod(methodType, throwable);
            throw new Exception(throwable.getMessage());
        }
    }
    
    static {
        HANDLER = new RunningHandler();
    }
}
