package ui.t1.tests.engine.vpc;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты")
public class BucketTest extends AbstractStorageTest {

    @Test
    @Order(1)
    @TmsLinks({@TmsLink("542274"), @TmsLink("542728")})
    @DisplayName("Бакет. Добавить бакет без версионирования")
    void addBucketWithoutVer() {
        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .checkBucketExistence(name, true)
                .deleteBucket(name)
                .checkBucketExistence(name, false);
    }

    @Test
    @Order(2)
    @TmsLink("542254")
    @DisplayName("Бакет. Добавить бакет с версионированием")
    void addBucketWithVer() {
        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .checkBucketExistence(name, true)
                .deleteBucket(name)
                .checkBucketExistence(name, false);
    }


}
