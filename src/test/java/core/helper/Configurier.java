package core.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.exception.CustomException;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Log4j2
public class Configurier {

    public static Map<String, String> applicationProperties = new HashMap<>();
    private static final String RESOURCE_PATH = StringUtils.concatPath("src", "test", "resources");
    private static final String APP_FILE_NAME = "config/application.properties";
    private static final String APP_FILE_PATH = StringUtils.concatPathToFile(RESOURCE_PATH, APP_FILE_NAME);
    private static final String ENV_FILE_NAME = "config/conf.json";
    private static final String ENV_FILE_PATH = StringUtils.concatPathToFile(RESOURCE_PATH, ENV_FILE_NAME);
    private static volatile Configurier instance;
    private static final String ENVIROMENT = "env";
    private final String enviroment = System.getProperty(ENVIROMENT);

 //   private static final Logger log = LoggerFactory.getLogger(Configurier.class);

    public String getEnviroment() {
        return enviroment;
    }

    private Configurier() {
    }

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

    public Map<String, String> getApplicationProperties() {
        return applicationProperties;
    }

    private String postfix = "";

    public boolean loadApplicationPropertiesForSegment() throws CustomException {
        String propertyPath;
        String envPropertyPath;

        if (System.getProperty(APP_FILE_NAME) != null) {
            propertyPath = System.getProperty(APP_FILE_NAME);
            envPropertyPath = System.getProperty(ENV_FILE_NAME);

            System.out.println("propertyPath =" + propertyPath + "envPropertyPath = " + envPropertyPath);

        } else {
            String rootPath = System.getProperty("user.dir");
            propertyPath = StringUtils.concatPath(rootPath, APP_FILE_PATH);
            envPropertyPath = StringUtils.concatPath(rootPath, ENV_FILE_PATH);
        }
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(propertyPath)) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            log.error("Can't load properties file: " + propertyPath, e);
            throw new CustomException(e);
        }
        if (properties.isEmpty()) {
            return false;
        }

        Map<String, String> envProperty = new HashMap<>();

        if (System.getProperty(ENVIROMENT) != null) {
            postfix = "." + System.getProperty(ENVIROMENT);
            try (FileInputStream fileInputStream = new FileInputStream(envPropertyPath)) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, HashMap<String, String>> confProp = mapper.readValue(fileInputStream, Map.class);
                envProperty.putAll(confProp.get(System.getProperty(ENVIROMENT).toUpperCase()));
            } catch (Exception e) {
                log.error("Can't load env properties file: " + envPropertyPath, e);
                throw new CustomException(e);
            }
        }

        log.debug("'{}' loaded successfully!", APP_FILE_PATH);

        for (String key : properties.stringPropertyNames()) {
            if (properties.getProperty(key).contains("conf." + ENVIROMENT)) {
                String envKey = properties.getProperty(key).split("conf\\." + ENVIROMENT + "\\.")[1];
                applicationProperties.put(key, envProperty.get(envKey));
            } else if (key.contains(postfix)) {
                applicationProperties.put(key.replace(postfix, ""), properties.getProperty(key));
            } else if (!applicationProperties.containsKey(key.replace(postfix, ""))) {
                applicationProperties.put(key, properties.getProperty(key));
            }
        }
        EnvParameters.getInstance().loadEnvParams(getAppProp("td.aliases"));

        return true;
    }

    public String getAppProp(String propertyKey) {
        String valueString = applicationProperties != null ? applicationProperties.get(propertyKey) : null;
        if (valueString == null) {
            log.error("Can't get value for Application key '{}'", propertyKey);
        } else {
            log.info("Key got successfully'{}'", propertyKey);
        }
        return valueString;
    }

    private static String readUsingBufferedReader(String fileName) {
        try {
            StringBuilder stringBuilder = null;
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                assert false;
                stringBuilder.append(line);
            }
            br.close();
            fr.close();

            assert false;
            return stringBuilder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new Error("sdfsdf");
    }
}
