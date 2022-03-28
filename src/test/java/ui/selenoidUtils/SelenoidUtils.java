package ui.selenoidUtils;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.InputStream;
import java.net.URL;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static core.helper.Configure.getAppProp;

public class SelenoidUtils {

    private static boolean isRemote;

    @SneakyThrows
    public static void isRemote() {

        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote"))) {
            isRemote = true;
            System.out.println("Тесты стартовали на selenoid сервере: " + getAppProp("webdriver.remote.url"));
            Configuration.remote = getAppProp("webdriver.remote.url");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            if (Boolean.parseBoolean(getAppProp("video.enabled"))) {
                capabilities.setCapability("enableVNC", true);
                capabilities.setCapability("enableVideo", true);
            } else {
                capabilities.setCapability("enableVNC", false);
                capabilities.setCapability("enableVideo", false);
            }
            capabilities.setCapability("enableLog", true);
            capabilities.setCapability("logName", "lastSelenoid.log");
            Configuration.browserCapabilities = capabilities;
        } else {
            isRemote = false;
            System.out.println("Тесты стартовали локально");
        }
    }

    @SneakyThrows
    public static void deleteSelenoidVideo(URL url) {
        RestAssured.given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .delete(url)
                .then()
                .statusCode(200);
    }

    public String getSessionId() {
        return ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
    }

    public static InputStream getSelenoidVideo(URL url) {
        int tryCount = 0;
        InputStream video = null;
        while (tryCount != 20) {
            Response response = RestAssured.given()
                    .get(url);
            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                video = response.then().extract().asInputStream();
                break;
            } else {
                sleep(500);
            }
            tryCount++;
            if (tryCount == 20) {
                System.out.println("Selenoid video not found");
            }
        }
        return video;
    }

    @SneakyThrows
    public void isVideo(String sessionId) {
        if (Boolean.parseBoolean(getAppProp("video.enabled")) && isRemote) {
            System.out.println("Запись видео включена");
            attachAllureVideo(sessionId);
        } else {
            System.out.println("Запись видео выключена");
        }
    }

    @SneakyThrows
    public void attachAllureVideo(String sessionId) {
        URL videoUrl = new URL(getAppProp("webdriver.remote.url") + "/video/" + sessionId + ".mp4");
        InputStream is = getSelenoidVideo(videoUrl);
        try {
            Allure.addAttachment("Video", "video/mp4", is, "mp4");
        } finally {
            deleteSelenoidVideo(videoUrl);
        }
    }
}
