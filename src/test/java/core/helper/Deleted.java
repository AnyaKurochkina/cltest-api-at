package core.helper;

import models.Entity;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deleted {
    Class<? extends Entity> value();
}


