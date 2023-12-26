package core.utils;

import core.helper.Configure;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

import java.io.File;

public class DownloadingFilesUtil {

    public static final String DOWNLOADS_DIRECTORY_PATH = new File(Configure.RESOURCE_PATH + "/downloads").getAbsolutePath() + "/";

    /**
     * Пример использования: checkFileExistWithTimeout("src/sd/Audit_Logs_Table.csv", 2);
     */
    public static void checkFileExistsWithTimeout(File file, int seconds) {
        Assertions.assertTrue(FileUtils.waitFor(file, seconds),
                String.format("Файл по пути: %s, не найден", file.getAbsolutePath()));

    }

    /**
     * Пример использования: checkFileExistsInDownloadsDirectoryWithTimeout("Audit_Logs_Table.csv", 2);
     */
    @Step("[Проверка] Файл присутствует в папке src/test/resources/downloads с именем: {0}")
    public static void checkFileExistsInDownloadsDirectoryWithTimeout(String fileName, int seconds) {
        checkFileExistsWithTimeout(new File(DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH + fileName), seconds);
    }

    /**
     * Пример использования: checkFileExistsInDownloadsDirectory("Audit_Logs_Table.csv");
     */
    @Step("[Проверка] Файл присутствует в папке src/test/resources/downloads с именем: {0}")
    public static void checkFileExistsInDownloadsDirectory(String fileName) {
        checkFileExistsWithTimeout(new File(DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH + fileName), 5);
    }
}
