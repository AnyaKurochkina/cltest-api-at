package ui.t1.tests.engine.vpc;

import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.CORS.CORSModal;
import ui.t1.pages.S3Storage.CORS.CORSModal.Method;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты")
public class BucketTest extends AbstractStorageTest {
    String name = getRandomBucketName();

    @Test
    @Order(1)
    @TmsLink("542274")
    @TmsLink("542728")
    @DisplayName("Бакет. Добавить бакет без версионирования")
    void addBucketWithoutVer() {
        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .deleteBucket(name);
    }

    @Test
    @Order(2)
    @TmsLink("542254")
    @DisplayName("Бакет. Добавить бакет с версионированием")
    void addBucketWithVer() {
        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .deleteBucket(name);
    }


}
