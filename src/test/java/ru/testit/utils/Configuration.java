package ru.testit.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Configuration {
    @EqualsAndHashCode.Include
    String id;
    Map<String, String> confMap = new HashMap<>();

    public void setConfMap(Map<String, String> confMap) {
        this.confMap = confMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().trim()));
    }
}
