package org.junit;

import org.junit.jupiter.params.provider.ArgumentsSource;
import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(ProductArgumentsProvider.class)
public @interface Source {
    int value();
}
