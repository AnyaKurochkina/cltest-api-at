package ui.t1.tests;

import core.enums.Role;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface WithAuthorization {

    Role value() default Role.SUPERADMIN;
}
