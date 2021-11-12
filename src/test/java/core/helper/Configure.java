package core.helper;

import lombok.extern.log4j.Log4j2;
import steps.authorizer.AuthorizerSteps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Log4j2
public class Configure {
    private static Properties properties;
    public static String RESOURCE_PATH;
    public static String ENV;

    public static String AuthorizerURL;
    public static String AccountManagerURL;
    public static String PortalBackURL;
    public static String TarifficatorURL;
    public static String ProductCatalog;

    static {
        try {
            RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
            properties = new Properties();
            loadProperties(RESOURCE_PATH + "/config/application.properties");
            if (System.getProperty("env") == null) {
                if (getAppProp("env") == null) {
                    throw new Exception("Не задан параметр env");
                } else ENV = getAppProp("env").toLowerCase();
            } else
                ENV = System.getProperty("env").toLowerCase();
            log.info("ENV = " + ENV);
            loadProperties(RESOURCE_PATH + "/config/" + ENV + ".properties");

            String kongURL = getAppProp("host_kong");
            AuthorizerURL = kongURL + "authorizer/api/v1/";
            AccountManagerURL = kongURL + "accountmanager/api/v1/";
            PortalBackURL = kongURL + "portal/api/v1/";
            TarifficatorURL = kongURL + "tarifficator/api/v1/";
            ProductCatalog = kongURL + "product-catalog/";

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
