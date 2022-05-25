//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.junit.jupiter.engine.descriptor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

public class MethodExtensionContext extends AbstractExtensionContext<TestMethodTestDescriptor> {
    private final ThrowableCollector throwableCollector;
    private TestInstances testInstances;

    MethodExtensionContext(ExtensionContext parent, EngineExecutionListener engineExecutionListener, TestMethodTestDescriptor testDescriptor, JupiterConfiguration configuration, ThrowableCollector throwableCollector) {
        super(parent, engineExecutionListener, testDescriptor, configuration);
        this.throwableCollector = throwableCollector;
    }

    public TestMethodTestDescriptor getTestDescriptor2() {
        return this.getTestDescriptor();
    }

    public Optional<AnnotatedElement> getElement() {
        return Optional.of(((TestMethodTestDescriptor)this.getTestDescriptor()).getTestMethod());
    }

    public Optional<Class<?>> getTestClass() {
        return Optional.of(((TestMethodTestDescriptor)this.getTestDescriptor()).getTestClass());
    }

    public Optional<Lifecycle> getTestInstanceLifecycle() {
        return this.getParent().flatMap(ExtensionContext::getTestInstanceLifecycle);
    }

    public Optional<Object> getTestInstance() {
        return this.getTestInstances().map(TestInstances::getInnermostInstance);
    }

    public Optional<TestInstances> getTestInstances() {
        return Optional.ofNullable(this.testInstances);
    }

    void setTestInstances(TestInstances testInstances) {
        this.testInstances = testInstances;
    }

    public Optional<Method> getTestMethod() {
        return Optional.of(((TestMethodTestDescriptor)this.getTestDescriptor()).getTestMethod());
    }

    public Optional<Throwable> getExecutionException() {
        return Optional.ofNullable(this.throwableCollector.getThrowable());
    }
}
