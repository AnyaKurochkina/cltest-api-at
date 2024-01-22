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

    public static String iamURL;
    public static String accountManagerURL;
    public static String portalBackURL;
    public static String tarifficatorURL;
    public static String productCatalogURL;
    public static String calculatorURL;
    public static String stateServiceURL;
    public static String orderServiceURL;
    public static String orderServiceAdminURL;
    public static String referencesURL;
    public static String resourceManagerURL;
    public static String feedServiceURL;
    public static String day2ServiceURL;
    public static String imageService;
    public static String dnsService;
    public static String powerDns;
    public static String rpcRouter;
    public static String restrictionServiceUrl;
    public static String budget;
    public static String syncService;
    public static String lucrumService;
    public static String selectorCp;
    public static String selectorInventory;
    public static String serviceManagerProxy;
    public static String auditor;
    public static String sccmManager;
    public static String victoriaProxy;
    public static String suggestionsBack;
    public static String waitingService;
    public static String secretService;
    public static String issueCollectorService;
    public static String calculationManager;
    public static String naming;
    public static String orchestratorWeb;
    public static String tagsService;
    public static String selectorAllocator;
    public static String vcloudConnector;
    public static String s3StorageOld;
    public static String s3StorageNew;
    public static String orchestratorURL;

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
            iamURL = KONG_URL + "iam/api";
            accountManagerURL = KONG_URL + "accountmanager";
            portalBackURL = KONG_URL + "portal/api";
            tarifficatorURL = KONG_URL + "tarifficator/api";
            calculatorURL = KONG_URL + "calculator";
            productCatalogURL = KONG_URL + "product-catalog";
            orderServiceURL = KONG_URL + "order-service/api";
            orchestratorURL = KONG_URL + "orchestrator";
            orderServiceAdminURL = KONG_URL + "order-service/admin/api";
            stateServiceURL = KONG_URL + "state-service";
            referencesURL = KONG_URL + "references";
            resourceManagerURL = KONG_URL + "resource-manager/api";
            feedServiceURL = KONG_URL + "feed-service";
            day2ServiceURL = KONG_URL + "day2-core";
            imageService = KONG_URL + "cloud-images";
            dnsService = KONG_URL + "cloud-dns";
            rpcRouter = KONG_URL + "rpc-django-router";
            restrictionServiceUrl = KONG_URL + "restriction-service";
            budget = KONG_URL + "budget";
            syncService = KONG_URL + "sync-service";
            lucrumService = KONG_URL + "lucrum-service";
            selectorCp = KONG_URL + "selector-cp";
            selectorInventory = KONG_URL + "selector-inventory";
            auditor = KONG_URL + "auditor";
            sccmManager = KONG_URL + "sccm-manager";
            serviceManagerProxy = KONG_URL + "service-manager-proxy";
            victoriaProxy = KONG_URL + "victoria-proxy";
            suggestionsBack = KONG_URL + "suggestions-back";
            waitingService = KONG_URL + "waiting-service";
            secretService = KONG_URL + "secret-service";
            issueCollectorService = KONG_URL + "issue-collector-service";
            calculationManager = KONG_URL + "calculationmanager";
            naming = KONG_URL + "naming";
            orchestratorWeb = KONG_URL + "orchestrator-web";
            tagsService = KONG_URL + "tags-service";
            selectorAllocator = KONG_URL + "selector-allocator";
            vcloudConnector = KONG_URL + "vcloud-connector";
            s3StorageOld = KONG_URL + "storage";
            s3StorageNew = KONG_URL + "storage-new";
            powerDns = getAppProp("url.powerdns");

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
