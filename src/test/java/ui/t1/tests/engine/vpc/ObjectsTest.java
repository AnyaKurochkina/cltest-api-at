package ui.t1.tests.engine.vpc;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. Объекты")
public class ObjectsTest extends AbstractStorageTest {

    @Test
    @Order(1)
    @TmsLink("994567")
    @DisplayName("Объекты. Загрузить объект")
    void uploadObject() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png");

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(2)
    @TmsLink("520456")
    @DisplayName("Объекты. Получить ссылку на объект")
    void getObjectLnk() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png")
                .getObjectLink("333.png");

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }

//    @Test
//    @Order(2)
//    @TmsLink("994567")
//    @DisplayName("Объекты. Удалить несколько объектов")
//    void deleteObject() {
//
//        new IndexPage().goToS3CloudStoragePage()
//                .addBucket(name, true)
//                .createBucket()
//                .openBucket(name)
//                .gotoObjectsLayer()
//                .uploadObject()
//                .addObject("C:\\temp\\pa.MPQ")
//                .deleteObjects("333.png", "444.png");
//
//        new IndexPage().goToS3CloudStoragePage()
//                .deleteBucket(name);
//
//    }

}

