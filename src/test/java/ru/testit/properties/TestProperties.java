package ru.testit.properties;

import core.helper.Configure;
import lombok.extern.log4j.Log4j2;
import ru.testit.junit5.RunningHandler;
import ru.testit.model.response.ConfigurationResponse;
import ru.testit.services.TestITClient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class TestProperties
{
    private List<String> testProps = new ArrayList<>();
    final private Map<String, ConfigurationResponse> configurations = new HashMap<>();
    private static volatile TestProperties instance;

    public static TestProperties getInstance() {
        TestProperties localInstance = instance;
        if (localInstance == null) {
            synchronized (TestProperties.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TestProperties();
                }
            }
        }
        return localInstance;
    }


    private TestProperties() {
        try {
            final String testConfigPath = Configure.RESOURCE_PATH + "/configurations.txt";
            if (Files.exists(Paths.get(testConfigPath))) {
                testProps = Files.readAllLines(Paths.get(testConfigPath));
                TestITClient client = new TestITClient();
                for(String line : testProps){
                    String[] parseLine = line.split("=");
                    if(!configurations.containsKey(parseLine[0])) {
                        ConfigurationResponse response = client.getConfiguration(parseLine[1].trim());
                        configurations.put(parseLine[0], response);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public Map<String, String> getConfigMap(Method method){
        return configurations.get(RunningHandler.extractExternalID(method, null)).getCapabilities();
    }
    
    private List<String> getConfigurationIds(String externalId) {
        List<String> list = testProps.stream().filter(t -> t.startsWith(externalId + "=")).collect(Collectors.toList());
        if(list.isEmpty())
            list = Collections.singletonList(TestITClient.properties.getConfigurationId());
        return list;
    }

}
