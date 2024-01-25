package core.utils;

import com.codeborne.selenide.Selenide;
import core.helper.AttachUtils;
import core.helper.Configure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.junit.TestsExecutionListener;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import ui.extesions.ConfigExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.sleep;

@Log4j2
public class DownloadingFilesUtil {

    public static final String DOWNLOADS_DIRECTORY_PATH = Configure.RESOURCE_PATH + "/downloads/";

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
    public static void checkFileExistsInDownloadsDirectory(String fileName, int seconds) {
        downloadFileFromSelenoidIfIsRemote(fileName);
        checkFileExistsWithTimeout(new File(DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH + fileName), seconds);
    }

    /**
     * Пример использования: checkFileExistsInDownloadsDirectory("Audit_Logs_Table.csv");
     */
    @Step("[Проверка] Файл присутствует в папке src/test/resources/downloads с именем: {0}")
    public static void checkFileExistsInDownloadsDirectory(String fileName) {
        downloadFileFromSelenoidIfIsRemote(fileName);
        checkFileExistsWithTimeout(new File(DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH + fileName), 5);
    }

    /**
     * Данный метод получает имя последнего скачанного файла
     * Заходит в загрузки хрома chrome://downloads/ и сохраняет оттуда имя файла
     *
     * @return filename
     */
    @SneakyThrows
    public static String getLastDownloadedFilename() {
        String[] filename = new String[1];
        Exception[] ex = new Exception[1];
        Selenide.executeJavaScript("window.open('','_blank');");
        Selenide.switchTo().window(1);
        Selenide.open("chrome://downloads/");

        for (int i = 0; i < (long) 5; i++) {
            ex[0] = null;
            sleep(2000);
            try {
                WebElement element = Selenide.executeJavaScript(
                        "return document.querySelector('downloads-manager').shadowRoot.querySelector('downloads-item').shadowRoot.getElementById('file-link')");
                filename[0] = Objects.requireNonNull(element, "Элемент file-link не найден").getText();
                log.info("Attempt get file name " + i + ". Name = '" + filename[0] + "'");
            } catch (WebDriverException e) {
                ex[0] = e;
                log.info("Failed attempt " + i + " to get filename text: " + e.getMessage());
                continue;
            }
            if (filename[0] != null && !filename[0].isEmpty()) {
                // здесь мы удаляем ссылку на файл со страницы чтобы она не мешала потом
                // Локатор локально будет отличаться ввиду новой версии хрома
                String locatorName = TestsExecutionListener.isRemote() ? "remove" : "remove-old";
                Selenide.executeJavaScript(
                        String.format("document.querySelector('downloads-manager').shadowRoot.querySelector('downloads-item').shadowRoot.getElementById('%s').click()", locatorName));
                break;
            }
        }

        if (filename[0] != null && !filename[0].isEmpty()) {
            Selenide.closeWindow();
            Selenide.switchTo().window(0);
            return filename[0];
        }
        Selenide.closeWindow();
        Selenide.switchTo().window(0);
        String message = "Timeout. Can not get last downloaded file name from chrome://downloads/. File name is '" + filename[0] + "'. Exception: " + ex[0].getMessage();
        log.info(message);
        throw new AssertionError(message, ex[0]);
    }

    /**
     * Данный метод сохраняет файл по имени из селеноид сервера в проект в папку downloads
     * которая будет отчищена после завершения тестов
     *
     * @param fileName
     */
    private static void downloadFileFromSelenoidIfIsRemote(String fileName) {
        if (TestsExecutionListener.isRemote()) {
            downloadFileFromSelenoid(fileName);
        }
    }

    /**
     * Данный метод сохраняет файл по имени из селеноид сервера в проект в папку downloads
     *
     * @param fileName
     */
    @SneakyThrows
    private static void downloadFileFromSelenoid(String fileName) {
        String fileUrl = AttachUtils.getUrlToDownloadedFileFromSelenoid(fileName);
        byte[] file = RestAssured.given()
                .get(fileUrl).then().extract().asByteArray();
        Files.createDirectories(Paths.get(DOWNLOADS_DIRECTORY_PATH));
        Path path = Paths.get(DOWNLOADS_DIRECTORY_PATH + fileName);
        Files.write(path, file);
        ConfigExtension.fileUrlsForDeleteFromSelenoid.add(fileUrl);
    }
}
