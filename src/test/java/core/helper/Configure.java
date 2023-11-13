package core.helper;

import lombok.extern.log4j.Log4j2;
import models.ObjectPoolService;
import org.junit.TestsExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.TestsExecutionListener.initApiRoutes;
import static org.junit.TestsExecutionListener.loadSecretJson;

@Log4j2
public class Configure {
    private static Properties properties;
    public static String RESOURCE_PATH;
    public static String ENV;
    public static volatile boolean isTestItCreateAutotest = System.getProperty("testItCreateAutotest", "false").equals("true");
    public static String KONG_URL;

    public static String IamURL;
    public static String AccountManagerURL;
    public static String PortalBackURL;
    public static String TarifficatorURL;
    public static String ProductCatalogURL;
    public static String CalculatorURL;
    public static String StateServiceURL;
    public static String OrderServiceURL;
    public static String OrderServiceAdminURL;
    public static String ReferencesURL;
    public static String ResourceManagerURL;
    public static String FeedServiceURL;
    public static String Day2ServiceURL;
    public static String ImageService;
    public static String DNSService;
    public static String PowerDns;
    public static String RpcRouter;
    public static String RestrictionServiceUrl;
    public static String Budget;
    public static String SyncService;
    public static String CdnProxy;

    static {
        try {
            RESOURCE_PATH = new File("src/test/resources").getAbsolutePath();
            properties = new Properties();
            properties.setProperty("testIt", "false");
            if (Objects.nonNull(System.getProperty("moon")))
                properties.setProperty("webdriver.remote.url", System.getProperty("moon"));
            if (Objects.nonNull(System.getProperty("dev.user")))
                properties.setProperty("dev.user", System.getProperty("dev.user"));
            if (Objects.nonNull(System.getProperty("dev.password")))
                properties.setProperty("dev.password", System.getProperty("dev.password"));
            if (Objects.nonNull(System.getProperty("test.user")))
                properties.setProperty("test.user", System.getProperty("test.user"));
            if (Objects.nonNull(System.getProperty("test.password")))
                properties.setProperty("test.password", System.getProperty("test.password"));
            loadProperties(RESOURCE_PATH + "/config/kafka.config.properties");
            loadProperties(RESOURCE_PATH + "/config/application.properties");
            if (System.getProperty("env") == null) {
                if (getAppProp("env") == null) {
                    throw new Exception("Не задан параметр env");
                } else ENV = getAppProp("env").toLowerCase();
            } else
                ENV = System.getProperty("env").toLowerCase();
            log.info("SET ENVIRONMENT = {}", ENV);
            loadProperties(RESOURCE_PATH + "/config/" + ENV + ".properties");
            loadProperties(RESOURCE_PATH + "/config/application.properties");

            KONG_URL = getAppProp("url.kong");
            IamURL = KONG_URL + "iam/api";
            AccountManagerURL = KONG_URL + "accountmanager";
            PortalBackURL = KONG_URL + "portal/api";
            TarifficatorURL = KONG_URL + "tarifficator/api";
            CalculatorURL = KONG_URL + "calculator";
            ProductCatalogURL = KONG_URL + "product-catalog";
            OrderServiceURL = KONG_URL + "order-service/api";
            OrderServiceAdminURL = KONG_URL + "order-service/admin/api";
            StateServiceURL = KONG_URL + "state-service";
            ReferencesURL = KONG_URL + "references";
            ResourceManagerURL = KONG_URL + "resource-manager/api";
            FeedServiceURL = KONG_URL + "feed-service";
            Day2ServiceURL = KONG_URL + "day2-core";
            ImageService = KONG_URL + "cloud-images";
            DNSService = KONG_URL + "cloud-dns";
            RpcRouter = KONG_URL + "rpc-django-router";
            RestrictionServiceUrl = KONG_URL + "restriction-service";
            Budget = KONG_URL + "budget";
            SyncService = KONG_URL + "sync-service";
            CdnProxy = KONG_URL + "cdn-proxy";
            PowerDns = getAppProp("url.powerdns");

            initApiRoutes();
            String fileSecret = Configure.getAppProp("data.folder") + "/shareFolder/" + ((System.getProperty("share") != null) ? System.getProperty("share") : "shareData") + ".json";
            if (Files.exists(Paths.get(fileSecret)))
                ObjectPoolService.loadEntities(DataFileHelper.read(fileSecret));
            loadSecretJson();
            Files.deleteIfExists(Paths.get(TestsExecutionListener.responseTimeLog));
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

    public static boolean isT1() {
        return ENV.toLowerCase().startsWith("t1");
    }

    public static boolean isIntegrationTestIt() {
        return (Configure.getAppProp("testIt").equals("true") || System.getProperty("testRunId") != null);
    }

    public static String getAppProp(String propertyKey) {
        return properties.getProperty(propertyKey);
    }

    public static String getAppProp(String propertyKey, String defaultValue) {
        return properties.getProperty(propertyKey, defaultValue);
    }

    public static Map<String, String> getAppPropStartWidth(String propertyKey) {
        return properties.stringPropertyNames().stream()
                .filter(p -> p.startsWith(propertyKey))
                .collect(Collectors.toMap(p -> p, Configure::getAppProp));
    }

    public static void setAppProp(String propertyKey, String propertyValue) {
        properties.setProperty(propertyKey, propertyValue);
    }
}
