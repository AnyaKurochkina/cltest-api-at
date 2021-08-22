package core.helper;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Log4j2
public class Configure {
    private static Properties properties;
    public static String RESOURCE_PATH;
    public static String ENV;

    static {
        try {
            if(System.getProperty("env") == null){
                throw new Exception("Не задан параметр env");
            }
            ENV = System.getProperty("env").toLowerCase();
            log.info("ENV = " + ENV);
            RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
            properties = new Properties();
            loadProperties(RESOURCE_PATH + "/config/application.properties");
            loadProperties(RESOURCE_PATH + "/config/" + ENV + ".properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
