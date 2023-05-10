package ui.t1.tests.engine.vpc;

import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.CORS.CORSModal.AccessControls;
import ui.t1.pages.S3Storage.CORS.CORSModal.Method;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. CORS")
public class ObjectsTest extends AbstractStorageTest {
    String name = getRandomBucketName();


//    @Test
//    @Order(1)
//    @TmsLink("994567")
//    @DisplayName("Бакет. Объекты. Загрузить объект")
//    void uploadObject() {
//
//        new IndexPage().goToS3CloudStoragePage()
//                .addBucket(name, true)
//                .createBucket()
//                .openBucket(name)
//                .gotoObjectsLayer()
//                .uploadObject()
//                .addObject("C:\\temp\\333.png");
//
//        new IndexPage().goToS3CloudStoragePage()
//                .deleteBucket(name);
//
//    }

    @Test
    @Order(2)
    @TmsLink("994567")
    @DisplayName("Бакет. Объекты. Удалить несколько объектов")
    void deleteObject() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoObjectsLayer()
                .uploadObject()
                .addObject("C:\\temp\\pa.MPQ");
//                .deleteObjects("333.png", "444.png");

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }
}
