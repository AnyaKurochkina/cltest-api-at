package ru.testit.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Link {
    String url();
    
    String title() default "";
    
    String description() default "";
    
    LinkType type();
}
