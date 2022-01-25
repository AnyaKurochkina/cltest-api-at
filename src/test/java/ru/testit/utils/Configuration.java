package ru.testit.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Configuration {
    @EqualsAndHashCode.Include
    String id;
    Map<String, String> confMap;
}
