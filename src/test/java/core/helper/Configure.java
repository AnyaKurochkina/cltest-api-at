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
    public static volatile boolean isTestItCreateAutotest = System.getProperty("testItCreateAutotest", "false").equals("true");

    public static String IamURL;
    public static String AccountManagerURL;
    public static String PortalBackURL;
    public static String TarifficatorURL;
    public static String ProductCatalogURL;
    public static String CalculatorURL;
    public static String StateServiceURL;
    public static String OrderServiceURL;
    public static String ReferencesURL;

    static {
        try {
            RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
            properties = new Properties();

            properties.setProperty("testIt", "false");

            loadProperties(RESOURCE_PATH + "/config/application.properties");
            loadProperties(RESOURCE_PATH + "/config/kafka.config.properties");
            if (System.getProperty("env") == null) {
                if (getAppProp("env") == null) {
                    throw new Exception("Не задан параметр env");
                } else ENV = getAppProp("env").toLowerCase();
            } else
                ENV = System.getProperty("env").toLowerCase();
            log.info("SET ENVIRONMENT = " + ENV);
            loadProperties(RESOURCE_PATH + "/config/" + ENV + ".properties");

            String kongURL = getAppProp("url.kong");
            IamURL = kongURL + "iam/api";
            AccountManagerURL = kongURL + "accountmanager";
            PortalBackURL = kongURL + "portal/api";
            TarifficatorURL = kongURL + "tarifficator/api";
            CalculatorURL = kongURL + "calculator";
            ProductCatalogURL = kongURL + "product-catalog/api/v1/";
            OrderServiceURL = kongURL + "order-service/api";
            StateServiceURL = kongURL + "state-service";
            ReferencesURL = kongURL + "references";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties(String file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            log.warn("Can't load environment properties file : " + e.getMessage());
        }
    }

    public static boolean isIntegrationTestIt() {
        return (Configure.getAppProp("testIt").equals("true") || System.getProperty("testRunId") != null);
    }

    public static String getAppProp(String propertyKey) {
        return properties.getProperty(propertyKey);
    }

    public static void setAppProp(String propertyKey, String propertyValue) {
        properties.setProperty(propertyKey, propertyValue);
    }
}
