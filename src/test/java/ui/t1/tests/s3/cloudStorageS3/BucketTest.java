package ui.t1.tests.s3.cloudStorageS3;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
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
@Feature("Бакеты")
public class BucketTest extends AbstractStorageTest {

    private static final AbstractS3StorageClient<?> S3_STORAGE_CLIENT_OLD = new S3StorageClientOld();

    private final EntitySupplier<S3StorageCreateResponse> s3Bucket = lazy(() -> S3_STORAGE_CLIENT_OLD.createS3(name, projectId));

    @Test
    @Order(1)
    @TmsLinks({@TmsLink("542274"), @TmsLink("542728")})
    @DisplayName("Бакет. Добавить бакет без версионирования")
    void addBucketWithoutVer() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .checkBucketExistence(bucketName, true);
    }

    @Test
    @Order(2)
    @TmsLink("542254")
    @DisplayName("Бакет. Добавить бакет с версионированием")
    void addBucketWithVer() {
        String bucketName = s3Bucket.get().getName();
        new IndexPage().goToS3CloudStoragePage()
                .checkBucketExistence(bucketName, true);
    }

    @Test
    @Order(3)
    @TmsLink("675620")
    @DisplayName("Бакет. Проверка уникальности имени бакета")
    void checkSameBuckets() {
        String newName = getRandomBucketName();
        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .checkBucketExistence(name, true)
                .addBucket(name, true)
                .createSameBucket()
                .closeBucketForm()
                .addBucket(newName, true)
                .createBucket()
                .checkBucketExistence(newName, true)
                .deleteBucket(name)
                .deleteBucket(newName)
                .checkBucketExistence(name, false)
                .checkBucketExistence(newName, false);
    }

    @Test
    @Tag("morozov_ilya")
    @Order(4)
    @TmsLink("SOUL-118")
    @DisplayName("Бакет. ЖЦ. Незавершенные загрузки")
    void uploadObjectWithFailTest() {
        String bucketName = s3Bucket.get().getName();
        String fileName = "big_file.rar";
        new IndexPage().goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObjectWithFail("src/test/resources/s3files/" + fileName, AccessBucketLevel.OWNER_ONLY, projectId)
                .goToS3CloudStoragePage()
                .openBucket(bucketName)
                .gotoAccessIncompleteDownloadsLayer()
                .checkIncompleteDownloadFileIsAppear(fileName);
    }
}
