package ui.uiExtesions;

import core.exception.CreateEntityException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CustomBeforeAllAndAfterAll implements Extension, InvocationInterceptor, TestExecutionListener, AfterEachCallback {
    private static final List<Test> allTests = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, Throwable> runBeforeAllTests = Collections.synchronizedMap(new HashMap<>());
    private static final ExtensionContext.Namespace runAfterAllSpace = ExtensionContext.Namespace.create(AfterAll.class);
    private static final ExtensionContext.Namespace finalAfterEach = ExtensionContext.Namespace.create(CustomBeforeAllAndAfterAll.class);

    @Override
    public void interceptBeforeAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) {
        invocation.skip();
    }

    @Override
    public void interceptAfterAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) {
        invocation.skip();
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        checkErrorBeforeAll(extensionContext);
        invocation.proceed();
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        checkErrorBeforeAll(extensionContext);
        invocation.proceed();
    }

    public void checkErrorBeforeAll(ExtensionContext extensionContext) {
        String className = extensionContext.getRequiredTestClass().getName();
        if (runBeforeAllTests.containsKey(className)) {
            if (Objects.nonNull(runBeforeAllTests.get(className))) {
                throw new BeforeAllException(runBeforeAllTests.get(className));
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        String className = extensionContext.getRequiredTestClass().getName();
        Test test = new Test(className, extensionContext.getRequiredTestMethod().getName());
        extensionContext.getStore(finalAfterEach).put(test.toString(), true);
    }

    @Override
    public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        Test test = new Test(extensionContext.getRequiredTestClass().getName(), extensionContext.getRequiredTestMethod().getName());
        if (runBeforeAllTests.containsKey(test.getClassName())) {
            if (Objects.nonNull(runBeforeAllTests.get(test.getClassName()))) {
                invocation.skip();
                return;
            }
        }
        if (runBeforeAllTests.containsKey(test.getClassName())) {
            allTests.stream()
                    .filter(t -> t.equals(test))
                    .findFirst().orElseThrow(Exception::new).setRun(true);
            invocation.proceed();
            return;
        }
        if (allTests.stream()
                .filter(t -> t.getClassName().equals(extensionContext.getRequiredTestClass().getName()))
                .noneMatch(Test::isRun)) {
            allTests.stream()
                    .filter(t -> t.equals(test))
                    .findFirst().orElseThrow(Exception::new).setRun(true);
            runBeforeAllTests.put(test.getClassName(), null);
            try {
                runBeforeAll(extensionContext, invocationContext);
            } catch (Throwable e) {
                invocation.skip();
                runBeforeAllTests.put(test.getClassName(), e);
                throw e;
            }
        }
        else
            allTests.stream()
                    .filter(t -> t.equals(test))
                    .findFirst().orElseThrow(Exception::new).setRun(true);
        invocation.proceed();
    }

    @Override
    public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        String className = extensionContext.getRequiredTestClass().getName();
        Test test = new Test(className, extensionContext.getRequiredTestMethod().getName());

        if (extensionContext.getStore(finalAfterEach).getOrDefault(test.toString(), Boolean.class, false)) {
            invocation.proceed();
            return;
        }

        if (runBeforeAllTests.containsKey(className)) {
            if (Objects.nonNull(runBeforeAllTests.get(className))) {
                invocation.skip();
                return;
            }
        }
        if (extensionContext.getStore(runAfterAllSpace).getOrDefault(test.toString(), Boolean.class, false)) {
            invocation.proceed();
            return;
        }
        Throwable throwable = null, throwableAll = null;
        try {
            invocation.proceed();
        } catch (Throwable e) {
            throwable = e;
        }
        try {
            if (allTests.stream()
                    .filter(t -> t.getClassName().equals(className))
                    .allMatch(Test::isRun)) {
                extensionContext.getStore(runAfterAllSpace).put(test.toString(), true);
                runAfterAll(extensionContext, invocationContext);
            }
        } catch (Throwable e) {
            throwableAll = e;
        }
        if (Objects.nonNull(throwable))
            throw throwable;
        if (Objects.nonNull(throwableAll))
            throw throwableAll;
    }

    private void runBeforeAll(ExtensionContext extensionContext, final ReflectiveInvocationContext<Method> context) throws Throwable {
        for (Method m : extensionContext.getRequiredTestClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(BeforeAll.class)) {
                try {
                    m.setAccessible(true);
                    m.invoke(context.getTarget().orElseThrow(Exception::new));
                } catch (Throwable e) {
                    throw e.getCause();
                }
            }
        }
    }

    private void runAfterAll(ExtensionContext extensionContext, final ReflectiveInvocationContext<Method> context) throws Throwable {
        for (Method m : extensionContext.getRequiredTestClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(AfterAll.class)) {
                try {
                    m.setAccessible(true);
                    m.invoke(context.getTarget().orElseThrow(Exception::new));
                } catch (Throwable e) {
                    throw e.getCause();
                }
            }
        }
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        String root = ((TestIdentifier) testPlan.getRoots().toArray()[0]).getUniqueId();
        getChildren(testPlan, root).stream().distinct().forEach(s -> {
            if (testPlan.getTestIdentifier(s).isTest()) {
                if (testPlan.getTestIdentifier(s).getSource().isPresent()) {
                    MethodSource methodSource = ((MethodSource) testPlan.getTestIdentifier(s).getSource().get());
                    allTests.add(new Test(methodSource));
                }
            }
        });
    }

    public List<String> getChildren(TestPlan testPlan, String id) {
        List<String> children = testPlan.getChildren(id).stream().map(TestIdentifier::getUniqueId).collect(Collectors.toList());
        for (String i : testPlan.getChildren(id).stream().map(TestIdentifier::getUniqueId).collect(Collectors.toList())) {
            children.addAll(getChildren(testPlan, i));
        }
        return children;
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    private static class Test {
        String className;
        String testName;
        @Setter
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        boolean run;

        public Test(MethodSource method) {
            this.className = method.getJavaClass().getName();
            this.testName = method.getMethodName();
        }

        public Test(String className, String testName) {
            this.className = className;
            this.testName = testName;
        }
    }

    static class BeforeAllException extends CreateEntityException{
        public BeforeAllException(Throwable e) {
            super(e.toString());
            this.setStackTrace(e.getStackTrace());
        }
    }
}
