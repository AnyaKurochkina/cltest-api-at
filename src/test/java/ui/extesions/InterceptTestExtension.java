package ui.extesions;

import core.exception.CreateEntityException;
import core.helper.Configure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.junit.BlockTests;
import org.junit.IgnoreInterceptTestExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import ru.testit.junit5.RunningHandler;
import ru.testit.junit5.StepsAspects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.getLifecycle;

@Log4j2
public class InterceptTestExtension implements InvocationInterceptor, TestExecutionListener {
    private static final List<String> runBeforeAll = Collections.synchronizedList(new ArrayList<>());
    private static final List<Test> allTests = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, Throwable> failClass = new ConcurrentHashMap<>();

    @SneakyThrows
    private boolean isIgnoreIntercept(ExtensionContext extensionContext, String method) {
        return Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(method))
                .findFirst()
                .orElseThrow(() -> new Exception("Не найден тест " + method)).isAnnotationPresent(IgnoreInterceptTestExtension.class);
    }

    @SneakyThrows
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) {
        boolean isOnlyIgnoredTests = allTests.stream().filter(test -> test.getClassName().equals(extensionContext.getRequiredTestClass().getName()))
                .allMatch(test -> isIgnoreIntercept(extensionContext, test.getTestName()));

        Method before = Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Order.class))
                .min(Comparator.comparingInt(o -> o.getAnnotation(Order.class).value()))
                .orElseThrow(() -> new Exception("В классе нет методов с Order"));

        //если бефор в классе уже запускался
        if (!runBeforeAll.contains(extensionContext.getParent().orElseThrow(Exception::new).getUniqueId())) {
            //если первый тест не будет запущен
            if (allTests.stream().noneMatch(test -> test.getClassName().equals(before.getDeclaringClass().getName()) && test.getTestName().equals(before.getName())) && !isOnlyIgnoredTests) {
                before.setAccessible(true);
                try {
                    invoke(before, extensionContext.getRequiredTestInstance(), getDisplayName(before));
                    runEachMethod(extensionContext, AfterEach.class);
                    runEachMethod(extensionContext, BeforeEach.class);
                } catch (Throwable e) {
                    invocation.skip();
                    if (Objects.nonNull(e.getCause()))
                        throw e.getCause();
                    throw e;
                } finally {
                    runBeforeAll.add(extensionContext.getParent().orElseThrow(Exception::new).getUniqueId());
                }
            }
        }

        allTests.stream().filter(test -> test.getClassName().equals(extensionContext.getRequiredTestClass().getName()) &&
                        test.getTestName().equals(extensionContext.getRequiredTestMethod().getName())).findFirst()
                .orElseThrow(() -> new Exception("Текущий тест не найден в allTests")).setRun(true);

        Throwable testThrow = null;
        try {
            if (failClass.containsKey(extensionContext.getRequiredTestClass().getName()))
                throw new CreateEntityException(failClass.get(extensionContext.getRequiredTestClass().getName()));
            invocation.proceed();
        } catch (Throwable e) {
            if (extensionContext.getRequiredTestClass().getName().equals(before.getDeclaringClass().getName())
                    && extensionContext.getRequiredTestMethod().getName().equals(before.getName())
                    && extensionContext.getRequiredTestClass().isAnnotationPresent(BlockTests.class))
                failClass.put(extensionContext.getRequiredTestClass().getName(), e);
            testThrow = e;
        }

        //если все тесты в классе были запущены
        if (allTests.stream().filter(test -> test.getClassName().equals(extensionContext.getRequiredTestClass().getName())).allMatch(Test::isRun)) {
            Method after = Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Order.class))
                    .max(Comparator.comparingInt(o -> o.getAnnotation(Order.class).value()))
                    .orElseThrow(() -> new Exception("В классе нет методов с Order"));
            if (allTests.stream().noneMatch(test -> test.getClassName().equals(after.getDeclaringClass().getName()) && test.getTestName().equals(after.getName())) && !isOnlyIgnoredTests) {
                after.setAccessible(true);
                try {
                    runEachMethod(extensionContext, AfterEach.class);
                    runEachMethod(extensionContext, BeforeEach.class);
                    invoke(after, extensionContext.getRequiredTestInstance(), getDisplayName(after));
                } catch (Throwable e) {
                    Throwable throwable = e;
                    if (Objects.nonNull(e.getCause()))
                        throwable = e.getCause();
                    if (Objects.nonNull(testThrow))
                        testThrow.addSuppressed(throwable);
                    else
                        testThrow = throwable;
                    throw testThrow;
                }
            }
        }
        if (Objects.nonNull(testThrow))
            throw testThrow;
    }

    public static String getDisplayName(Method before) {
        String displayName = before.getName();
        if (before.isAnnotationPresent(DisplayName.class))
            displayName = before.getAnnotation(DisplayName.class).value();
        return displayName;
    }

    @Step
    public static void invoke(Method method, Object obj, String title) throws Throwable {
        modifyStep(title);
        method.invoke(obj);
    }

    public static void modifyStep(String name) {
        AllureLifecycle allureLifecycle = getLifecycle();
        String id = allureLifecycle.getCurrentTestCaseOrStep().orElse(null);
        if (Objects.nonNull(id)) {
            allureLifecycle.updateStep(id, s -> s.setName(name));
        }
        if (Configure.isIntegrationTestIt()) {
            if (StepsAspects.getCurrentStep().get() != null) {
                StepsAspects.getCurrentStep().get().setTitle(name);
                log.debug(name);
            }
        }
    }

    public void runEachMethod(final ExtensionContext extensionContext, Class<? extends Annotation> clazz) throws Throwable {
        if (clazz.equals(AfterEach.class))
            new ConfigExtension().afterEach(extensionContext);
        if (clazz.equals(BeforeEach.class))
            new ConfigExtension().beforeEach(extensionContext);
        List<Method> list = Arrays.stream(extensionContext.getRequiredTestClass().getMethods()).filter(method -> method.isAnnotationPresent(clazz)).collect(Collectors.toList());
        for (Method m : list) {
            try {
                m.setAccessible(true);
                invoke(m, extensionContext.getRequiredTestInstance(), RunningHandler.extractTitle(m));
            } catch (Throwable e) {
                if (Objects.nonNull(e.getCause()))
                    throw e.getCause();
                else
                    throw e;
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
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString(onlyExplicitlyIncluded = true)
    private static class Test {
        @ToString.Include
        @EqualsAndHashCode.Include
        String className;
        @ToString.Include
        @EqualsAndHashCode.Include
        String testName;
        Integer order;
        @Setter
        boolean run;

        public Test(MethodSource method) {
            this.className = method.getJavaClass().getName();
            this.testName = method.getMethodName();
            if (method.getJavaMethod().isAnnotationPresent(Order.class))
                this.order = method.getJavaMethod().getAnnotation(Order.class).value();
        }
    }

}