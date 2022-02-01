package ru.testit.properties;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class AppProperties
{
    private final Properties appProps;
    public final static String TEST_IT_TOKEN = "testItToken";
    
    @SneakyThrows
    public AppProperties() {
        final String appConfigPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testit.properties")).getPath();
        this.appProps = new Properties();
        this.appProps.load(new FileInputStream(appConfigPath));
    }
    
    public String getProjectID() {
        return String.valueOf(this.appProps.get("ProjectId"));
    }
    
    public String getUrl() {
        return String.valueOf(this.appProps.get("URL"));
    }
    
    public String getPrivateToken() {
        return String.valueOf(this.appProps.get("PrivateToken"));
    }
    public void setPrivateToken(String token) {
        this.appProps.setProperty("PrivateToken", token);
    }
    
    public String getConfigurationId() {
        return String.valueOf(this.appProps.get("ConfigurationId"));
    }
}
