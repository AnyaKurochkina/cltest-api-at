package clp.core.helpers;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StubAliases {

    private static volatile StubAliases instance;
    private static final String RESOURCE_PATH = StringUtils.concatPath("src", "test", "resources");
    private static final String ALIAS_FILE = "aliases.stub.config.properties";
    private static final String ALIAS_FILE_PATH = StringUtils.concatPathToFile(RESOURCE_PATH, ALIAS_FILE);
    private static final Logger log = LoggerFactory.getLogger(StubAliases.class);
    private static Map<String, StubAliasNames> alias = new HashMap<>();


    private StubAliases() {
    }

    public static StubAliases getInstance() {
        if (instance == null)
            synchronized (StubAliases.class) {
                instance = new StubAliases();
            }
        return instance;
    }

    public void loadApplicationPropertiesForSegment(String path) throws IOException {

        String propertyPath;

        if (System.getProperty(ALIAS_FILE) != null && System.getProperty(ALIAS_FILE).contains("/")) {
            propertyPath = System.getProperty(ALIAS_FILE);
        } else {
            String rootPath = System.getProperty("user.dir");
            String propPath = StringUtils.concatPath(rootPath, RESOURCE_PATH);
            propertyPath = StringUtils.concatPath(propPath, path);
        }
        StringReader aliasBody = new StringReader(new String(Files.readAllBytes(Paths.get(propertyPath)), StandardCharsets.UTF_8));
        CSVParser parcer = CSVFormat.DEFAULT.withDelimiter(';').parse(aliasBody);
        List<CSVRecord> list = parcer.getRecords();
        for (CSVRecord line : list) {
            alias.put(line.get(0), new StubAliasNames(line.get(2),line.get(1),line.get(3)));
        }




        log.info("'{}' loaded successfully!", ALIAS_FILE_PATH);
    }

    public Map<String, StubAliasNames> getAlias() {
        return alias;
    }

    public StubAliasNames getValue(String key) {
        return alias.get(key);
    }
}
