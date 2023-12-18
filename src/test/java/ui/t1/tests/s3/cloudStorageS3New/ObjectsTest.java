package ui.t1.tests.s3.cloudStorageS3New;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import steps.t1.s3_storage.S3StorageClient;
import steps.t1.s3_storage.S3StorageClientNew;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.Objects.AccessBucketLevel;
import ui.t1.tests.s3.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. Объекты")
@Story("Объектное хранилище S3")
public class ObjectsTest extends AbstractStorageTest {

    private static final S3StorageClient S_3_STORAGE_CLIENT_NEW = new S3StorageClientNew();

    @AfterAll
    public static void clearTestData() {
        if (!bucketsForDelete.isEmpty()) {
            bucketsForDelete.forEach(s3 ->S_3_STORAGE_CLIENT_NEW.deleteS3(s3.getName(), projectId));
        }
    }

    @Test
    @Order(1)
    @TmsLink("994567")
    @DisplayName("Объекты. Загрузить объект")
    void uploadObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true);
    }

    @Test
    @Order(2)
    @TmsLink("520456")
    @DisplayName("Объекты. Получить ссылку на объект")
    void getObjectLnk() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .getObjectLink("333.png");
    }

    @Test
    @Order(3)
    @TmsLink("520445")
    @DisplayName("Объекты. Переименовать объект")
    void renameObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .updateObjectName("333.png", "334.png")
                .checkObjectExists("333.png", false)
                .checkObjectExists("334.png", true);
    }

    @Test
    @Order(4)
    @TmsLink("520489")
    @DisplayName("Объекты. Восстановить удаленные объекты в бакете с версионированием")
    void restoreVersObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        S_3_STORAGE_CLIENT_NEW.addVersioningToBucketS3(name, projectId);
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .deleteObject("333.png")
                .checkObjectExists("333.png", false)
                .showHideDeleted(true)
                .checkObjectExists("333.png", true)
                .restoreObject("333.png")
                .showHideDeleted(false)
                .checkObjectExists("333.png", true);
    }

    @Test
    @Order(5)
    @TmsLink("675556")
    @DisplayName("Объекты. Восстановить удаленные объекты в бакете без версионирования")
    void restoreUnVersObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .deleteObject("333.png")
                .checkObjectExists("333.png", false)
                .showHideDeleted(true)
                .checkObjectExists("333.png", false);
    }

    @Test
    @Order(6)
    @TmsLink("520533")
    @DisplayName("Объекты. Открыть доступ")
    void openObjectPublicAccess() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .openObjectAccess("333.png");
    }

    @Test
    @Order(7)
    @TmsLink("520411")
    @DisplayName("Объекты. Скачать объект")
    void downloadObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .updateObjectName("333.png", "334.png")
                .checkObjectExists("334.png", true)
                .downloadObject("334.png");
    }

    @Test
    @Order(8)
    @TmsLink("994567")
    @DisplayName("Объекты. Удалить несколько объектов")
    void deleteObject() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObjects(AccessBucketLevel.OWNER_ONLY, "src/test/resources/s3files/333.png", "src/test/resources/s3files/444.png")
                .deleteObjects("333.png", "444.png");
    }
}

