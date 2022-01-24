package ru.testit.properties;

import core.helper.Configure;
import lombok.extern.log4j.Log4j2;
import ru.testit.junit5.RunningHandler;
import ru.testit.model.response.ConfigurationResponse;
import ru.testit.services.TestITClient;
import ru.testit.utils.Configuration;
import ru.testit.utils.UniqueTest;

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
    final private Map<UniqueTest, ConfigurationResponse> configurations = new HashMap<>();
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
                    UniqueTest uniqueTest = new UniqueTest(parseLine[0].trim(), parseLine[1].trim());
                    if(!configurations.containsKey(uniqueTest)) {
                        ConfigurationResponse response = client.getConfiguration(uniqueTest.getConfigurationId());
                        configurations.put(uniqueTest, response);
                    }
                    else log.error("{} уже существует", uniqueTest);
                }
                ConfigurationResponse response = client.getConfiguration(TestITClient.properties.getConfigurationId());
                configurations.put(new UniqueTest("default", TestITClient.properties.getConfigurationId()), response);
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public List<Configuration> getConfigMapsByTest(Method method){
        String externalId = RunningHandler.extractExternalID(method, null);
        List<String> configurationIds = getConfigurationIds(externalId);
        List<Configuration> configurationList = new ArrayList<>();
        for(String id : configurationIds){
            UniqueTest uniqueTest = new UniqueTest(externalId, id);
            Configuration configuration = new Configuration();
            configuration.setId(id);
            if(configurations.containsKey(uniqueTest)) {
                configuration.setConfMap(configurations.get(uniqueTest).getCapabilities());
            }
            else {
                configuration.setConfMap(configurations.get(new UniqueTest("default", TestITClient.properties.getConfigurationId())).getCapabilities());
            }
            configurationList.add(configuration);
        }
        return configurationList;
    }
    
    private List<String> getConfigurationIds(String externalId) {
        List<String> list = testProps.stream().filter(t -> t.startsWith(externalId + "=")).map(t -> t.split("=")[1]).collect(Collectors.toList());
        if(list.isEmpty())
            list = Collections.singletonList(TestITClient.properties.getConfigurationId());
        return list;
    }

}
