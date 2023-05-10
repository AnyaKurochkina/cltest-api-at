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
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal.LifeCycleRuleTypes;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal.LifeCycleConditionTriggers;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. ЖЦ")
public class LifeCycleTest extends AbstractStorageTest {
    String name = getRandomBucketName();


    @Test
    @Order(1)
    @TmsLink("1281945")
    @TmsLink("1281947")
    @DisplayName("Бакет. ЖЦ. Добавить")
    void addLifeCycle() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoLifeCycle()
                .addLifeCycle()
                .setName("Name")
                .setPrefix("Prefix")
                .setRuleType(LifeCycleRuleTypes.ABORTCOMPLETEMULTIPARTUPLOAD)
                .setDays("30");
//                .setConditionalTrigger(LifeCycleConditionTriggers.EXACTDATE);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }
}
