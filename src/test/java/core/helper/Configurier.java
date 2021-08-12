package core.helper;

import core.exception.CustomException;
import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Log4j2
public class Configurier {
    private static final String RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
    private static final String CONFIG_PATH = "/config/";
    private static volatile Configurier instance;
    private final String enviroment = System.getProperty("env").toLowerCase();
    private static final Properties properties = new Properties();

    private Configurier() {}

    public static Configurier getInstance() {
        if (instance == null)
            synchronized (Configurier.class) {
                instance = new Configurier();
                try {
                    instance.loadApplicationPropertiesForSegment();
                } catch (CustomException e) {
                    log.error(e.getMessage());
                }
            }
        return instance;
    }

    public void loadApplicationPropertiesForSegment() throws CustomException {
        try (FileInputStream fileInputStream = new FileInputStream(RESOURCE_PATH + CONFIG_PATH + "application.properties")) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            log.error("Can't load properties file: " + e);
            throw new CustomException(e);
        }
        try (FileInputStream fileInputStream = new FileInputStream(RESOURCE_PATH + CONFIG_PATH + enviroment + ".properties")) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            log.error("Can't load enviroment properties file : "+ e);
            throw new CustomException(e);
        }
    }

    public String getAppProp(String propertyKey) {
        String valueString = properties.getProperty(propertyKey);
        if (valueString == null) {
            log.error("Can't get value for Application key '{}'", propertyKey);
        }
        return valueString;
    }
}
