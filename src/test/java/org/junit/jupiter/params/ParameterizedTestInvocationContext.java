package org.junit.jupiter.params;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

public class ParameterizedTestInvocationContext implements TestTemplateInvocationContext {
    private final ParameterizedTestNameFormatter formatter;
    private final ParameterizedTestMethodContext methodContext;
    private final Object[] arguments;

    ParameterizedTestInvocationContext(ParameterizedTestNameFormatter formatter, ParameterizedTestMethodContext methodContext, Object[] arguments) {
        this.formatter = formatter;
        this.methodContext = methodContext;
        this.arguments = arguments;
    }

    public Object[] getArgument(){
        return arguments;
    }

    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(invocationIndex, this.arguments);
    }

    public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new ParameterizedTestParameterResolver(this.methodContext, this.arguments));
    }
}

