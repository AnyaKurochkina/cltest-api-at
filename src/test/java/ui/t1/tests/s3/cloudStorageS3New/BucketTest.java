package ui.t1.tests.s3.cloudStorageS3New;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.t1.s3_storage.S3StorageCreateResponse;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import steps.t1.s3_storage.S3StorageClient;
import steps.t1.s3_storage.S3StorageClientNew;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.Objects.AccessBucketLevel;
import ui.t1.tests.s3.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты")
public class BucketTest extends AbstractStorageTest {

    private static final S3StorageClient S_3_STORAGE_CLIENT_NEW = new S3StorageClientNew();
    @AfterAll
    public static void clearTestData() {
        if (!bucketsForDelete.isEmpty()) {
            bucketsForDelete.forEach(s3 ->S_3_STORAGE_CLIENT_NEW.deleteS3(s3.getName(), projectId));
        }
    }
    @Test
    @Order(1)
    @TmsLinks({@TmsLink("542274"), @TmsLink("542728")})
    @DisplayName("Бакет. Добавить бакет без версионирования")
    void addBucketWithoutVer() {
        bucketsForDelete.add(S3StorageCreateResponse.builder().name(name).build());
        new IndexPage().goToNewS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .checkBucketExistence(name, true);
    }

    @Test
    @Order(2)
    @TmsLink("542254")
    @DisplayName("Бакет. Добавить бакет с версионированием")
    void addBucketWithVer() {
        bucketsForDelete.add(S3StorageCreateResponse.builder().name(name).build());
        new IndexPage().goToNewS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .checkBucketExistence(name, true);
    }

    @Test
    @Order(3)
    @TmsLink("675620")
    @DisplayName("Бакет. Проверка уникальности имени бакета")
    void checkSameBuckets() {
        String newName = getRandomBucketName();
        new IndexPage().goToNewS3CloudStoragePage()
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
    @Order(4)
    @TmsLink("SOUL-118")
    @DisplayName("Бакет. ЖЦ. Незавершенные загрузки")
    void uploadObjectWithFailTest() {
        bucketsForDelete.add(S_3_STORAGE_CLIENT_NEW.createS3(name, projectId));
        String fileName = "big_file.rar";
        new IndexPage().goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoObjectsLayer()
                .clickUploadObject()
                .addObjectWithFail("src/test/resources/s3files/" + fileName, AccessBucketLevel.OWNER_ONLY, projectId)
                .goToNewS3CloudStoragePage()
                .openBucket(name)
                .gotoAccessIncompleteDownloadsLayer()
                .checkIncompleteDownloadFileIsAppear(fileName);
    }
}
