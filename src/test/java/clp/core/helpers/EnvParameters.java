package clp.core.helpers;

import lombok.extern.log4j.Log4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static clp.core.helpers.Configurier.applicationProperties;

@Log4j
public class EnvParameters {

    private static volatile EnvParameters instance;
    private static final String RESOURCE_PATH = StringUtils.concatPath("src", "test", "resources", "config");
    private static final String ALIAS_FILE = "td.aliases";
    private static final String ALIAS_FILE_PATH = StringUtils.concatPathToFile(RESOURCE_PATH, ALIAS_FILE);

    public static EnvParameters getInstance() {
        if (instance == null)
            synchronized (EnvParameters.class) {
                instance = new EnvParameters();
            }
        return instance;
    }

    public static void loadEnvParams(String path) {

        String propertyPath;

        if (System.getProperty(ALIAS_FILE) != null && System.getProperty(ALIAS_FILE).contains("/")) {
            propertyPath = System.getProperty(ALIAS_FILE);
        } else {
            String rootPath = System.getProperty("user.dir");
            String propPath = StringUtils.concatPath(rootPath, RESOURCE_PATH);
            propertyPath = StringUtils.concatPath(propPath, path);
        }
        try {
            StringReader aliasBody = new StringReader(new String(Files.readAllBytes(Paths.get(propertyPath)), StandardCharsets.UTF_8));
            //Получаем содержимое файла построчно.
            CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').parse(aliasBody);
            List<CSVRecord> list = parser.getRecords();
            for (CSVRecord line : list) {
                if (!(line == null)) {
                    applicationProperties.put(line.get(0), line.get(1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
