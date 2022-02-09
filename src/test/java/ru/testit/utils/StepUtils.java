package ru.testit.utils;

//import org.aspectj.lang.reflect.*;
//import org.junit.jupiter.api.AssertAll;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.MultipleFailuresError;
import ru.testit.annotations.*;
import java.lang.reflect.Method;

public class StepUtils
{
    public static StepNode makeStepNode(final String title, final String description, final StepNode currentStep) {
        final StepNode node = new StepNode();
        node.setTitle(title);
        node.setDescription(description);
        node.setParent(currentStep);
        return node;
    }
}
