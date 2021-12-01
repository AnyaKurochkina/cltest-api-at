package ru.testit.junit5;

import core.helper.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import java.lang.reflect.*;
import java.util.Objects;

@Log4j2
public class JUnit5EventListener implements Extension, BeforeAllCallback, AfterAllCallback, InvocationInterceptor, TestWatcher
{
    private static final RunningHandler HANDLER;
    
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
        String className = extensionContext.getRequiredTestMethod().getDeclaringClass().getSimpleName();
        String methodName = extensionContext.getRequiredTestMethod().getName();
        JUnit5EventListener.HANDLER.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), getSubId(extensionContext));
        try {
            invocation.proceed();
        }
        catch (Throwable throwable) {
            JUnit5EventListener.HANDLER.finishTest(extensionContext.getRequiredTestMethod(), throwable, getSubId(extensionContext));
            throw new Exception(throwable.getMessage());
        }
    }

    private String getSubId(final ExtensionContext extensionContext){
        if(!extensionContext.getRequiredTestMethod().isAnnotationPresent(Test.class))
            return StringUtils.findByRegex("#(\\d+)\\]$", extensionContext.getUniqueId());
        return null;
    }

    public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Exception {
        JUnit5EventListener.HANDLER.startTest(extensionContext.getRequiredTestMethod(), extensionContext.getDisplayName(), getSubId(extensionContext));
        try {
            invocation.proceed();
        }
        catch (Throwable throwable) {
            JUnit5EventListener.HANDLER.finishTest(extensionContext.getRequiredTestMethod(), throwable, getSubId(extensionContext));
            throw new Exception(throwable.getMessage());
        }
    }
    
    public void testSuccessful(final ExtensionContext context) {
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), null, getSubId(context));
    }
    
    public void testAborted(final ExtensionContext context, final Throwable cause) {
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), cause, getSubId(context));
    }
    
    public void testFailed(final ExtensionContext context, final Throwable cause) {
        JUnit5EventListener.HANDLER.finishTest(context.getRequiredTestMethod(), cause, getSubId(context));
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
