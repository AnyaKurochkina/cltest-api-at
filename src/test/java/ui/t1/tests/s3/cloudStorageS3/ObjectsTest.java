package ui.t1.tests.s3.cloudStorageS3;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.t1.s3_storage.S3StorageCreateResponse;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import steps.t1.s3_storage.AbstractS3StorageClient;
import steps.t1.s3_storage.S3StorageClientOld;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.Objects.AccessBucketLevel;
import ui.t1.tests.engine.EntitySupplier;
import ui.t1.tests.s3.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. Объекты")
@Story("Хранилище S3")
public class ObjectsTest extends AbstractStorageTest {

    private static final AbstractS3StorageClient<?> S3_STORAGE_CLIENT_OLD = new S3StorageClientOld();

    private final EntitySupplier<S3StorageCreateResponse> s3Bucket = lazy(() -> S3_STORAGE_CLIENT_OLD.createS3(name, projectId));
    private final EntitySupplier<S3StorageCreateResponse> s3BucketWithVersioning = lazy(() -> {
        S3StorageCreateResponse s3StorageCreateResponse = S3_STORAGE_CLIENT_OLD.createS3(name, projectId);
        S3_STORAGE_CLIENT_OLD.addVersioningToBucketS3(name, projectId);
        return s3StorageCreateResponse;
    });

    @Test
    @Order(1)
    @TmsLink("994567")
    @DisplayName("Загрузить объект")
    void uploadObject() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true);
    }

    @Test
    @Order(2)
    @TmsLink("520456")
    @DisplayName("Получить ссылку на объект")
    void getObjectLnk() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .getObjectLink("333.png");
    }

    @Test
    @Order(3)
    @TmsLink("520445")
    @DisplayName("Переименовать объект")
    void renameObject() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
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
    @DisplayName("Восстановить удаленные объекты в бакете с версионированием")
    void restoreVersObject() {
        String bucketName = s3BucketWithVersioning.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
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
    @DisplayName("Восстановить удаленные объекты в бакете без версионирования")
    void restoreUnVersObject() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
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
    @DisplayName("Открыть доступ")
    void openObjectPublicAccess() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .checkObjectExists("333.png", true)
                .openObjectAccess("333.png");
    }

    @Test
    @Order(7)
    @TmsLink("520411")
    @DisplayName("Скачать объект")
    void downloadObject() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObject("src/test/resources/s3files/333.png", AccessBucketLevel.OWNER_ONLY)
                .updateObjectName("333.png", "335.png")
                .checkObjectExists("335.png", true)
                .downloadObject("335.png");
    }

    @Test
    @Order(8)
    @TmsLink("994567")
    @DisplayName("Удалить несколько объектов")
    void deleteObject() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                    .gotoObjectsLayer()
                    .clickUploadObject()
                    .addObjects(AccessBucketLevel.OWNER_ONLY, "src/test/resources/s3files/333.png", "src/test/resources/s3files/444.png")
                    .deleteObjects("333.png", "444.png");
    }
}

