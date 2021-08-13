package core.helper;

import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Log4j2
public class Configure {
    private static final String RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
    private static final String env = System.getProperty("env").toLowerCase();
    private static final Properties properties = new Properties();

    static {
        loadProperties(RESOURCE_PATH + "/config/application.properties");
        loadProperties(RESOURCE_PATH + "/config/" + env + ".properties");
    }

    private static void loadProperties(String file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            log.error("Can't load environment properties file : " + e.getMessage());
        }
    }

    public static String getAppProp(String propertyKey) {
        String valueString = properties.getProperty(propertyKey);
        if (valueString == null)
            log.error("Can't get value for Application key '{}'", propertyKey);
        return valueString;
    }
}
