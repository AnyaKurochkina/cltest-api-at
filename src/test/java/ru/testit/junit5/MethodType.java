package ru.testit.junit5;

import java.lang.annotation.*;
import org.junit.jupiter.api.*;

public enum MethodType
{
    TEST((Class<? extends Annotation>)Test.class), 
    BEFORE_METHOD((Class<? extends Annotation>)BeforeEach.class), 
    AFTER_METHOD((Class<? extends Annotation>)AfterEach.class), 
    BEFORE_CLASS((Class<? extends Annotation>)BeforeAll.class), 
    AFTER_CLASS((Class<? extends Annotation>)AfterAll.class);
    
    private Class<? extends Annotation> clazz;
    
    private MethodType(final Class<? extends Annotation> markerAnnotation) {
        this.clazz = markerAnnotation;
    }
}
