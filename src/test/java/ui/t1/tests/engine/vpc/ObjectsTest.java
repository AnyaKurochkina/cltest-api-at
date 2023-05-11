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
                .addObject("src/test/resources/s3files/333.png")
                .checkObjectExists("333.png", true);

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
                .checkObjectExists("333.png", true)
                .getObjectLink("333.png");

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(3)
    @TmsLink("520445")
    @DisplayName("Объекты. Переименовать объект")
    void renameObject() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png")
                .checkObjectExists("333.png", true)
                .updateObjectName("333.png", "334.png")
                .checkObjectExists("333.png", false)
                .checkObjectExists("334.png", true);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(4)
    @TmsLink("520489")
    @DisplayName("Объекты. Восстановить удаленные объекты в бакете с версионированием")
    void restoreVersObject() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png")
                .checkObjectExists("333.png", true)
                .deleteObject("333.png")
                .checkObjectExists("333.png", false)
                .showHideDeleted(true)
                .checkObjectExists("333.png", true)
                .restoreObject("333.png")
                .showHideDeleted(false)
                .checkObjectExists("333.png", true);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(5)
    @TmsLink("675556")
    @DisplayName("Объекты. Восстановить удаленные объекты в бакете без версионирования")
    void restoreUnVersObject() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png")
                .checkObjectExists("333.png", true)
                .deleteObject("333.png")
                .checkObjectExists("333.png", false)
                .showHideDeleted(true)
                .checkObjectExists("333.png", false);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(6)
    @TmsLink("520533")
    @DisplayName("Объекты. Открыть доступ")
    void openObjectPublicAccess() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("src/test/resources/s3files/333.png")
                .checkObjectExists("333.png", true)
                .openObjectAccess("333.png");

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
//                .addObjects("src/test/resources/s3files/333.png", "src/test/resources/s3files/444.png")
//                .deleteObjects("333.png", "444.png");
//
//        new IndexPage().goToS3CloudStoragePage()
//                .deleteBucket(name);
//
//    }

}

