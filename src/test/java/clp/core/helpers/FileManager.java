package clp.core.helpers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.exception.CustomException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 *
 */

public class FileManager {
    private static final Logger LOG = LoggerFactory.getLogger(FileManager.class);

    private FileManager() {
    }

    public static File getFileFromPath(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            return file;
        } else {
            file = null;
        }
        ClassLoader classLoader = FileManager.class.getClassLoader();
        URL fileURL = classLoader.getResource(path);
        if (fileURL != null) {
            file = new File(fileURL.getFile());
            if (!file.exists()) {
                throw new FileNotFoundException("Can't find file from path: " + path);
            }
        }
        return file;
    }

    public static String getTextFileAsString(File textFile) throws CustomException {
        StringBuilder strBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8);
                BufferedReader bufferReader = new BufferedReader(inputStreamReader)
        ) {
            LOG.debug("File encoding: {}", inputStreamReader.getEncoding());

            String sCurrentLine;
            while ((sCurrentLine = bufferReader.readLine()) != null) {
                strBuilder.append(sCurrentLine);
                strBuilder.append(ls);
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new CustomException(e);
        }
        return strBuilder.toString();
    }

    public static File getFileFromURL(URL url) {
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            LOG.error("Can't find file by url", e);
            file = new File(url.getPath());
        }
        return file;
    }

    public static void writeMapsToCSV(String fileName, List<Map<String, Object>> arrayWithMaps) throws CustomException {
        if (arrayWithMaps == null || arrayWithMaps.isEmpty()) {
            return;
        }

        //Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.EXCEL.withDelimiter(';');
        try (
                FileWriter fileWriter = new FileWriter(fileName);
                CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
        ) {
            List header = arrayWithMaps.get(0).entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            csvFilePrinter.printRecord(header);
            for (Map<String, Object> recordMap : arrayWithMaps) {
                List recordList = Collections.list(Collections.enumeration(recordMap.values()));
                csvFilePrinter.printRecord(recordList);
            }
            LOG.debug("CSV file with events was created successfully!");
        } catch (Exception e) {
            LOG.error("Error in CsvFileWriter!", e);
            throw new CustomException(e);
        }
    }

}
