package ui.t1.tests.cloudStorageS3;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.Objects.ObjectsModal.AccessLevel;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. Веб-сайт")
public class WebSiteTest extends AbstractStorageTest {

    @Test
    @Order(1)
    @TmsLink("542103")
    @DisplayName("Веб-сайт. Включение режима + Конечная точка")
    void websiteEndpoint() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)

                .gotoObjectsLayer()

                .uploadObject()
                .addObject("src/test/resources/s3files/index.html", AccessLevel.READFORALL)
                .checkObjectExists("index.html", true)

                .uploadObject()
                .addObject("src/test/resources/s3files/error.html", AccessLevel.READFORALL)
                .checkObjectExists("error.html", true)
                .gotoWebSiteLayerr()

                .setWebSiteMode(true);
        //TODO:
        //Добавить проверку на работоспособность ссылки(скорее всего послать api запрос) + узнать, как чекнуть серты

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(2)
    @TmsLink("542094")
    @DisplayName("Веб-сайт. Редактирование")
    void websiteEditing() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)

                .gotoObjectsLayer()

                .uploadObject()
                .addObjects( AccessLevel.READFORALL,
                        "src/test/resources/s3files/index.html",
                        "src/test/resources/s3files/index1.html",
                        "src/test/resources/s3files/error.html",
                        "src/test/resources/s3files/error1.html")
                .checkObjectExists("index.html", true)
                .checkObjectExists("index1.html", true)
                .checkObjectExists("error.html", true)
                .checkObjectExists("error1.html", true)

                .gotoWebSiteLayerr()

                .setWebSiteMode(true)
                .openWebSiteSettings()
                .setIndexName("index1.html")
                .setErrorName("error1.html")
                .saveEdit();
        //TODO:
        //Добавить проверку на работоспособность ссылки(скорее всего послать api запрос) + узнать, как чекнуть серты

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }


}

