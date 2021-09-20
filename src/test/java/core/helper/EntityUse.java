package core.helper;

import models.Entity;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EntityUses.class)
public @interface EntityUse {
    Class<? extends Entity> c();
    int from() default 0;
    int to() default 0;
}


