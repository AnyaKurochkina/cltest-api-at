package core.helper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static core.helper.Configurier.applicationProperties;

@Log4j2
public class EnvParameters {

    private static volatile EnvParameters instance;
    private static final String RESOURCE_PATH = StringUtils.concatPath("src", "test", "resources", "config");
    private static final String ALIAS_FILE = "td.aliases";
    private static final String ALIAS_FILE_PATH = StringUtils.concatPathToFile(RESOURCE_PATH, ALIAS_FILE);

//    private static final Logger log = LoggerFactory.getLogger(EnvParameters.class);

    public static EnvParameters getInstance() {
        if (instance == null)
            synchronized (EnvParameters.class) {
                instance = new EnvParameters();
            }
        return instance;
    }
    volatile String propertyPath;

    public void loadEnvParams(String path) {

        if (System.getProperty(ALIAS_FILE) != null && System.getProperty(ALIAS_FILE).contains("/")) {
            propertyPath = System.getProperty(ALIAS_FILE);
        } else {
            String rootPath = System.getProperty("user.dir");
            String propPath = StringUtils.concatPath(rootPath, RESOURCE_PATH);
            propertyPath = StringUtils.concatPath(propPath, path);
//            propertyPath = "C:\\Users\\user\\IdeaProjects\\cloud-api-at\\src\\test\\resources\\config\\tdAliasesDEV.csv";
        }
        try {
            StringReader aliasBody = new StringReader(new String(Files.readAllBytes(Paths.get(propertyPath)), StandardCharsets.UTF_8));
            //Получаем содержимое файла построчно.
            CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').parse(aliasBody);
            List<CSVRecord> list = parser.getRecords();
            for (CSVRecord line : list) {
                if (!(line == null)) {
                    log.info("Get From FILE: " + line.get(0) + " - " + line.get(1));
                    applicationProperties.put(line.get(0), line.get(1));
                } else {
                    log.info("CANT READ FROM FILE " + Thread.currentThread());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}