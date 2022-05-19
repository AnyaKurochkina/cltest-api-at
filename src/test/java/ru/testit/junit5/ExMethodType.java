package ru.testit.junit5;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ExMethodType {
    @EqualsAndHashCode.Exclude
    MethodType methodType;
    String methodName;
    String testName;
}
