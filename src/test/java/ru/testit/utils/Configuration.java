package ru.testit.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class Configuration {
    String id;
    Map<String, String> confMap;
}
